package annak.hc.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    IN_PROCESSING(1, "В обробці"),
    ACCEPTED(2, "Підтверджено"),
    FORWARDED_FOR_DELIVERY(3, "Передано на доставку"),
    RECEIVED(4, "Отримано");

    private final int id;
    private final String name;
}
