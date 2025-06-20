import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";
    private String courierLogin = "MaksiBom1998";
    private String courierPassword = "1234";
    private String courierFirstName = "Maksim";
    private int courierId = -1;

    @Before
    public void setUp() {
        RestAssured.baseURI = baseUrl;

        // Создание курьера перед тестами
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/courier");

        // Получение id
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(courierLogin, courierPassword))
                .post("/courier/login");

        courierId = response.path("id");
    }

    @After
    public void tearDown() {
        if (courierId != -1) {
            RestAssured.given()
                    .delete("/courier/" + courierId);
        }
    }

    @Test
    public void courierCanLoginWithValidCredentials() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(courierLogin, courierPassword))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void loginFailsWithWrongPassword() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(courierLogin, "wrongpass"))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    public void loginFailsWithWrongLogin() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials("wronglogin", courierPassword))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
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
    public void loginFailsForNonExistentCourier() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials("nonexistent", "whatever"))
                .when()
                .post("/courier/login")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
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