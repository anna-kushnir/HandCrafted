package annak.hc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_PHOTO", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"product", "photo"})
@NoArgsConstructor
@AllArgsConstructor
public class ProductPhoto {
    @Id
    @SequenceGenerator(name = "ID_GENERATOR_PRODUCT_PHOTO", sequenceName = "HANDCRAFTED_SCHEMA.PRODUCT_PHOTO_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GENERATOR_PRODUCT_PHOTO")
    @Column(name = "ID", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "PHOTO", nullable = false)
    private String photo;

    @Column(name = "INDEX")
    private Integer index;
}
