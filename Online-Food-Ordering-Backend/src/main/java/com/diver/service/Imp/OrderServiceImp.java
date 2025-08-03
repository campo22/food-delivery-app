package com.diver.service.Imp;

import com.diver.dto.*;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.OperationNotAllowedException;
import com.diver.exception.ResourceNotFoundException;
import com.diver.model.*;
import com.diver.repository.*;
import com.diver.request.OrderRequest;
import com.diver.service.CartService;
import com.diver.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;



    /**
     * Crea una nueva orden a partir del carrito de un usuario.
     * La implementación debe validar que el restaurante exista, calcular el total
     * y limpiar el carrito del usuario tras crear la orden.
     *
     * @param req  El DTO de la petición, que contiene el ID del restaurante y la dirección de entrega.
     * @param user El usuario (cliente) que realiza la orden.
     * @return El DTO de la orden recién creada.
     */
    // Para la creación de órdenes - necesita transacción ya que modifica múltiples entidades
    @Transactional
    @Override
    public OrderDto createOrder(OrderRequest req, User user) {
        log.info("Iniciando la creación de una nueva orden para el usuario: {}", user.getEmail());

        // PASO1: validar que el usuario tenga la direction de entrega
       Address shipAddress= req.getDeliveryAddress();
       Address saveAddress= addressRepository.save(shipAddress);


       if(!user.getAddresses().contains(saveAddress)) {
           user.getAddresses().add(saveAddress);
           userRepository.save(user);
       }

       // PASO2: validar que el restaurante exista
       Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
               .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

       // PASO3: obtener el carrito del usuario
       Cart cart = cartRepository.findByCustomerId(user.getId())
               .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

       if (cart.getCartItems().isEmpty()) {
           throw new OperationNotAllowedException("El carrito del usuario esta vacio");
       }

       Order order = new Order();
       order.setCustomer(user);
       order.setRestaurant(restaurant);
       order.setDeliveryAddress(saveAddress);
       order.setCreatedAt(LocalDateTime.now());
       order.setOrderStatus("PENDIENTE");

       // paso4: convertir los items del carrito en items de la orden
        List<OrderItem> createOrderItems = new ArrayList<>();
        for (CartItem cartItems : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();

            orderItem.setFood(cartItems.getFood());
            orderItem.setQuantity(cartItems.getQuantity());
            orderItem.setTotalPrice(cartItems.getTotalPrice());
            orderItem.setIngredients(cartItems.getIngredients());
            orderItem.setOrder(order);
            createOrderItems.add(orderItemRepository.save(orderItem));
        }
        order.setOrderItems(createOrderItems);
        // calcular el total de la orden
        order.setTotalAmount( cart.getTotal());
        order.setTotalItems( cart.getCartItems().size());
        Order savedOrder = orderRepository.save(order);
        // limpiar el carrito del usuario
        cartService.clearCart(user);
        log.info("Orden ID {} creada con exito para el usuario: {}",savedOrder.getId() ,user.getEmail());

        return mapToOrderDto(savedOrder);
    }

    /**
     * Actualiza el estado de una orden.
     * Esta operación es típicamente realizada por el propietario del restaurante.
     *
     * @param orderId     El ID de la orden a actualizar.
     * @param orderStatus El nuevo estado de la orden (ej: "EN_PREPARACION", "EN_CAMINO").
     * @param user        El usuario (propietario) que realiza la actualización.
     * @return El DTO de la orden actualizada.
     */
    // Para actualizar el estado de una orden
    @Transactional
    @Override
    public OrderDto updateOrderStatus(Long orderId, String orderStatus, User user) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if ( !order.getRestaurant().getOwner().getId().equals(user.getId()) ) {
            throw new OperationNotAllowedException("El usuario no es el propietario del restaurante");
        }

        if (
                orderStatus.equals("EN_PREPARACION")
                ||orderStatus.equals("EN_CAMINO")
                ||orderStatus.equals("ENTREGADO")
                ||orderStatus.equals("PENDIENTE")
                ||orderStatus.equals("CANCELADO")
        ) {
            order.setOrderStatus(orderStatus.toUpperCase());
            return mapToOrderDto(orderRepository.save(order));
        }

        throw new OperationNotAllowedException("El estado de la orden no es valido");
    }

    /**
     * Cancela una orden.
     * Un usuario solo puede cancelar su propia orden.
     *
     * @param orderId El ID de la orden a cancelar.
     * @param user    El usuario (cliente) que realiza la cancelación.
     */
    // Para cancelar una orden
    @Transactional
    @Override
    public void cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if ( !order.getCustomer().getId().equals(user.getId()) ) {
            throw new OperationNotAllowedException("El usuario no es el propietario de la orden");
        }
        if (!order.getOrderStatus().equals("PENDIENTE")) {
            throw new OperationNotAllowedException("La orden no puede ser cancelada");
        }
        order.setOrderStatus("CANCELADO");
        orderRepository.save(order);

    }

    /**
     * Obtiene todas las órdenes realizadas por un usuario.
     *
     * @param user El usuario cuyas órdenes se desean obtener.
     * @return Una lista de DTOs de las órdenes del usuario.
     */
    // Para consultas de solo lectura
    @Transactional(readOnly = true)
    @Override
    public List<OrderDto> findOrdersByUserId(User user) {
        List<Order> orders = orderRepository.findByCustomerId(user.getId());

        return orders.stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las órdenes de un restaurante, con un filtro opcional por estado.
     * La implementación debe validar que el 'user' es el propietario del restaurante.
     *
     * @param restaurantId El ID del restaurante.
     * @param orderStatus  (Opcional) El estado por el cual filtrar las órdenes.
     * @param user         El usuario (propietario) que realiza la consulta.
     * @return Una lista de DTOs de las órdenes del restaurante.
     */
    // Para consultas de solo lectura
    @Transactional(readOnly = true)
    @Override
    public List<OrderDto> findOrdersByRestaurantId(Long restaurantId, String orderStatus, User user) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con ID: " + restaurantId));

        if ( !restaurant.getOwner().getId().equals(user.getId()) ) {
            throw new AccessDeniedException("El usuario no es el propietario del restaurante");
        }

        List<Order> orders= (orderStatus !=null && !orderStatus.isEmpty() )
                ? orderRepository.findByRestaurantIdAndOrderStatus(restaurantId, orderStatus.toUpperCase())
                : orderRepository.findByRestaurantId(restaurantId);

        return orders.stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    /**
     * Encuentra una orden específica por su ID, validando los permisos del usuario.
     * Sirve tanto para clientes (que solo pueden ver sus órdenes) como para
     * propietarios (que solo pueden ver las de su restaurante).
     *
     * @param orderId El ID de la orden a buscar.
     * @param user    El usuario que realiza la consulta.
     * @return El DTO de la orden encontrada.
     */
    // Para consultas de solo lectura
    @Transactional(readOnly = true)
    @Override
    public OrderDto findOrderById(Long orderId, User user) {

        Order order = findOrderByIdInternal(orderId);

        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner = order.getRestaurant().getOwner().getId().equals(user.getId());

        if (!isCustomer && !isOwner) {
            throw new AccessDeniedException("El usuario no tiene permiso para ver la orden");

        }
        return mapToOrderDto(order);
    }

    // ===============================================================================
    // methods auxiliares
    // =================================================================================

    private Order findOrderByIdInternal(Long orderId) {

        return orderRepository.findById( orderId )
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + orderId));
    }

    private OrderDto mapToOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomer(mapToSimpleUserDto(order.getCustomer()));
        dto.setRestaurant(mapToRestaurantSimpleDto(order.getRestaurant()));
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setItems(order.getOrderItems().stream().map(this::mapToOrderItemDto).toList());
        return dto;
    }

    private UserSimpleDto mapToSimpleUserDto(User customer) {
        UserSimpleDto dto = new UserSimpleDto();
        dto.setId(customer.getId());
        dto.setEmail(customer.getEmail());
        return dto;
    }
    private OrderItemDto mapToOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setFood( mapToSimpleFoodDto(item.getFood()) );
        dto.setQuantity(item.getQuantity());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
    private FoodDto mapToSimpleFoodDto(Food food) {
        FoodDto dto = new FoodDto();
        dto.setId(food.getId());
        dto.setName(food.getName());
        return dto;
    }
    private RestaurantSimpleDto mapToRestaurantSimpleDto(Restaurant restaurant) {
        RestaurantSimpleDto dto = new RestaurantSimpleDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        return dto;
    }

}