package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftSetDto {
    private Long id;
    private List<GiftSetItemDto> items;
    private BigDecimal totalPrice;
    private String packagingWishes;
}
