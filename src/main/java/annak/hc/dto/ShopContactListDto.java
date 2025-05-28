package annak.hc.dto;

import annak.hc.entity.ShopContact;
import lombok.Data;

import java.util.List;

@Data
public class ShopContactListDto {
    private List<ShopContact> shopContacts;
}
