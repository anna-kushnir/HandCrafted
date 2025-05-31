package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FavoriteProductToGiftSetDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String photo;
    private boolean inStock;
    private boolean canAddToGiftSet;
    private Long maxQuantity;
}
