package com.diver.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
