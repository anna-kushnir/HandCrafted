package annak.hc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEM", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_ORDER_ITEM", sequenceName = "HANDCRAFTED_SCHEMA.ORDER_ITEM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_ORDER_ITEM")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    // TODO: перевіряти, щоб був вказаний або лише товар, або лише набір
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GIFT_SET_ID")
    private GiftSet giftSet;

    @Column(name = "ITEM_COST")
    private BigDecimal itemCost;

    @Column(name = "QUANTITY")
    private Long quantityInOrder;
}
