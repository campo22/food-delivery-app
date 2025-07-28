package com.diver.service;

import com.diver.dto.CartDto;
import com.diver.model.User;
import com.diver.request.AddCartItemRequest;

/**
 * Contrato para el servicio de gestión del Carrito de Compras.
 * Define las operaciones de negocio para añadir, actualizar, eliminar ítems
 * y consultar el estado del carrito de un usuario.
 */
public interface CartService {

    /**
     * Añade un ítem al carrito del usuario o incrementa su cantidad si ya existe.
     *
     * @param req El DTO con la información del plato a añadir.
     * @param user    El usuario autenticado.
     * @return El DTO del carrito completo y actualizado.
     */
    CartDto addItemToCart(AddCartItemRequest req, User user);

    /**
     * Actualiza la cantidad de un ítem específico en el carrito.
     *
     * @param cartItemId El ID del ítem del carrito a actualizar.
     * @param quantity   La nueva cantidad. Si es 0, el ítem se elimina.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     */
    CartDto updateCartItemQuantity(Long cartItemId, int quantity, User user);

    /**
     * Elimina un ítem del carrito.
     *
     * @param cartItemId El ID del ítem del carrito a eliminar.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     */
    CartDto removeItemFromCart(Long cartItemId, User user);

    /**
     * Encuentra el carrito de un usuario por su objeto User.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito del usuario.

     */
    CartDto findCartByUserId(User user);

    /**
     * Elimina todos los ítems del carrito de un usuario.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito vacío.
     */
    CartDto clearCart(User user);
}