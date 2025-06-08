package com.diver.service.Imp;

import com.diver.config.JwtProvider;
import com.diver.model.User;
import com.diver.repository.UserRepository;
import com.diver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

   /**
        * Busca y retorna un usuario a partir de un token JWT.
        *
        * Proceso:
        * 1. Extrae el email del usuario desde el token JWT usando JwtProvider.
        * 2. Busca el usuario en la base de datos por su email.
        *
        * @param jwt Token JWT del usuario autenticado.
        * @return User objeto usuario correspondiente al token.
        * @throws Exception si el usuario no existe o el token es inválido.
        */
    @Override
    public User findUserByJwt(String jwt) throws Exception {
        String email =  jwtProvider.getUsernameFromToken(jwt);
        User user = userRepository.findByEmail(email);
        return user;
    }

    /**
          * Busca un usuario por su email.
          *
          * @param email Email del usuario a buscar.
          * @return User objeto usuario correspondiente al email proporcionado.
          * @throws Exception si el usuario no existe en la base de datos.
          */
    @Override
    public User findUserByEmail(String email) throws Exception {

        User user= userRepository.findByEmail(email);
        if(user==null) {
            throw new Exception("El usuario con el email " + email + " no existe en el sistema");
        }
        return user;

    }
}
