package annak.hc.repository.spec;

import annak.hc.entity.Category;
import annak.hc.entity.Color;
import annak.hc.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Set;

public class ProductSpecifications {

    private ProductSpecifications() {
        throw new IllegalStateException("Не можна створити екземпляр класу ProductSpecifications");
    }

    public static Specification<Product> hasColorIn(Set<Color> colors) {
        return (root, query, cb) -> root.join("colors").in(colors);
    }

    public static Specification<Product> priceBetween(BigDecimal priceFrom, BigDecimal priceTo) {
        return (root, query, cb) -> cb.or(
                cb.and(
                        cb.isTrue(root.get("withDiscount")),
                        cb.between(root.get("discountedPrice"), priceFrom, priceTo)
                ),
                cb.and(
                        cb.isFalse(root.get("withDiscount")),
                        cb.between(root.get("price"), priceFrom, priceTo)
                )
        );
    }

    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Product> hasCategory(Category category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }
}
