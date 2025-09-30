package monitoramento.agua.demo.Controllers;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import monitoramento.agua.demo.dtos.AuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

@RestController
@RequestMapping("/login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final CognitoIdentityProviderClient cognitoClient;

    @Autowired
    public AuthController(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

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

    @PostMapping
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", authRequest.getUsername());
            authParams.put("PASSWORD", authRequest.getPassword());

            // Só calcula e adiciona o hash se o clientSecret existir
            if (clientSecret != null && !clientSecret.isEmpty()) {
                try {
                    authParams.put("SECRET_HASH", calculateSecretHash(authRequest.getUsername()));
                } catch (Exception ex) {
                    logger.error("Erro ao calcular o SECRET_HASH: {}", ex.getMessage());
                    return ResponseEntity.status(500).body(Map.of("error", "Erro ao calcular o SECRET_HASH."));
                }
            }

            InitiateAuthRequest authReq = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authReq);
            AuthenticationResultType result = response.authenticationResult();

            // Retorna os tokens para o cliente
            return ResponseEntity.ok(Map.of(
                    "idToken", result.idToken(),
                    "accessToken", result.accessToken(),
                    "refreshToken", result.refreshToken(),
                    "expiresIn", result.expiresIn()
            ));

        } catch (NotAuthorizedException e) {
            logger.warn("Tentativa de login falhou para o usuário {}: {}", authRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas."));
        } catch (UserNotFoundException e) {
            logger.warn("Tentativa de login para usuário inexistente: {}", authRequest.getUsername());
            return ResponseEntity.status(404).body(Map.of("error", "Usuário não encontrado."));
        } catch (CognitoIdentityProviderException e) {
            logger.error("Erro do Cognito durante o login: {}", e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.awsErrorDetails().errorMessage()));
        }
    }

    // MUDANÇA 4: Método de refresh refatorado para usar o AWS SDK
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        String username = payload.get("username"); // Necessário para o secret hash se o cliente for confidencial

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "refresh_token_required"));
        }

        try {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("REFRESH_TOKEN", refreshToken);

            if (clientSecret != null && !clientSecret.isEmpty()) {
                if (username == null || username.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "username_required_for_secret_hash"));
                }
                try {
                    authParams.put("SECRET_HASH", calculateSecretHash(username));
                } catch (Exception ex) {
                    logger.error("Erro ao calcular o SECRET_HASH: {}", ex.getMessage());
                    return ResponseEntity.status(500).body(Map.of("error", "Erro ao calcular o SECRET_HASH."));
                }
            }

            InitiateAuthRequest authReq = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authReq);
            AuthenticationResultType result = response.authenticationResult();

            return ResponseEntity.ok(Map.of(
                    "idToken", result.idToken(),
                    "accessToken", result.accessToken(),
                    "expiresIn", result.expiresIn()
            ));
        } catch (NotAuthorizedException e) {
            logger.warn("Falha no refresh token: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token inválido ou expirado."));
        } catch (CognitoIdentityProviderException e) {
            logger.error("Erro do Cognito durante o refresh: {}", e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.awsErrorDetails().errorMessage()));
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
