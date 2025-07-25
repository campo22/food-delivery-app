package com.diver.controller;

import com.diver.dto.CategoryDto;
import com.diver.model.User;
import com.diver.request.CreateCategoryRequest; // DTO específico para la petición
import com.diver.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
// ... otras importaciones de Swagger y Spring ...
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
// Ruta base consistente para todas las operaciones de gestión de categorías
@RequestMapping("/api")
@Tag(name = "Admin: Category Management", description = "Endpoints para la gestión de categorías.")
public class CategoryController {

    private final CategoryService categoryService;


    @PostMapping("/admin/category")
    @Operation(summary = "Crear una nueva categoría",
            description = "Permite a un PROPIETARIO DE RESTAURANTE crear una nueva categoría para su restaurante.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Categoría creada exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado si el usuario no es RESTAURANT_OWNER",
                            content = @Content)
    })
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<CategoryDto> createCategory(
            // Usamos el DTO de petición correcto, que solo pide el 'name'
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal User user
    ) {
        log.info("Usuario '{}' solicita crear una nueva categoría: '{}'.", user.getEmail(), request.getName());

        // El servicio solo necesita el nombre y el usuario autenticado
        CategoryDto createdCategory = categoryService.createCategory(request.getName(), user);

        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // Este endpoint es para que un OWNER obtenga las categorías de SU restaurante.
    @GetMapping("/admin/my-restaurant/categories") // Ruta clara y específica
    @Operation(
            summary = "Obtener categorías de mi restaurante",
            description = "Permite a un PROPIETARIO DE RESTAURANTE " +
                          "obtener todas las categorías asociadas a su restaurante.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categorías obtenidas exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado si el usuario no es RESTAURANT_OWNER",
                            content = @Content)
            }
    )
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<CategoryDto>> getMyRestaurantCategories(
            @AuthenticationPrincipal User user
    ) {
        log.debug("Propietario '{}' solicita las categorías de su restaurante.", user.getEmail());

        // Llamamos al método del servicio que entiende cómo buscar por userId
        List<CategoryDto> categories = categoryService.findCategoriesByUserId(user.getId());

        return ResponseEntity.ok(categories);
    }

    // Y si un ADMIN necesita ver las categorías de CUALQUIER restaurante, tendría su propio endpoint:
    @GetMapping("/admin/{Id}/category")
    @Operation(
            summary = "Obtener categorías por ID de restaurante (Admin)",
            description = "Permite a un ADMINISTRADOR obtener todas las " +
                          "categorías de un restaurante específico por su ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categorías obtenidas exitosamente"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acceso denegado si el usuario no es ADMIN",
                            content = @Content)
            }
    )
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long Id) {
        log.debug("Admin solicita las categorías del restaurante ID: {}", Id);
        CategoryDto categories = categoryService.findCategoryById(Id);
        return ResponseEntity.ok(categories);
    }
}