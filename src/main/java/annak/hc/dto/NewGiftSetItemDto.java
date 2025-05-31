package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewGiftSetItemDto {
    private Long productId;
    private String productName;
    private Long quantity;
}
