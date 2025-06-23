package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;
import static org.hamcrest.Matchers.*;


public class CourierCreationTest {

    private static final String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";
    private static final String courierLogin = "MaksiBom1998";
    private static final String courierPassword = "1234";
    private static final String courierFirstName = "Maksim";

    private int createdCourierId = -1;

    @Before
    @Step("Настройка базового URL")
    public void setUp() {
        RestAssured.baseURI = baseUrl;
    }

    @After
    @Step("Удаление созданного курьера")
    public void tearDown() {
        if (createdCourierId != -1) {
            deleteCourier(createdCourierId);
        }
    }

    @Test
    @Step("Тест успешного создания курьера")
    public void createCourierSuccessfully() {
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);

        createNewCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        createdCourierId = loginAndGetId(courierLogin, courierPassword);
    }

    @Test
    @Step("Тест невозможности создания дубликата курьера")
    public void cannotCreateDuplicateCourier() {
        // Создаем курьера
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);
        createNewCourier(courier);
        createdCourierId = loginAndGetId(courierLogin, courierPassword);

        // Пытаемся создать курьера с таким же логином
        createNewCourier(courier)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @Step("Тест невозможности создания курьера без обязательных полей")
    public void cannotCreateCourierWithoutRequiredFields() {
        // Создаём без поля password
        models.Courier invalidCourier = new models.Courier(courierLogin, null, courierFirstName);

        createNewCourier(invalidCourier)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Step("Получение ID курьера после авторизации")
    private int loginAndGetId(String login, String password) {
        return loginCourier(login, password)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Step("Создание нового курьера")
    private Response createNewCourier(models.Courier courier) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier");
    }

    @Step("Авторизация курьера")
    private Response loginCourier(String login, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new models.CourierCredentials(login, password))
                .when()
                .post("/courier/login");
    }

    @Step("Удаление курьера с id {courierId}")
    private void deleteCourier(int courierId) {
        RestAssured.given()
                .when()
                .delete("/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}