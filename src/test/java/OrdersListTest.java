import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrdersListTest {

    @Before
    @Step("Настройка базового URL API")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @Step("Проверка получения списка заказов")
    public void getOrdersListShouldReturnOrdersArray() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", is(instanceOf(java.util.List.class)));
    }
}