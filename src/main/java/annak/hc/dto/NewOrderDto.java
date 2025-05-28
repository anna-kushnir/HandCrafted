package annak.hc.dto;

import annak.hc.entity.User;
import annak.hc.entity.embedded.TypeOfReceipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewOrderDto {
    private User user;
    private Long userPhone;
    private TypeOfReceipt typeOfReceipt;
    private String deliveryAddressRegion;
    private String deliveryAddressCity;
    private String deliveryAddressPostAddress;
    private String orderComments;
}
