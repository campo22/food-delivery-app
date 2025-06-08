package com.diver.service;

import com.diver.model.User;

public interface UserService {

    // buscar un usuario por su jwt
    public User findUserByJwt(String jwt) throws Exception;

    // buscar un usuario por su email
    public User findUserByEmail(String email) throws Exception;
}
