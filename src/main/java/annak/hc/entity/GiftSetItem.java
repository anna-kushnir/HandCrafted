package annak.hc.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "GIFT_SET_ITEM", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"giftSet", "product"})
@NoArgsConstructor
@AllArgsConstructor
public class GiftSetItem {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_GIFT_SET_ITEM", sequenceName = "HANDCRAFTED_SCHEMA.GIFT_SET_ITEM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_GIFT_SET_ITEM")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GIFT_SET_ID")
    private GiftSet giftSet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "QUANTITY")
    private Long quantity;

    // TODO: при кожному перегляді кошика та створенні замовлення - потрібно оновити це поле актуальною ціною
    @Column(name = "PRODUCT_COST")
    private BigDecimal productCost;
}
