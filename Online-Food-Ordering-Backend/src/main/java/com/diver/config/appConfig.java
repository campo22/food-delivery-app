package com.diver.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Clase de configuración principal de seguridad de la aplicación.
 * Define la política de autenticación/autorización, control de sesiones,
 * configuración CORS y filtros personalizados como el de validación de JWT.
 */
@Configuration
@EnableWebSecurity // Habilita la seguridad web en la aplicación (equivalente a WebSecurityConfigurerAdapter)
@EnableMethodSecurity   // Habilita la seguridad a nivel de metodo (para @PreAuthorize
@AllArgsConstructor
public class appConfig {


    // MEJORA: Inyectamos nuestro filtro como un bean de Spring.
    // Spring se encargará de crearlo y de pasarle el UserDetailsService que necesita.
    private final JwtTokenValidator jwtTokenValidator;
    /**
     * Define la cadena de filtros de seguridad de Spring (SecurityFilterChain).
     * Este método reemplaza el uso de WebSecurityConfigurerAdapter en Spring Security moderno.
     *
     * @param http objeto de configuración de seguridad HTTP proporcionado por Spring
     * @return un SecurityFilterChain con todas las reglas de seguridad aplicadas
     * @throws Exception si ocurre algún error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔒 Gestión de sesión
                .sessionManagement(management ->
                                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // → Define que la app no mantendrá sesiones de usuario en el servidor.
                        //    Cada request debe autenticarse con su propio token (JWT).
                )

                // 🔐 Autorización de peticiones HTTP
                // EN SecurityConfig.java
                .authorizeHttpRequests(auth -> auth

                        // --- REGLAS PÚBLICAS (Las más específicas primero) ---
                        // Permite el acceso sin autenticación a todos los endpoints de autenticación.
                        .requestMatchers("/auth/**").permitAll()

                        // Permite el acceso sin autenticación a la documentación de la API.
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Permite el acceso PÚBLICO a los endpoints de consulta de comida.
                        .requestMatchers(HttpMethod.GET, "/api/food/**").permitAll()

                        // Permite el acceso PÚBLICO a los endpoints de consulta de restaurantes.
                        .requestMatchers(HttpMethod.GET, "/api/restaurant", "/api/restaurant/search", "/api/restaurant/restaurant/{restaurantId}").permitAll()

                        // --- REGLAS DE ADMINISTRACIÓN ---
                        // Protege todas las rutas de administración, la autorización final se delega a @PreAuthorize.
                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")

                        // --- REGLA GENERAL PARA EL RESTO DE LA API ---
                        // Cualquier otra petición bajo /api/ (que no haya coincidido antes) requiere autenticación.
                        // Esto protege endpoints como /api/restaurant/{id}/toggle-favorite o /api/users/profile
                        .requestMatchers("/api/**").authenticated()

                        // --- REGLA POR DEFECTO ---
                        // Si alguna ruta no coincidió con nada anterior (ej: la raíz "/"), se permite.
                        .anyRequest().permitAll()
                )

                // 🔄 Filtro de validación de token JWT personalizado
                .addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class)
                // → Coloca tu filtro JWT antes del filtro de autenticación básica.

                // 🚫 Deshabilita protección CSRF
                .csrf(csrf -> csrf.disable())
                // → No es necesaria si la autenticación es con token (no con formularios y cookies)

                // 🌐 Configura la política CORS personalizada
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // → Permite que tu frontend (en otro dominio) acceda al backend

        return http.build(); // Finaliza y devuelve la configuración
    }

    /**
     * Define la política de CORS (Cross-Origin Resource Sharing) para permitir
     * que el frontend (React, Angular, etc.) pueda acceder al backend Spring.
     *
     * @return CorsConfigurationSource configurado con orígenes, headers y métodos permitidos
     */
    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();

                // 🌍 Orígenes permitidos para acceder al backend
                cfg.setAllowedOrigins(Arrays.asList(
                        "https://diver-food.vercel.app",  // Producción
                        "http://localhost:3000"           // Desarrollo local (React)
                ));

                // ✅ Métodos HTTP permitidos (todos)
                // e.g. GET, POST, PUT, DELETE
                cfg.setAllowedMethods(Collections.singletonList("*"));

                // 🔐 Permite envío de cookies y cabeceras como Authorization
                cfg.setAllowCredentials(true);

                // ✅ Cabeceras permitidas en la solicitud (todas)
                // e.g. Authorization, Content-Type,
                cfg.setAllowedHeaders(Collections.singletonList("*"));

                // 👁️ Cabeceras visibles en la respuesta (Authorization, etc.)
                cfg.setExposedHeaders(Arrays.asList("Authorization"));

                // 🕒 Cache de configuración preflight (pre-autorización) por 1 hora
                cfg.setMaxAge(3600L);

                return cfg;
            }
        };

    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
