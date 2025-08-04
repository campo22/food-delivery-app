package com.diver.service.Imp;

import com.diver.dto.*;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.OperationNotAllowedException;
import com.diver.exception.ResourceNotFoundException;
import com.diver.exception.UserNotFoundException;
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
     * @param detachedUser El usuario (cliente) que realiza la orden.
     * @return El DTO de la orden recién creada.
     */
    // Para la creación de órdenes - necesita transacción ya que modifica múltiples entidades
    @Override
    @Transactional
    public OrderDto createOrder(OrderRequest req, User detachedUser) { // Parámetro renombrado para mayor claridad
        log.info("Iniciando la creación de una nueva orden para el usuario: {}", detachedUser.getEmail());

        // --- PASO 1: CARGAR LA ENTIDAD "USER" GESTIONADA ---
        // Se carga una instancia "fresca" del usuario desde la BD para trabajar dentro de la transacción actual.
        // Esto es CRUCIAL para evitar LazyInitializationException.
        User managedUser = userRepository.findById(detachedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("El usuario autenticado con ID " + detachedUser.getId() +
                                                             " no fue encontrado en la base de datos."));

        // --- PASO 2: GESTIONAR LA DIRECCIÓN DE ENTREGA ---
        // La petición envía un objeto Address completo. Lo guardamos para asegurarnos de que tiene un ID.
        Address deliveryAddressFromRequest = req.getDeliveryAddress();
        Address savedAddress = addressRepository.save(deliveryAddressFromRequest);

        // Verificamos si esta dirección ya está asociada al perfil del usuario.
        // Usamos la lista 'addresses' del 'managedUser', que ahora es accesible.
        boolean addressExistsInProfile = managedUser.getAddresses().stream()
                .anyMatch(addr -> addr.getId().equals(savedAddress.getId()));

        if (!addressExistsInProfile) {
            // Si no existe, la añadimos a su perfil.
            managedUser.getAddresses().add(savedAddress);
            // No es necesario un save explícito de 'managedUser' aquí, @Transactional se encargará.
            log.info("Nueva dirección ID {} añadida al perfil del usuario '{}'.", savedAddress.getId(), managedUser.getEmail());
        }

        // --- PASO 3: VALIDAR RESTAURANTE Y CARRITO ---
        Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con ID: "
                        + req.getRestaurantId()));

        Cart cart = cartRepository.findByCustomerId(managedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario con ID: "
                        + managedUser.getId()));

        if (cart.getCartItems().isEmpty()) { // Asumiendo que el campo en Cart se llama 'items'
            throw new OperationNotAllowedException("No se puede crear una orden desde un carrito vacío.");
        }

        // --- PASO 4: CREAR Y POBLAR LA ORDEN ---
        Order order = new Order();
        order.setCustomer(managedUser); // Usamos el usuario gestionado
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(savedAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus("PENDIENTE");

        // --- PASO 5: CONVERTIR ITEMS DEL CARRITO A ITEMS DE ORDEN ---
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setIngredients(new ArrayList<>(cartItem.getIngredients()));
            orderItem.setOrder(order); // Asociar el ítem a la nueva orden

            orderItems.add(orderItem);
        }
        // Asignamos la lista a la orden. Cascade.ALL se encargará de guardarlos.
        order.setOrderItems(orderItems);

        // --- PASO 6: CALCULAR TOTALES Y GUARDAR LA ORDEN ---
        order.setTotalAmount(cart.getTotal());
        order.setTotalItems(cart.getCartItems().size());

        Order savedOrder = orderRepository.save(order);

        // --- PASO 7: LIMPIAR EL CARRITO ---
        // Llamamos al servicio de carrito, que tiene la lógica de negocio para limpiarlo.
        cartService.clearCart(managedUser);

        log.info("Orden ID {} creada con éxito para el usuario: {}. El carrito ha sido vaciado.",
                savedOrder.getId(), managedUser.getEmail());

        // --- PASO 8: DEVOLVER EL DTO DE RESPUESTA ---
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
        dto.setTotalItemCount(order.getOrderItems().size());
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
        dto.setIngredients(item.getIngredients());
        return dto;
    }
    private SimpleFoodDto mapToSimpleFoodDto(Food food) {
        SimpleFoodDto dto = new SimpleFoodDto();
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