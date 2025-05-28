package annak.hc.controller;

import annak.hc.entity.ShopContact;
import annak.hc.service.ShopContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ShopContactService shopContactService;

    @ModelAttribute("shopContacts")
    public List<ShopContact> getShopContacts() {
        return shopContactService.getAll();
    }
}
