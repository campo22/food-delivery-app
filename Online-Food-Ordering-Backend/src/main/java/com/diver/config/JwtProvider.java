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

@Service
public class JwtProvider {

    private SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(Authentication auth) {

        // coleccion de roles del usuario autenticado
        Collection<? extends  GrantedAuthority> authorities = auth.getAuthorities();

        // convertir la coleccion de roles en una cadena
        String roles = populateAuthorities( authorities );

        String token = Jwts.builder()// el Jbuilder que se encarga de construir el token JWT
                .setExpiration((new Date(new Date().getTime()+ JwtConstant.EXPIRATION_TIME) )) // tiempo de expiración del token (24 horas)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim( "email", auth.getName() ) // email del usuario autenticado")
                .claim("roles", roles) // roles del usuario autenticado
                .signWith(key) // clave para firmar el token
                .compact(); // compacta el token en una cadena

        return token;
    }
    //  Obtiene el email del usuario autenticado a partir del token JWT
    public String  getUsernameFromToken(String token) {

        token= token.substring(7); // elimina el prefijo "Bearer " (7 caracteres)

        Claims claims =Jwts.parserBuilder()// el Jbuilder que se encarga de construir el token JWT
                .setSigningKey(key) // clave para verificar la firma del token
                .build() // construye el parser
                .parseClaimsJws(token) // parsea el token y obtiene los claims
                .getBody();

        String email = claims.get("email", String.class);
        return email;

    }

    /**
     * Convierte una colección de autoridades en una cadena separada por comas
     *
     * @param authorities Colección de objetos GrantedAuthority que representan los permisos/roles
     * @return Cadena formateada con todas las autoridades separadas por comas
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        // Crear un conjunto de autoridades para evitar duplicados de autoridades
        Set<String> auths = new HashSet<>();

        // Extraer el nombre de cada autoridad y agregarlo al conjunto
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }

        // Unir todas las autoridades en una sola cadena separada por comas
        return String.join(",", auths);
    }


}
