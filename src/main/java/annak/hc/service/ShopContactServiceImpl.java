package annak.hc.service;

import annak.hc.entity.ShopContact;
import annak.hc.repository.ShopContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopContactServiceImpl implements ShopContactService {

    private final ShopContactRepository shopContactRepository;

    @Override
    public List<ShopContact> getAll() {
        return shopContactRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public String updateAll(List<ShopContact> shopContactList) {
        if (shopContactList == null || shopContactList.size() != 5) {
            return "Очікується рівно 5 контактів";
        }
        for (ShopContact shopContact : shopContactList) {
            update(shopContact);
        }
        return "Контактні дані успішно оновлено";
    }

    @Override
    public String update(ShopContact shopContact) {
        shopContactRepository.findById(shopContact.getId()).ifPresent(existing -> {
            existing.setValue(shopContact.getValue());
            shopContactRepository.save(existing);
        });
        return "Контакт успішно оновлено";
    }
}
