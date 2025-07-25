package com.diver.service.Imp;

import com.diver.dto.CartDto;
import com.diver.model.User;
import com.diver.request.AddCartItemRequest;
import com.diver.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class CartServiceImp implements CartService {


    /**
     * Añade un ítem al carrito del usuario o incrementa su cantidad si ya existe.
     *
     * @param request El DTO con la información del plato a añadir.
     * @param user    El usuario autenticado.
     * @return El DTO del carrito completo y actualizado.
     */
    @Override
    public CartDto addItemToCart(AddCartItemRequest request, User user) {
        return null;
    }

    /**
     * Actualiza la cantidad de un ítem específico en el carrito.
     *
     * @param cartItemId El ID del ítem del carrito a actualizar.
     * @param quantity   La nueva cantidad. Si es 0, el ítem se elimina.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     */
    @Override
    public CartDto updateCartItemQuantity(Long cartItemId, int quantity, User user) {
        return null;
    }

    /**
     * Elimina un ítem del carrito.
     *
     * @param cartItemId El ID del ítem del carrito a eliminar.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     */
    @Override
    public CartDto removeItemFromCart(Long cartItemId, User user) {
        return null;
    }

    /**
     * Encuentra el carrito de un usuario por su objeto User.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito del usuario.
     */
    @Override
    public CartDto findCartByUserId(User user) {
        return null;
    }

    /**
     * Elimina todos los ítems del carrito de un usuario.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito vacío.
     */
    @Override
    public CartDto clearCart(User user) {
        return null;
    }
}
