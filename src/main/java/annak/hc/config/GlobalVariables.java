package annak.hc.config;

import java.math.BigDecimal;

public class GlobalVariables {

    private GlobalVariables() {
        throw new IllegalStateException("Utility class");
    }

    public static final BigDecimal WRAP_PRICE = BigDecimal.valueOf(50);

    public static final String MESSAGE = "message";
    public static final String PRODUCTS = "products";
    public static final String CATEGORIES = "categories";
    public static final String ORDERS = "orders";
    public static final String IS_AUTHENTICATED = "isAuthenticated";
}
