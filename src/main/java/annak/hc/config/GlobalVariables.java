package annak.hc.config;

import java.math.BigDecimal;

public class GlobalVariables {

    private GlobalVariables() {
        throw new IllegalStateException("Utility class");
    }

    public static final BigDecimal WRAP_PRICE = BigDecimal.valueOf(50);

    public final static String MESSAGE = "message";
    public final static String PRODUCTS = "products";
    public final static String CATEGORIES = "categories";
    public final static String ORDERS = "orders";
    public final static String IS_AUTHENTICATED = "isAuthenticated";
}
