package com.diver.config;

public class JwtConstant {

    // ✅ Ahora usa System.getenv() para acceder a la variable de entorno
    public static final String SECRET_KEY = System.getenv("JWT_SECRET");

    public static final String JWT_HEADER = "Authorization";

    public static final long EXPIRATION_TIME = 86400000L; // 24 horas en milisegundos
}
