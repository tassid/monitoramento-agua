package monitoramento.agua.demo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injete o seu clientId diretamente aqui
    @Value("${aws.cognito.clientId}")
    private String clientId;

    // Injete o issuer uri para usar no validador
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                // 1. REGRAS DE LIBERAÇÃO (PÚBLICO)
                // Permite acesso total à documentação do Swagger e ao seu endpoint de login.
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/login/**" // Libera todos os endpoints dentro do AuthController
                ).permitAll()
                // 2. REGRAS DE PROTEÇÃO (EXIGE TOKEN)
                // Protege todas as rotas que começam com /api/
                .requestMatchers("/api/**").authenticated()
                // 3. REGRA GERAL (BOA PRÁTICA)
                // Qualquer outra requisição que não se encaixe nas regras acima,
                // por padrão, também exigirá autenticação.
                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // A mágica acontece aqui: configuramos o jwt() com um decoder customizado
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    // Este Bean cria o nosso decodificador de JWT customizado
    @Bean
    JwtDecoder jwtDecoder() {
        // Criamos o decodificador padrão que valida a assinatura usando a URL do JWKS
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // Criamos um validador para o Issuer (quem emitiu o token)
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);

        // **AQUI ESTÁ A SOLUÇÃO:** Criamos nosso próprio validador para a "audiência"
        // Ele vai verificar a claim "client_id" em vez de "aud"
        OAuth2TokenValidator<Jwt> audienceValidator = token -> {
            if (token.getClaimAsString("client_id").equals(clientId)) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "The client_id claim is not valid", null));
            }
        };

        // Juntamos os dois validadores
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);

        // Configuramos o decodificador para usar nosso validador customizado
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }
}
