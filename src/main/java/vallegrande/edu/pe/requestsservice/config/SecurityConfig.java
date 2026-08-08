package vallegrande.edu.pe.requestsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad con Keycloak como Identity Provider.
 * El microservicio actúa como Resource Server y valida los JWT
 * emitidos por Keycloak en cada petición entrante.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Swagger y actuator públicos
                        .pathMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // Endpoints públicos de tipos (sacramentos y misas)
                        .pathMatchers(HttpMethod.GET, "/v1/api/requests/types/**").permitAll()

                        // Solo SECRETARIO puede crear, editar y eliminar solicitudes
                        .pathMatchers(HttpMethod.POST, "/v1/api/requests").hasRole("SECRETARIO")
                        .pathMatchers(HttpMethod.PUT, "/v1/api/requests/**").hasRole("SECRETARIO")
                        .pathMatchers(HttpMethod.DELETE, "/v1/api/requests/**").hasRole("SECRETARIO")
                        .pathMatchers(HttpMethod.PATCH, "/v1/api/requests/*/priority").hasRole("SECRETARIO")

                        // Solo PARROCO puede aprobar/rechazar
                        .pathMatchers(HttpMethod.PATCH, "/v1/api/requests/*/status").hasRole("PARROCO")

                        // Listar y ver detalle: ambos roles autenticados
                        .pathMatchers(HttpMethod.GET, "/v1/api/requests/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter()))
                )
                .build();
    }

    /**
     * Convierte los roles de Keycloak (realm_access.roles) al formato
     * que Spring Security entiende (ROLE_SECRETARIO, ROLE_PARROCO).
     */
    @Bean
    public ReactiveJwtAuthenticationConverter keycloakJwtConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(new KeycloakRoleConverter())
        );
        return converter;
    }
}
