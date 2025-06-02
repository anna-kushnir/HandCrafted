package annak.hc.dto;

import annak.hc.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemDto {
    private Long id;
    private Order order;
    private String name;            // назва товару або "Подарунковий набір"
    private BigDecimal cost;        // ціна товару або подарункового набору
    private Long quantityInOrder;
    private List<String> photos;    // 1 фото, якщо це товар, і більше, якщо це набір
    private boolean hasDeletedProduct;
    private boolean isGiftSet;

    private Long productId;
    private Long giftSetId;
}
