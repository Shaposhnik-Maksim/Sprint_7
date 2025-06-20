import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierCreationTest {

    private String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";
    private String courierLogin = "MaksiBom1998";
    private String courierPassword = "1234";
    private String courierFirstName = "Maksim";
    private int createdCourierId = -1;

    @Before
    public void setUp() {
        RestAssured.baseURI = baseUrl;
    }

    @After
    public void tearDown() {
        if (createdCourierId != -1) {
            // Удаляем курьера после теста
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(new CourierCredentials (courierLogin, courierPassword))
                    .when()
                    .post("/courier/login")
                    .then()
                    .statusCode(200);

            RestAssured
                    .given()
                    .when()
                    .delete("/courier/" + createdCourierId)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    public void createCourierSuccessfully() {
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Авторизация, чтобы получить id курьера для удаления
        Response loginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(courierLogin, courierPassword))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .response();

        createdCourierId = loginResponse.path("id");
    }


    @Test
    public void cannotCreateDuplicateCourier() {
        // Cоздаем курьера
        createCourierSuccessfully();

        // Пытаемся создать курьера с таким же логином
        Courier duplicateCourier = new Courier(courierLogin, courierPassword, courierFirstName);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(duplicateCourier)
                .when()
                .post("/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    public void cannotCreateCourierWithoutRequiredFields() {
        // Создаём без поля password
        Courier invalidCourier = new Courier(courierLogin, null, courierFirstName);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidCourier)
                .when()
                .post("/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
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

        // Геттеры необходимы для сериализации
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