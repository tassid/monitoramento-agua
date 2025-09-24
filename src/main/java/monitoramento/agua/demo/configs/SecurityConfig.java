package monitoramento.agua.demo.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Proteger endpoints
                .authorizeHttpRequests(authorize -> authorize
                // Protege todas as rotas em /api/, exigindo um usuário autenticado
                .requestMatchers("/api/**").authenticated()
                // Permite acesso a outras rotas (se houver, como /public)
                .anyRequest().permitAll()
                )
                // 2. Configurar a validação do token JWT
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // 3. Tornar a API stateless (não cria sessões HTTP)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. (Opcional) Desabilitar CSRF, comum para APIs stateless
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
