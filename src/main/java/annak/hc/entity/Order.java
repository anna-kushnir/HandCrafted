package annak.hc.entity;

import annak.hc.entity.embedded.DeliveryAddress;
import annak.hc.entity.embedded.Status;
import annak.hc.entity.embedded.TypeOfReceipt;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ORDER", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"user", "formationDate"})
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_ORDER", sequenceName = "HANDCRAFTED_SCHEMA.ORDER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_ORDER")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "USER_PHONE")
    private Long userPhone;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "FORMATION_DATE")
    private LocalDateTime formationDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "TYPE_OF_RECEIPT")
    @Enumerated(EnumType.STRING)
    private TypeOfReceipt typeOfReceipt;

    @Column(name = "RECEIPT_DATE")
    private LocalDateTime receiptDate;

    @AttributeOverrides({
            @AttributeOverride(name = "region", column = @Column(name = "DELIVERY_ADDRESS_REGION")),
            @AttributeOverride(name = "city", column = @Column(name = "DELIVERY_ADDRESS_CITY")),
            @AttributeOverride(name = "postAddress", column = @Column(name = "DELIVERY_ADDRESS_POST_ADDRESS"))
    })
    private DeliveryAddress deliveryAddress;

    @Column(name = "INVOICE_NUMBER")
    private Long invoiceNumber;

    @Column(name = "ORDER_COMMENTS")
    private String orderComments;

//    TODO: перевірити, чи треба цей список, чи може треба додати ще giftSets
    @ElementCollection
    @CollectionTable(schema = "HANDCRAFTED_SCHEMA", name = "ORDER_ITEM", joinColumns = @JoinColumn(name = "ORDER_ID"))
    @Column(name = "QUANTITY")
    @MapKeyJoinColumn(table = "ORDER_ITEM", name = "PRODUCT_ID")
    private Map<Product, Long> products = new HashMap<>();
}
