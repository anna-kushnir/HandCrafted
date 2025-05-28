package annak.hc.service;

import annak.hc.entity.ShopContact;

import java.util.List;

public interface ShopContactService {
    List<ShopContact> getAll();
    String updateAll(List<ShopContact> contacts);
    String update(ShopContact shopContact);
}
