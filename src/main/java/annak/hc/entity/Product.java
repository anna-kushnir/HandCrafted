package annak.hc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "PRODUCT", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"name", "creationDate"})
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_PRODUCT", sequenceName = "HANDCRAFTED_SCHEMA.PRODUCT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_PRODUCT")
    @Column(name = "ID", updatable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "KEY_WORDS")
    private String keyWords;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "WITH_DISCOUNT")
    private boolean withDiscount;

    @Column(name = "DISCOUNTED_PRICE")
    private BigDecimal discountedPrice;

    @Column(name = "IN_STOCK")
    private boolean inStock;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "CAN_ADD_TO_GIFT_SET")
    private boolean canAddToGiftSet;

    @Column(name = "MAX_QUANTITY_IN_GIFT_SET")
    private Long maxQuantityInGiftSet;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "DELETED")
    private boolean deleted;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPhoto> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            schema = "HANDCRAFTED_SCHEMA",
            name = "PRODUCT_COLOR",
            joinColumns = @JoinColumn(name = "PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "COLOR_ID")
    )
    private Set<Color> colors;
}
