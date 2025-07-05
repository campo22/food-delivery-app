package com.diver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio responsable de la generación y validación de tokens JWT (JSON Web Token).
 * <p>
 * Esta clase centraliza la lógica para crear nuevos tokens para usuarios autenticados
 * y para extraer información de los tokens existentes. Utiliza una clave secreta
 * para firmar y verificar los tokens, asegurando su integridad y autenticidad.
 *
 * @author Tu Nombre (o el nombre del equipo)
 * @version 1.1
 * @since 2024-06-27
 */
@Service
public class JwtProvider {

    /**
     * Clave secreta utilizada para firmar y verificar los tokens JWT.
     * Se genera una sola vez al instanciar la clase para garantizar la consistencia.
     */
    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * Genera un nuevo token JWT para un usuario que ha sido autenticado exitosamente.
     * <p>
     * El token incluye el email del usuario como el "subject" y sus roles/permisos
     * en un claim personalizado llamado "authorities", siguiendo las convenciones de Spring Security.
     *
     * @param auth El objeto {@link Authentication} proporcionado por Spring Security tras un login exitoso.
     * @return Un String que representa el token JWT compacto y firmado.
     */
    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String authoritiesString = populateAuthorities(authorities);

        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + JwtConstant.EXPIRATION_TIME))
                // El "subject" (sub) es el claim estándar para el identificador del principal.
                .setSubject(auth.getName())
                // Claim personalizado para las autoridades, siguiendo la convención de Spring.
                .claim("authorities", authoritiesString)
                .signWith(key)
                .compact();

        return token;
    }

    /**
     * Extrae el email (subject) de un token JWT.
     * <p>
     * Este método no valida el token, asume que la validación ya ha sido realizada.
     * Su propósito es obtener los claims después de una validación exitosa.
     *
     * @param jwt El token JWT completo (sin el prefijo "Bearer ").
     * @return El email del usuario contenido en el "subject" del token.
     */
    public String getEmailFromToken(String jwt) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Extrae los claims (cuerpo de datos) de un token JWT después de validar su firma.
     * Este método es útil en el filtro de validación para obtener toda la información del token.
     *
     * @param jwt El token JWT completo (sin el prefijo "Bearer ").
     * @return El objeto {@link Claims} que contiene todos los datos del payload del token.
     */
    public Claims getClaimsFromToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * Convierte una colección de objetos {@link GrantedAuthority} en una única cadena de texto
     * separada por comas.
     * <p>
     * Este formato es ideal para ser almacenado en un claim de un token JWT.
     *
     * @param authorities La colección de autoridades del usuario.
     * @return Una cadena que representa los roles, por ejemplo: "ROLE_ADMIN,ROLE_USER".
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}