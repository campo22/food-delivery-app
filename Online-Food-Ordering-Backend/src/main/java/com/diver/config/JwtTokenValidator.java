package com.diver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {
    /**
     * @param request // Solicitud HTTP
     * @param response // Respuesta HTTP
     * @param filterChain  // Filtro de secuencia de peticiones HTTP
     * @throws ServletException // Excepción si ocurre algún error durante la ejecución del filtro
     * @throws IOException // el error de entrada/salida durante la ejecución del filtro
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Aquí se puede agregar lógica de filtro antes de que la solicitud continúe a través de la cadena
        // Por ejemplo, se puede agregar código para autenticar al usuario, registrar la solicitud,
        // modificar la solicitud o la respuesta, etc.

        // Después de realizar las operaciones de filtro necesarias, se llama al metodo nextFilter de filterChain
        // para permitir que la solicitud continúe a través de la cadena hacia el recurso final
        //filterChain.doFilter(request, response);

        // Obtener el token JWT de la solicitud
        String jwt=request.getHeader(JwtConstant.JWT_HEADER);

        if (jwt !=null){

            // Eliminar el prefijo "Bearer " del token JWT para obtener solo el token
            jwt = jwt.substring(7);

            // Validar el token JWT
            try {

                // keys.hmacShaKeyFor() genera una clave secreta a partir de una
                // cadena de bytes que se usa para firmar el token
                SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

                Claims claims= Jwts.parserBuilder()// Inicia configuración del validador de JWT
                                   .setSigningKey(key)// Define la clave secreta para firmar el token
                                   .build()
                                   .parseClaimsJws(jwt) // Parsea el token JWT y extrae los claims (información del token)
                                   .getBody(); // Obtiene los claims del token


                String email=String.valueOf(claims.get("email"));
                String authorities=String.valueOf(claims.get("authorities"));

                // Convertir el string de autoridades en una lista de objetos GrantedAuthority
                // El metodo AuthorityUtils.commaSeparatedStringToAuthorityList() convierte una cadena de autoridades
                // separadas por comas en una lista de objetos GrantedAuthority
                List<GrantedAuthority> auth= AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);





            } catch (Exception e) {
                throw new BadRequestException("Token JWT inválido......");
            }

            // Si el token JWT es válido, se puede continuar con la solicitud
            filterChain.doFilter(request, response);

        }




    }
}
