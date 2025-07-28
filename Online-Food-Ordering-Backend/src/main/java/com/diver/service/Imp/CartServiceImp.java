package com.diver.service.Imp;

import com.diver.dto.CartDto;
import com.diver.dto.CartItemDto;
import com.diver.dto.UserSimpleDto;
import com.diver.exception.AccessDeniedException;
import com.diver.exception.ResourceNotFoundException;
import com.diver.model.Cart;
import com.diver.model.CartItem;
import com.diver.model.Food;
import com.diver.model.User;
import com.diver.repository.CartItemRepository;
import com.diver.repository.CartRepository;
import com.diver.repository.FoodRepository;
import com.diver.request.AddCartItemRequest;
import com.diver.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class CartServiceImp implements CartService {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;
    private final CartItemRepository cartItemRepository;


    /**
     * Añade un ítem al carrito del usuario o incrementa su cantidad si ya existe.
     *
     * @param req  El DTO con la información del plato a añadir.
     * @param user El usuario autenticado.
     * @return El DTO del carrito completo y actualizado.
     * @throws ResourceNotFoundException Si el plato no se encuentra.
     */
    @Override
    @Transactional
    public CartDto addItemToCart(AddCartItemRequest req, User user) {
        log.info("Usuario '{}' solicita añadir el plato ID {} (cantidad: {}) a su carrito.",
                user.getEmail(), req.getFoodId(), req.getQuantity());

        Cart cart = findCartByUserIdInternal(user.getId());
        Food food = foodRepository.findById(req.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado con ID: " + req.getFoodId()));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getFood().getId().equals(req.getFoodId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int oldQuantity = existingItem.getQuantity();
            existingItem.setQuantity(oldQuantity + req.getQuantity());
            existingItem.setTotalPrice(existingItem.getQuantity() * food.getPrice());
            log.debug("Ítem existente encontrado. Actualizando cantidad de {} a {}.", oldQuantity, existingItem.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setFood(food);
            newItem.setCart(cart);
            newItem.setQuantity(req.getQuantity());
            newItem.setTotalPrice((long) req.getQuantity() * food.getPrice());
            cart.getCartItems().add(newItem);
            log.debug("Añadiendo nuevo ítem al carrito para el plato '{}'.", food.getName());
        }

        recalculateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        log.info("Carrito del usuario '{}' actualizado. Nuevo total: {}.", user.getEmail(), updatedCart.getTotal());
        return mapToCartDto(updatedCart);
    }

    /**
     * Actualiza la cantidad de un ítem específico en el carrito.
     *
     * @param cartItemId El ID del ítem del carrito a actualizar.
     * @param quantity   La nueva cantidad. Si es 0, el ítem se elimina.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     * @throws ResourceNotFoundException Si el ítem del carrito no se encuentra.
     * @throws AccessDeniedException     Si el usuario no tiene permiso para modificar este ítem.
     */
    @Override
    @Transactional
    public CartDto updateCartItemQuantity(Long cartItemId, int quantity, User user) {
        log.info("Usuario '{}' solicita actualizar la cantidad del ítem de carrito ID {} a {}.",
                user.getEmail(), cartItemId, quantity);

        CartItem cartItem = findCartItemByIdAndValidateOwnership(cartItemId, user);
        Cart cart = cartItem.getCart();

        if (quantity <= 0) {
            log.warn("Cantidad solicitada es {} (<= 0). Eliminando el ítem ID {} del carrito.", quantity, cartItemId);
            cart.getCartItems().remove(cartItem);
        } else {
            int oldQuantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice((long) quantity * cartItem.getFood().getPrice());
            log.debug("Cantidad del ítem ID {} actualizada de {} a {}.", cartItemId, oldQuantity, quantity);
        }

        recalculateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        log.info("Cantidad del ítem en el carrito de '{}' actualizada. Nuevo total: {}.", user.getEmail(), updatedCart.getTotal());
        return mapToCartDto(updatedCart);
    }

    /**
     * Elimina un ítem del carrito.
     *
     * @param cartItemId El ID del ítem del carrito a eliminar.
     * @param user       El usuario autenticado, para validación de propiedad.
     * @return El DTO del carrito completo y actualizado.
     * @throws ResourceNotFoundException Si el ítem del carrito no se encuentra.
     * @throws AccessDeniedException     Si el usuario no tiene permiso para eliminar este ítem.
     */
    @Override
    @Transactional
    public CartDto removeItemFromCart(Long cartItemId, User user) {
        log.warn("Usuario '{}' solicita eliminar el ítem de carrito ID {}.", user.getEmail(), cartItemId);
        CartItem cartItem = findCartItemByIdAndValidateOwnership(cartItemId, user);
        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);

        recalculateCartTotal(cart);
        Cart updatedCart = cartRepository.save(cart);
        log.info("Ítem ID {} eliminado del carrito de '{}'. Nuevo total: {}.", cartItemId, user.getEmail(), updatedCart.getTotal());
        return mapToCartDto(updatedCart);
    }

    /**
     * Encuentra el carrito de un usuario por su objeto User.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito del usuario.
     * @throws ResourceNotFoundException Si el carrito no se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public CartDto findCartByUserId(User user) {
        log.debug("Solicitud para encontrar el carrito del usuario '{}'.", user.getEmail());
        Cart cart = findCartByUserIdInternal(user.getId());
        return mapToCartDto(cart);
    }

    /**
     * Elimina todos los ítems del carrito de un usuario.
     *
     * @param user El usuario autenticado.
     * @return El DTO del carrito vacío.
     * @throws ResourceNotFoundException Si el carrito no se encuentra.
     */
    @Override
    @Transactional
    public CartDto clearCart(User user) {
        log.warn("Usuario '{}' solicita vaciar su carrito por completo.", user.getEmail());
        Cart cart = findCartByUserIdInternal(user.getId());

        if (cart.getCartItems().isEmpty()) {
            log.info("El carrito del usuario '{}' ya estaba vacío. No se realizaron cambios.", user.getEmail());
            return mapToCartDto(cart);
        }

        cart.getCartItems().clear();
        recalculateCartTotal(cart); // Esto pondrá el total a 0
        Cart clearedCart = cartRepository.save(cart);
        log.info("Carrito del usuario '{}' vaciado exitosamente.", user.getEmail());
        return mapToCartDto(clearedCart);
    }

    // --- MÉTODOS PRIVADOS ---

    private Cart findCartByUserIdInternal(Long userId) {
        return cartRepository.findByCustomerId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario con ID: " + userId));
    }

    private CartItem findCartItemByIdAndValidateOwnership(Long cartItemId, User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de carrito no encontrado con ID: " + cartItemId));
        if (!cartItem.getCart().getCustomer().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permiso para modificar este ítem del carrito.");
        }
        return cartItem;
    }

    private void recalculateCartTotal(Cart cart) {
        long total = cart.getCartItems().stream().mapToLong(CartItem::getTotalPrice).sum();
        cart.setTotal(total);
    }
    private CartDto mapToCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setCustomer( mapToSimpleUserDto(cart.getCustomer()) );
        dto.setItems(cart.getCartItems().stream().map(this::mapToCartItemDto).toList());
        dto.setTotal(cart.getTotal());
        return dto;
    }

    private UserSimpleDto mapToSimpleUserDto(User customer) {
        UserSimpleDto dto = new UserSimpleDto();
        dto.setId(customer.getId());
        dto.setEmail(customer.getEmail());
        return dto;
    }

    private CartItemDto mapToCartItemDto(CartItem cartItem) {
    CartItemDto dto = new CartItemDto();
    dto.setId(cartItem.getId());
    dto.setFood( mapToSimpleFoodDto(cartItem.getFood()) );
    dto.setQuantity(cartItem.getQuantity());
    dto.setIngredients(cartItem.getIngredients());
    dto.setTotalPrice(cartItem.getTotalPrice());
    return dto;
    }
    private CartItemDto.SimpleFoodDto mapToSimpleFoodDto(Food food) {
        CartItemDto.SimpleFoodDto simpleFoodDto = new CartItemDto.SimpleFoodDto();
        simpleFoodDto.setId(food.getId());
        simpleFoodDto.setName(food.getName());
        simpleFoodDto.setImage(
                food.getImages() != null &&
                !food.getImages().isEmpty() ?
                food.getImages().get(0) :
                null
        );
        return simpleFoodDto;
    }




}
