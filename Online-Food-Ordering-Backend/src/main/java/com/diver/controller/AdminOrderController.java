package com.diver.controller;

import com.diver.dto.OrderDto;
import com.diver.model.User;
import com.diver.service.OrderService;
import com.diver.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de órdenes desde la perspectiva del restaurante/administrador.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
@Tag(
        name = "Order Management (Admin/Owner)",
        description = "Endpoints para que los propietarios gestionen las órdenes de su restaurante."
)
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final OrderService orderService;
    private final RestaurantService restaurantService; // Para obtener el ID del restaurante del propietario

    /**
     * Actualiza el estado de una orden específica.
     * @param orderId El ID de la orden a actualizar.
     * @param orderStatus El nuevo estado para la orden.
     * @param user El usuario (propietario) autenticado.
     * @return ResponseEntity con el DTO de la orden actualizada.
     */
    @PutMapping("/{orderId}/{orderStatus}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation
            (summary = "Actualizar estado de una orden",
            description = "Permite a un propietario de restaurante cambiar el estado de una orden " +
                          "(ej: a 'EN_PREPARACION', 'EN_CAMINO', etc.)."
            )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado de la orden actualizado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No tienes permiso para actualizar esta orden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada"
            )
    })
    public ResponseEntity<OrderDto> updateOrderStatus(
            @Parameter(description = "ID de la orden a actualizar", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "Nuevo estado de la orden (ej: EN_PREPARACION, EN_CAMINO, ENTREGADO)", required = true)
            @PathVariable String orderStatus,
            @AuthenticationPrincipal User user
    ) {
        log.info("Propietario '{}' solicita actualizar el estado de la orden ID {} a '{}'.",
                user.getEmail(), orderId, orderStatus);
        OrderDto order = orderService.updateOrderStatus(orderId, orderStatus, user);
        return ResponseEntity.ok(order);
    }

    /**
     * Obtiene todas las órdenes del restaurante del propietario autenticado.
     * Permite filtrar por estado.
     * @param orderStatus (Opcional) El estado por el cual filtrar las órdenes.
     * @param user El usuario (propietario) autenticado.
     * @return ResponseEntity con la lista de DTOs de las órdenes.
     */
    @GetMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(
            summary = "Ver órdenes de mi restaurante",
            description = "Devuelve una lista de todas las órdenes del restaurante del propietario," +
                          " con filtro opcional por estado.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Órdenes obtenidas exitosamente"
            )
    })
    public ResponseEntity<List<OrderDto>> getOrdersHistoryRestaurant(
            @Parameter(description = "Filtrar por estado de la orden (ej: PENDIENTE, EN_PREPARACION)")
            @RequestParam(required = false) String orderStatus,
            @AuthenticationPrincipal User user
    ) {
        log.debug("Propietario '{}' solicita las órdenes de su restaurante.", user.getEmail());
        var restaurant = restaurantService.getRestaurantByUserId(user.getId());
        List<OrderDto> orders = orderService.findOrdersByRestaurantId(restaurant.getId(), orderStatus, user);
        return ResponseEntity.ok(orders);
    }
}