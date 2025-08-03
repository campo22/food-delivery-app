package com.diver.controller;

import com.diver.dto.CartDto;
import com.diver.model.User;
import com.diver.request.AddCartItemRequest;
import com.diver.request.UpdateCartItemRequest;
import com.diver.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(
        name = "Cart Management",
        description = "Endpoints para gestionar el carrito de compras del usuario.")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @Operation(summary = "Añadir un ítem al carrito",
            description = "Añade un plato de comida al carrito del usuario. Si el plato ya existe, incrementa su cantidad.")
    public ResponseEntity<CartDto> addItemToCart(
            @Valid @RequestBody AddCartItemRequest req, // <-- ¡CORREGIDO!
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita añadir el plato ID {} (cantidad: {}) a su carrito.",
                user.getEmail(), req.getFoodId(), req.getQuantity());
        CartDto cart = cartService.addItemToCart(req, user);
        return new ResponseEntity<>(cart, HttpStatus.OK); // 200 OK es más consistente para una operación que puede crear o actualizar.
    }

    @PutMapping("/item/update")
    @Operation(summary = "Actualizar la cantidad de un ítem del carrito",
            description = "Actualiza la cantidad de un ítem en el carrito del usuario. " +
                    "Si la cantidad es 0, el ítem se elimina."
    )
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @Valid @RequestBody UpdateCartItemRequest req,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita actualizar la cantidad del ítem de carrito ID {} a {}.",
                user.getEmail(), req.getCartItemId(), req.getQuantity());
        CartDto cart = cartService.updateCartItemQuantity(req.getCartItemId(), req.getQuantity(), user);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/item/{cartItemId}/remove")
    @Operation(summary = "Eliminar un ítem del carrito")
    public ResponseEntity<CartDto> removeItemFromCart(
            @Parameter(description = "ID del ítem del carrito a eliminar", required = true)
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal User user
    ) {
        log.warn("Usuario '{}' solicita eliminar el ítem de carrito ID {}.", user.getEmail(), cartItemId);
        CartDto cart = cartService.removeItemFromCart(cartItemId, user);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    @Operation(summary = "Obtener el carrito del usuario")
    public ResponseEntity<CartDto> findCartByUserId(@AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita consultar su carrito.", user.getEmail());
        CartDto cart = cartService.findCartByUserId(user);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Vaciar el carrito",
            description = "Elimina todos los ítems del carrito del usuario.")
    public ResponseEntity<CartDto> clearCart(@AuthenticationPrincipal User user) {
        log.warn("Usuario '{}' solicita vaciar su carrito por completo.", user.getEmail());
        CartDto cart = cartService.clearCart(user);
        return ResponseEntity.ok(cart);
    }
}