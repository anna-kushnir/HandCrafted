package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftSetItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productCost;
    private Long quantity;
}
