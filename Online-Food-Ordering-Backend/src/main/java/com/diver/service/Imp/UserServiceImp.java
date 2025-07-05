package com.diver.service.Imp;

import com.diver.dto.UserProfileDto;
import com.diver.exception.UserNotFoundException;
import com.diver.model.User;
import com.diver.repository.UserRepository;
import com.diver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la interfaz {@link UserService}.
 * <p>
 * Contiene la lógica de negocio para las operaciones de usuario, interactuando
 * con el repositorio para el acceso a datos. Utiliza la anotación {@code @Transactional}
 * para gestionar el ciclo de vida de las sesiones de base de datos.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Este método está marcado como transaccional de solo lectura. Esto asegura que la
     * sesión de Hibernate permanezca abierta durante toda la ejecución del método,
     * permitiendo que las colecciones con carga perezosa (como 'favorites' y 'addresses')
     * se inicialicen correctamente al ser accedidas durante la creación del DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileByEmail(String email) {
        // 1. Obtenemos la entidad completa desde la base de datos.
        User user = findUserByEmail(email); // Reutilizamos nuestro propio método

        // 2. Realizamos la transformación de la Entidad a un DTO.
        // Como estamos dentro de una transacción, acceder a user.getFavorites()
        // y user.getAddresses() funcionará sin problemas.
        return new UserProfileDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(), // Convertimos el enum a String para el DTO
                user.getFavorites(),
                user.getAddresses()
        );
    }

    /**
     * @param id
     * @return
     */
    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }
}