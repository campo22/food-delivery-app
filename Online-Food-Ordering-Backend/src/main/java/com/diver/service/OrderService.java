package com.diver.service;

import com.diver.dto.OrderDto;
import com.diver.model.User;
import com.diver.request.OrderRequest;

import java.util.List;

/**
 * Contrato para el servicio de gestión de Órdenes (Pedidos).
 * Define las operaciones de negocio para crear, actualizar, cancelar y consultar órdenes,
 * tanto desde la perspectiva del cliente como del restaurante.
 */
public interface OrderService {

    /**
     * Crea una nueva orden a partir del carrito de un usuario.
     * La implementación debe validar que el restaurante exista, calcular el total
     * y limpiar el carrito del usuario tras crear la orden.
     *
     * @param req  El DTO de la petición, que contiene el ID del restaurante y la dirección de entrega.
     * @param user El usuario (cliente) que realiza la orden.
     * @return El DTO de la orden recién creada.
     */
    OrderDto createOrder(OrderRequest req, User user);

    /**
     * Actualiza el estado de una orden.
     * Esta operación es típicamente realizada por el propietario del restaurante.
     *
     * @param orderId     El ID de la orden a actualizar.
     * @param orderStatus El nuevo estado de la orden (ej: "EN_PREPARACION", "EN_CAMINO").
     * @param user        El usuario (propietario) que realiza la actualización.
     * @return El DTO de la orden actualizada.
     */
    OrderDto updateOrderStatus(Long orderId, String orderStatus, User user);

    /**
     * Cancela una orden.
     * Un usuario solo puede cancelar su propia orden.
     *
     * @param orderId El ID de la orden a cancelar.
     * @param user    El usuario (cliente) que realiza la cancelación.
     */
    void cancelOrder(Long orderId, User user);

    /**
     * Obtiene todas las órdenes realizadas por un usuario.
     *
     * @param user El usuario cuyas órdenes se desean obtener.
     * @return Una lista de DTOs de las órdenes del usuario.
     */
    List<OrderDto> findOrdersByUserId(User user);

    /**
     * Obtiene todas las órdenes de un restaurante, con un filtro opcional por estado.
     * La implementación debe validar que el 'user' es el propietario del restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @param orderStatus  (Opcional) El estado por el cual filtrar las órdenes.
     * @param user         El usuario (propietario) que realiza la consulta.
     * @return Una lista de DTOs de las órdenes del restaurante.
     */
    List<OrderDto> findOrdersByRestaurantId(Long restaurantId, String orderStatus, User user);

    /**
     * Encuentra una orden específica por su ID, validando los permisos del usuario.
     * Sirve tanto para clientes (que solo pueden ver sus órdenes) como para
     * propietarios (que solo pueden ver las de su restaurante).
     *
     * @param orderId El ID de la orden a buscar.
     * @param user    El usuario que realiza la consulta.
     * @return El DTO de la orden encontrada.
     */
    OrderDto findOrderById(Long orderId, User user);
}