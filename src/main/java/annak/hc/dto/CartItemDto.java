package annak.hc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private String name;            // назва товару або "Подарунковий набір"
    private BigDecimal cost;        // ціна товару або подарункового набору
    private Long quantityInCart;
    private List<String> photos;    // 1 фото, якщо це товар, і більше, якщо це набір
    private boolean isGiftSet;

    private Long productId;
    private Long giftSetId;
    private Long productQuantity;   // якщо це товар, то вказується к-ть товару в наявності
}
