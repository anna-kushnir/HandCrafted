package annak.hc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GIFT_SET", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"user", "formationDate"})
@NoArgsConstructor
@AllArgsConstructor
public class GiftSet {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_GIFT_SET", sequenceName = "HANDCRAFTED_SCHEMA.GIFT_SET_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_GIFT_SET")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "FORMATION_DATE")
    private LocalDateTime formationDate;

    @Column(name = "PACKAGING_WISHES")
    private String packagingWishes;

    // TODO: при кожному перегляді кошика та створенні замовлення - потрібно оновити це поле актуальною вартістю
    @Column(name = "PRICE")
    private BigDecimal price;

    @OneToMany(mappedBy = "giftSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GiftSetItem> items = new ArrayList<>();
}
