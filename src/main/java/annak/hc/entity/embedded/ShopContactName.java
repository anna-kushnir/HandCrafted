package annak.hc.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShopContactName {
    PHONE_NUMBER_1(1, "Номер телефону 1"),
    PHONE_NUMBER_2(2, "Номер телефону 2"),
    TELEGRAM(3, "Telegram"),
    INSTAGRAM(4, "Instagram"),
    FACEBOOK(5, "Facebook");

    private final int id;
    private final String name;
}
