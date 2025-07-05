package com.diver.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que se ejecuta una vez por cada petición para validar un token JWT.
 * <p>
 * Su responsabilidad es interceptar la cabecera 'Authorization', extraer el token,
 * validarlo y, si es válido, establecer la autenticación del usuario en el
 * {@link SecurityContextHolder}. Esto permite que el resto de la aplicación,
 * incluyendo los controladores y la seguridad a nivel de método, reconozcan al
 * usuario como autenticado.
 *
 * @author Tu Nombre (o el nombre del equipo)
 * @version 1.2
 * @since 2024-06-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Lógica principal del filtro para procesar la autenticación JWT.
     *
     * @param request     La solicitud HTTP entrante.
     * @param response    La respuesta HTTP que se enviará.
     * @param filterChain El objeto que permite invocar al siguiente filtro en la cadena.
     * @throws ServletException si ocurre un error de servlet.
     * @throws IOException      si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractJwtFromRequest(request);

        // Si no hay token, simplemente continuamos con la cadena de filtros.
        // Otros filtros de Spring Security manejarán el caso de un endpoint protegido sin token.
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Delegamos la validación y extracción de claims al JwtProvider.
            Claims claims = jwtProvider.getClaimsFromToken(jwt);
            String email = claims.getSubject();

            // Verificamos que el email exista y que no haya ya una autenticación en el contexto.
            // Esto último evita trabajo innecesario en peticiones que ya han sido autenticadas.
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargamos el objeto UserDetails completo desde la base de datos.
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                // Creamos el objeto Authentication, poniendo el objeto UserDetails como el "Principal".
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,         // ¡El Principal es ahora el objeto User completo!
                        null,                // No se necesitan credenciales (password) en la autenticación por token.
                        userDetails.getAuthorities() // Los roles/permisos del usuario.
                );

                // Establecemos la autenticación en el contexto de seguridad.
                // A partir de este punto, el usuario está "logueado" para esta petición.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuario '{}' autenticado exitosamente a través de JWT.", email);
            }
        } catch (Exception e) {
            // Si ocurre cualquier error durante la validación del token (expirado, malformado, etc.),
            // lo registramos en el log y limpiamos el contexto de seguridad por si contenía
            // datos de autenticación parciales o inválidos.
            log.error("Error al procesar el token JWT. Causa: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // Continuamos con el siguiente filtro en la cadena, sea cual sea el resultado.
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera 'Authorization' de la solicitud.
     *
     * @param request La solicitud HTTP.
     * @return El token JWT como un String, o null si la cabecera no existe o no tiene el formato "Bearer ".
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstant.JWT_HEADER);
        // Usamos StringUtils de Spring para verificar de forma segura que el token no es nulo y tiene el prefijo.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Extrae solo el token, sin "Bearer ".
        }
        return null;
    }
}