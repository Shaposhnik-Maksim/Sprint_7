import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;

import static org.hamcrest.Matchers.*;

public class CourierCreationTest {

    private String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";
    private String courierLogin = "MaksiBom1998";
    private String courierPassword = "1234";
    private String courierFirstName = "Maksim";
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
            // Удаляем курьера после теста
            loginCourier(courierLogin, courierPassword);
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

        // Авторизация, чтобы получить id курьера для удаления
        Response loginResponse = loginCourier(courierLogin, courierPassword)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .response();

        createdCourierId = loginResponse.path("id");
    }

    @Test
    @Step("Тест невозможности создания дубликата курьера")
    public void cannotCreateDuplicateCourier() {
        // Создаем курьера
        createCourierSuccessfully();

        // Пытаемся создать курьера с таким же логином
        Courier duplicateCourier = new Courier(courierLogin, courierPassword, courierFirstName);

        createNewCourier(duplicateCourier)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @Step("Тест невозможности создания курьера без обязательных полей")
    public void cannotCreateCourierWithoutRequiredFields() {
        // Создаём без поля password
        Courier invalidCourier = new Courier(courierLogin, null, courierFirstName);

        createNewCourier(invalidCourier)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создание нового курьера")
    private Response createNewCourier(Courier courier) {
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
                .body(new CourierCredentials(login, password))
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

    private static class Courier {
        private String login;
        private String password;
        private String firstName;

        public Courier(String login, String password, String firstName) {
            this.login = login;
            this.password = password;
            this.firstName = firstName;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getFirstName() {
            return firstName;
        }
    }

    private static class CourierCredentials {
        private String login;
        private String password;

        public CourierCredentials(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }
}