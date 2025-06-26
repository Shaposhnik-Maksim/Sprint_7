package steps;

import api.OrderApi;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

public class OrderSteps {
    @Step("Создать заказ")
    public static Response createOrder(Order order) {
        return OrderApi.createOrder(order);
    }

    @Step("Получить список заказов")
    public static Response getOrdersList() {
        return OrderApi.getOrdersList();
    }
}