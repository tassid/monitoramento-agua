package monitoramento.agua.demo.Controllers;

import java.lang.System.Logger;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;

// import jakarta.websocket.Decoder; // Removed unnecessary import

import com.auth0.jwt.interfaces.JWTVerifier;

import monitoramento.agua.demo.dtos.AuthRequest;

@RestController
@RequestMapping("/login")
public class AuthController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Value("${aws.cognito.url}")
    private String cognitoUrl;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;

    @Value("${aws.cognito.region}")
    private String awsRegion;

    private final Map<String, PublicKey> publicKeyCache = new HashMap<>();

    private final RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String secretHash = calculateSecretHash(authRequest.getUsername());

            Map<String, Object> authParams = new HashMap<>();
            authParams.put("USERNAME", authRequest.getUsername());
            authParams.put("PASSWORD", authRequest.getPassword());
            authParams.put("SECRET_HASH", secretHash);

            Map<String, Object> payload = new HashMap<>();
            payload.put("AuthFlow", "USER_PASSWORD_AUTH");
            payload.put("ClientId", clientId);
            payload.put("AuthParameters", authParams);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-amz-json-1.1");
            headers.set("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");

            // Serializa o payload como JSON
            String body = objectMapper.writeValueAsString(payload);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            System.out.println("URL: " + cognitoUrl);
            System.out.println("Payload: " + payload);
            System.out.println("Headers: " + headers);
            System.out.println("Entity: " + request);

            ResponseEntity<String> response = restTemplate.postForEntity(cognitoUrl, request, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {

                DecodedJWT jwt = JWT.decode(token);

                String keyId = jwt.getKeyId(); // Ou jwt.getHeaderClaim("kid").asString();
                PublicKey publicKey = getPublicKey(keyId);

                if (publicKey != null && publicKey instanceof RSAPublicKey) {
                    // Agora, verifica a assinatura
                    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
                    JWTVerifier verifier = JWT.require(algorithm)
                            .withIssuer(String.format("https://cognito-idp.%s.amazonaws.com/%s", awsRegion, userPoolId)) // Adicione seu aws.cognito.region aqui
                            .build();

                    verifier.verify(token); // Esta linha lança uma exceção se a verificação falhar

                    Map<String, Object> claims = new HashMap<>();
                    jwt.getClaims().forEach((k, v) -> claims.put(k, v.as(Object.class)));
                    return ResponseEntity.ok(Map.of("valid", true, "claims", claims));
                } else {
                    logger.error("Chave pública não encontrada ou não é do tipo RSA para o kid: {}", keyId);
                }

            } catch (JWTVerificationException e) {
                logger.error("Token JWT do Cognito inválido: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Erro ao validar token do Cognito: {}", e.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "message", "Token inválido"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        String username = payload.get("username");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "refresh_token_required"));
        }

        try {
            logger.info("Tentando refresh token: {}", refreshToken);
            logger.info("Usando ClientId: {}", clientId);
            logger.info("Usando ClientSecret: {}", clientSecret);
            logger.info("Usando username: {}", username);
            String secretHash = calculateSecretHash(username);
            logger.info("SecretHash calculado para refresh: {}", secretHash);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-amz-json-1.1");
            headers.set("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");

            Map<String, String> authParams = new HashMap<>();
            authParams.put("REFRESH_TOKEN", refreshToken);
            authParams.put("SECRET_HASH", secretHash);

            Map<String, Object> body = new HashMap<>();
            body.put("AuthFlow", "REFRESH_TOKEN_AUTH");
            body.put("ClientId", clientId);
            body.put("AuthParameters", authParams);

            String jsonBody = objectMapper.writeValueAsString(body); // Converter o body para String JSON

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers); // Usar String como corpo

            ResponseEntity<JsonNode> response = restTemplate.exchange(cognitoUrl,
                    HttpMethod.POST,
                    request,
                    JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode authResultNode = response.getBody().get("AuthenticationResult");
                if (authResultNode != null && authResultNode.has("IdToken") && authResultNode.has("AccessToken")) {
                    Map<String, String> refreshedTokens = new HashMap<>();
                    refreshedTokens.put("idToken", authResultNode.get("IdToken").asText());
                    refreshedTokens.put("accessToken", authResultNode.get("AccessToken").asText());
                    // O Refresh Token geralmente não é retornado na resposta de refresh.
                    // O cliente deve armazenar o Refresh Token original.
                    return ResponseEntity.ok(refreshedTokens);
                } else {
                    logger.error("Resposta de refresh token inesperada: {}", response.getBody());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "invalid_refresh_token_response"));
                }
            } else {
                logger.error("Erro ao renovar token. Status: {}, Corpo: {}", response.getStatusCode(),
                        response.getBody());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_refresh_token"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Erro ao processar a renovação do token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "refresh_token_processing_error", "details", e.getMessage()));
        }
    }

    private PublicKey getPublicKey(String keyId) {
        if (publicKeyCache.containsKey(keyId)) {
            return publicKeyCache.get(keyId);
        }

        try {
            String jwksUrl = String.format("%s/%s/.well-known/jwks.json", cognitoUrl, userPoolId);
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(jwksUrl, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode keys = response.getBody().get("keys");
                if (keys != null && keys.isArray()) {
                    for (JsonNode key : keys) {
                        if (key.has("kid") && key.get("kid").asText().equals(keyId)) {
                            String nStr = key.get("n").asText();
                            String eStr = key.get("e").asText();

                            byte[] nBytes = Base64.getUrlDecoder().decode(nStr);
                            byte[] eBytes = Base64.getUrlDecoder().decode(eStr);

                            java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(
                                    new java.math.BigInteger(1, nBytes), new java.math.BigInteger(1, eBytes));
                            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
                            PublicKey publicKey = keyFactory.generatePublic(spec);
                            publicKeyCache.put(keyId, publicKey);
                            return publicKey;
                        }
                    }
                }
            }
            logger.error("Chave pública não encontrada para kid: {}", keyId);

        } catch (Exception e) {
            logger.error("Erro ao buscar chaves públicas do Cognito: {}", e.getMessage());
        }
        return null;
    }

    private String calculateSecretHash(String input) throws Exception {
        String message = input + clientId;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
