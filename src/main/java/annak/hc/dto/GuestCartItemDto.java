package annak.hc.dto;

import lombok.Data;

@Data
public class GuestCartItemDto {
    private Long productId;
    private Long quantityInCart;
}
