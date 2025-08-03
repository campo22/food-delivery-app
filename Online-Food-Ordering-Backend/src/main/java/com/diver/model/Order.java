package com.diver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Representa una orden dentro del sistema de pedidos del restaurante.
 * <p>
 * Una orden contiene información sobre el cliente, el restaurante, la dirección de entrega,
 * los productos solicitados, el estado de la orden, la fecha de creación, el total de productos y el precio total.
 * </p>
 *
 * <p>
 * Relaciones principales:
 * <ul>
 *   <li>Muchas órdenes pueden pertenecer a un cliente único y un cliente puede tener muchas órdenes.</li>
 *   <li>Muchas órdenes pueden pertenecer a un restaurante único y un restaurante puede tener muchas órdenes.</li>
 *   <li>Muchas órdenes pueden pertenecer a una dirección de entrega única y una dirección de entrega puede tener muchas órdenes.</li>
 *   <li>Una orden puede tener muchos productos y un producto puede pertenecer a muchas órdenes.</li>
 *   <li>Una orden puede tener un metodo de pago y un metodo de pago puede pertenecer a muchas órdenes.</li>
 * </ul>
 * </p>
 *
 * @author Equipo de Desarrollo
 * @version 1.0
 * @since 2024-06
 */
@Data
@Entity
@Table(name = "`order`")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    /**
     * Identificador único de la orden.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Cliente que realiza la orden.
     * Relación muchos a uno: muchas órdenes pueden pertenecer a un cliente.
     */
    @ManyToOne
    private User customer;

    /**
     * Restaurante asociado a la orden.
     * Relación muchos a uno: muchas órdenes pueden pertenecer a un restaurante.
     * Se ignora en la serialización JSON para evitar ciclos infinitos.
     */
    @JsonIgnore
    @ManyToOne
    private Restaurant restaurant;

    /**
     * Monto total de la orden en la moneda correspondiente.
     */
    private Long totalAmount;

    /**
     * Estado actual de la orden (por ejemplo: "Pendiente", "En preparación", "Entregada").
     * Indica la fase del proceso en la que se encuentra el pedido.
     */
    private String orderStatus;

    /**
     * Fecha y hora de creación de la orden.
     * Registra el momento exacto en que se realizó el pedido.
     */
    private LocalDateTime createdAt;

    /**
     * Dirección de entrega asociada a la orden.
     * Relación muchos a uno: muchas órdenes pueden compartir una dirección de entrega.
     * Especifica dónde debe ser entregado el pedido.
     */
    @ManyToOne
    private Address deliveryAddress;

    /**
     * Lista de productos (ítems) incluidos en la orden.
     * Relación uno a muchos: una orden puede tener muchos productos.
     * Contiene el detalle de cada producto solicitado.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    // **
    //  * Método de pago utilizado para la orden.
    //  * Relación pendiente de implementación.
    //  */
    // private Payment payment;

    /**
     * Cantidad total de productos en la orden.
     * Representa el número total de ítems solicitados.
     */
    private int totalItems;

    /**
     * Precio total de la orden.
     * Representa el costo final del pedido incluyendo todos los productos.
     */
    private int totalPrice;

}
