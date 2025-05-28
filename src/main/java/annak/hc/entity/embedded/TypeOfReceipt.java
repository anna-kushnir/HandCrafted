package annak.hc.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeOfReceipt {
    PICKUP_FROM_THE_STORE(1, "Самовивіз з магазину"),
    DELIVERY_TO_THE_POST_OFFICE(2, "Доставка у поштове відділення");

    private final int id;
    private final String name;
}
