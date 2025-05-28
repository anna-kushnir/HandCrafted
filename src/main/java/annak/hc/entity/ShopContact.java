package annak.hc.entity;

import annak.hc.entity.embedded.ShopContactName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SHOP_CONTACT", schema = "HANDCRAFTED_SCHEMA")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
public class ShopContact {
    @Id
    @Column(name = "ID", updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopContactName name;

    @Column(name = "VALUE")
    private String value;
}
