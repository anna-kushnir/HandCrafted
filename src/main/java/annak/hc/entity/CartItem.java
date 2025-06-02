package annak.hc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CART_ITEM", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"user", "product"})
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_CART_ITEM", sequenceName = "HANDCRAFTED_SCHEMA.CART_ITEM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_CART_ITEM")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GIFT_SET_ID")
    private GiftSet giftSet;

    @Column(name = "QUANTITY")
    private Long quantityInCart;
}
