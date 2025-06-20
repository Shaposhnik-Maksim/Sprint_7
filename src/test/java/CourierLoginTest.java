import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;

import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";
    private String courierLogin = "MaksiBom1998";
    private String courierPassword = "1234";
    private String courierFirstName = "Maksim";
    private int courierId = -1;

    @Before
    @Step("Подготовка тестовых данных - создание курьера")
    public void setUp() {
        RestAssured.baseURI = baseUrl;

        // Создание курьера перед тестами
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);
        createCourier(courier);

        // Получение id созданного курьера
        Response response = loginCourier(courierLogin, courierPassword);
        courierId = response.path("id");
    }

    @After
    @Step("Очистка тестовых данных - удаление курьера")
    public void tearDown() {
        if (courierId != -1) {
            deleteCourier(courierId);
        }
    }

    @Test
    @Step("Тест успешного входа с валидными учетными данными")
    public void courierCanLoginWithValidCredentials() {
        loginCourier(courierLogin, courierPassword)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Step("Тест входа с неверным паролем")
    public void loginFailsWithWrongPassword() {
        loginCourier(courierLogin, "wrongpass")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест входа с неверным логином")
    public void loginFailsWithWrongLogin() {
        loginCourier("wronglogin", courierPassword)
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест входа без пароля")
    public void loginFailsWithoutPassword() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentialsWithoutPassword(courierLogin))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест входа без логина")
    public void loginFailsWithoutLogin() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentialsWithoutLogin(courierPassword))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест входа для несуществующего курьера")
    public void loginFailsForNonExistentCourier() {
        loginCourier("nonexistent", "whatever")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    // Вспомогательные методы с аннотациями Step
    @Step("Создание курьера {courier.login}")
    private void createCourier(Courier courier) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/courier");
    }

    @Step("Авторизация курьера {login}")
    private Response loginCourier(String login, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(login, password))
                .post("/courier/login");
    }

    @Step("Удаление курьера с id {courierId}")
    private void deleteCourier(int courierId) {
        RestAssured.given()
                .delete("/courier/" + courierId);
    }

    // POJO классы для сериализации
    private static class Courier {
        private final String login;
        private final String password;
        private final String firstName;

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
        private final String login;
        private final String password;

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

    private static class CourierCredentialsWithoutPassword {
        private final String login;

        public CourierCredentialsWithoutPassword(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    private static class CourierCredentialsWithoutLogin {
        private final String password;

        public CourierCredentialsWithoutLogin(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }
}