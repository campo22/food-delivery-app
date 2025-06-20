package com.diver.repository;

import com.diver.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * 🔎 Búsqueda flexible de restaurantes por nombre o tipo de cocina.
     *
     * Esta consulta personalizada permite buscar restaurantes cuyos nombres
     * o tipos de cocina contengan una palabra clave específica, sin importar
     * si está escrita en mayúsculas o minúsculas.
     *
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * findBySearchQuery("sushi")
     * </pre>
     *
     * Buscará coincidencias como:
     * - "Sushi World" (en el nombre)
     * - "Japonesa" (en el tipo de cocina)
     *
     * @param query Palabra clave para buscar (por ejemplo: "pizza", "italiana").
     * @return Lista de restaurantes que coinciden con el criterio de búsqueda.
     */
    @Query("""
           SELECT r FROM Restaurant r
           WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    List<Restaurant> findBySearchQuery(String query);


    /**
     * 🔐 Obtiene un restaurante por el ID de su propietario.
     *
     * Útil para mostrar o administrar el restaurante de un usuario autenticado
     * que tiene rol de propietario.
     *
     * <p><b>Ejemplo:</b> Si el usuario con ID 3 tiene un restaurante, lo retorna.</p>
     *
     * @param userId ID del usuario propietario del restaurante.
     * @return Restaurante asociado, o {@code null} si no existe.
     */
    Restaurant findByOwnerId(Long userId);
}
