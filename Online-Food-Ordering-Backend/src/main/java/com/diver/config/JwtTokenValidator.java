package com.diver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ✅ Filtro personalizado que intercepta cada solicitud HTTP para validar el token JWT.
 * <p>
 * ✅ Este filtro:
 * - Extrae el token JWT desde el header "Authorization".
 * - Valida la firma y la expiración del token.
 * - Extrae los claims del token (email y roles).
 * - Establece la autenticación en el contexto de Spring Security.
 */
public class JwtTokenValidator extends OncePerRequestFilter {

    /**
     * 🔁 Este método se ejecuta una sola vez por cada solicitud entrante.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se devolverá.
     * @param filterChain La cadena de filtros de seguridad.
     *
     * @throws ServletException Si ocurre un error al ejecutar el filtro.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ✅ 1. Obtener el token JWT desde la cabecera Authorization
        String jwt = request.getHeader(JwtConstant.JWT_HEADER); // ejemplo: "Bearer eyJhbGciOi..."

        // 🔐 2. Verificar que el header exista y comience con "Bearer "
        if (jwt != null && jwt.startsWith("Bearer ")) {
            try {
                // ✂️ 3. Eliminar el prefijo "Bearer " (7 caracteres)
                jwt = jwt.substring(7); // ahora solo contiene el token JWT puro

                // 🔑 4. Generar la clave secreta para verificar la firma del JWT
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                // 🧾 5. Validar y parsear el JWT
                Claims claims = Jwts.parserBuilder()// Construye un parser de JWT
                        .setSigningKey(key)      // Establece la clave para validar la firma
                        .build()                 // Construye el parser
                        .parseClaimsJws(jwt)     // Valida firma, formato y expiración
                        .getBody();              // Obtiene los claims (payload) del token

                // 📧 6. Extraer información útil del token
                String email = claims.get("email", String.class);             // Identificador del usuario
                String authorities = claims.get("authorities", String.class); // Ejemplo: "ROLE_USER,ROLE_ADMIN"

                // 🔓 7. Convertir los roles en una lista de objetos GrantedAuthority
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                // 🔐 8. Crear un objeto Authentication con email y roles
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        email, // Principal (usuario autenticado)
                        null,  // Credenciales (null porque no lo necesitamos en JWT)
                        auth   // Lista de roles/autorizaciones
                );

                // ✅ 9. Registrar la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // ❌ Si el token está mal formado, expirado o alterado
                throw new RuntimeException("❌ Token JWT inválido o expirado", e);
            }
        }

        // 🔄 10. Continuar con la cadena de filtros (siempre debe llamarse)
        filterChain.doFilter(request, response);
    }
}
