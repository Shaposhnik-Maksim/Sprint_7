package tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import steps.OrderSteps;
import static org.hamcrest.Matchers.*;

public class OrdersListTest {

    @Test
    @DisplayName("Проверка получения непустого списка заказов")
    public void getOrdersListShouldReturnNonEmptyOrdersArray() {
        OrderSteps.getOrdersList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", is(instanceOf(java.util.List.class)))
                .body("orders.size()", greaterThan(0));
    }
}