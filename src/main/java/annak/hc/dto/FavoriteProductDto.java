package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FavoriteProductDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private boolean withDiscount;
    private BigDecimal discountedPrice;
    private String photo;
    private boolean inStock;
}
