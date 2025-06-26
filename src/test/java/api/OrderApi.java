package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Order;

public class OrderApi extends ApiClient {
    public static Response createOrder(Order order) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(order)
                .post("/orders");
    }

    public static Response getOrdersList() {
        return RestAssured.given()
                .get("/orders");
    }
}