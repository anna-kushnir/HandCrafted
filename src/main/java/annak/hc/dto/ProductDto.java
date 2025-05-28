package annak.hc.dto;

import annak.hc.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String keyWords;
    private BigDecimal price;
    private boolean withDiscount;
    private BigDecimal discountedPrice;
    private boolean inStock;
    private Long quantity;
    private boolean canAddToGiftSet;
    private Long maxQuantityInGiftSet;
    private LocalDateTime creationDate;
    private Category category;
    private Long categoryId;
    private boolean deleted;
    private List<String> photos;
    private String colors;
}
