package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class OrdersListTest {

    private static RequestSpecification requestSpec;

    @BeforeClass
    @Step("Настройка спецификации API")
    public static void setUp() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://qa-scooter.praktikum-services.ru/api/v1")
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.requestSpecification = requestSpec;
    }

    @Test
    @Step("Проверка получения непустого списка заказов")
    public void getOrdersListShouldReturnNonEmptyOrdersArray() {
        given()
                .spec(requestSpec)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", is(instanceOf(java.util.List.class)))
                .body("orders.size()", greaterThan(0));
    }
}