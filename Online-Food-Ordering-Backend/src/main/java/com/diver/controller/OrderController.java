package com.diver.controller;

import com.diver.dto.OrderDto;
import com.diver.model.User;
import com.diver.request.OrderRequest;
import com.diver.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las órdenes (pedidos) desde la perspectiva del cliente.
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(
        name = "Order Management (Customer)",
        description = "Endpoints para que los clientes creen y gestionen sus órdenes."
)
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * Crea una nueva orden a partir del carrito del usuario autenticado.
     * @param req El DTO de la petición con el ID del restaurante y el ID de la dirección de entrega.
     * @param user El usuario cliente autenticado.
     * @return ResponseEntity con el DTO de la orden creada y estado 201 Created.
     */
    @PostMapping
    @Operation(
            summary = "Crear una nueva orden",
            description = "Crea una orden con los ítems del carrito del usuario y la " +
                           "dirección de entrega especificada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante o dirección no encontrados")
    })
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody OrderRequest req,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' está creando una nueva orden.", user.getEmail());
        OrderDto order = orderService.createOrder(req, user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Obtiene el historial de órdenes del usuario autenticado.
     * @param user El usuario cliente autenticado.
     * @return ResponseEntity con una lista de DTOs de las órdenes del usuario.
     */
    @GetMapping
    @Operation(summary = "Ver mi historial de órdenes",
            description = "Devuelve una lista de todas las órdenes realizadas por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de órdenes obtenido exitosamente")
    })
    public ResponseEntity<List<OrderDto>> getOrderHistoryUser(
            @AuthenticationPrincipal User user
    ) {
        log.debug("Usuario '{}' solicita su historial de órdenes.", user.getEmail());
        List<OrderDto> orders = orderService.findOrdersByUserId(user);
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancela una orden específica del usuario.
     * La lógica de negocio en el servicio previene la cancelación de órdenes que ya no están pendientes.
     * @param orderId El ID de la orden a cancelar.
     * @param user El usuario cliente autenticado.
     * @return ResponseEntity con estado 204 No Content.
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancelar mi orden",
            description = "Permite a un usuario cancelar una de sus propias órdenes si aún está en estado 'PENDIENTE'.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Orden cancelada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permiso para cancelar esta orden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada")
    })
    public ResponseEntity<Void> cancelMyOrder(
            @Parameter(description = "ID de la orden a cancelar", required = true)
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user
    ) {
        log.warn("Usuario '{}' solicita la cancelación de la orden ID {}.", user.getEmail(), orderId);
        // El método del servicio se llama `cancelOrder`, pero la acción real es cambiar el estado.
        // Un PUT o PATCH es semánticamente más correcto que un DELETE para esta operación.
        orderService.cancelOrder(orderId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}