package com.diver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // MEJORA: Importación clave
import org.springframework.security.core.userdetails.UserDetailsService; // MEJORA: Importación clave
import org.springframework.stereotype.Component; // MEJORA: Lo convertimos en un bean de Spring
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filtro que intercepta cada solicitud para validar el token JWT y establecer
 * la autenticación del usuario en el contexto de seguridad de Spring.
 * <p>
 * Este filtro se ejecuta una vez por petición, valida el token y, si es correcto,
 * carga los detalles completos del usuario para que estén disponibles en toda la aplicación.
 */
@Slf4j
@Component // MEJORA: Anotamos la clase como un componente para que Spring la gestione.
@RequiredArgsConstructor // MEJORA: Usamos Lombok para inyectar dependencias por constructor.
public class JwtTokenValidator extends OncePerRequestFilter {

    // MEJORA: Inyectamos el servicio que sabe cómo buscar usuarios.
    private final UserDetailsService userDetailsService;

    /**
     * Procesa cada solicitud HTTP para validar el JWT y establecer la autenticación.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);

            try {
                // Parseamos el token para obtener el email del usuario.
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = claims.get("email", String.class);

                // --- ¡EL CAMBIO FUNDAMENTAL ESTÁ AQUÍ! ---
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 1. Cargamos el objeto UserDetails COMPLETO desde la base de datos.
                    // Tu implementación de UserDetailsService (CustomUserDetailsService) hará el trabajo.
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                    // 2. Creamos el objeto Authentication, poniendo el objeto UserDetails como el "Principal".
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, // ¡AHORA EL PRINCIPAL ES EL OBJETO User COMPLETO!
                            null,
                            userDetails.getAuthorities() // Los roles ya vienen dentro del objeto UserDetails.
                    );

                    // 3. Establecemos la autenticación en el contexto de seguridad.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Usuario '{}' autenticado exitosamente.", email);
                }

            } catch (Exception e) {
                // Es mejor loguear el error que lanzar una excepción genérica que podría ser ocultada.
                log.error("Error al validar el token JWT: {}", e.getMessage());
                // Puedes limpiar el contexto por si acaso.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}