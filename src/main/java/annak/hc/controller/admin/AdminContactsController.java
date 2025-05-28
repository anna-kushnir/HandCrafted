package annak.hc.controller.admin;

import annak.hc.dto.ShopContactListDto;
import annak.hc.service.ShopContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactsController {

    private final ShopContactService shopContactService;

    @GetMapping
    public String getEditContactsForm(Model model) {
        var contacts = shopContactService.getAll();
        ShopContactListDto shopContactsDto = new ShopContactListDto();
        shopContactsDto.setShopContacts(contacts);
        model.addAttribute("oldContacts", shopContactsDto);
        return "admin/edit_contacts";
    }

    @PostMapping
    public String editContacts(@ModelAttribute("oldContacts") ShopContactListDto shopContactListDto,
                               RedirectAttributes redirectAttributes) {
        String result = shopContactService.updateAll(shopContactListDto.getShopContacts());
        redirectAttributes.addFlashAttribute("msgResult", result);
        return "redirect:/admin/contacts";
    }
}
