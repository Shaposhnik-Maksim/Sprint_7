package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.CourierSteps;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierLoginTest {
    private static final String courierLogin = "MaksiBom1998_" + System.currentTimeMillis();
    private static final String courierPassword = "1234";
    private static final String courierFirstName = "Maksim";

    private int courierId;

    @Before
    public void setUp() {
        // Создаем уникального курьера для каждого запуска тестов
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);
        Response createResponse = CourierSteps.createCourier(courier);
        createResponse.then().statusCode(201);

        // Получаем ID через авторизацию
        Response loginResponse = CourierSteps.loginCourier(
                new CourierCredentials(courierLogin, courierPassword)
        );

        courierId = loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");

        assertThat("Courier ID должен быть положительным числом", courierId, greaterThan(0));
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            CourierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешная авторизация с валидными данными")
    public void successfulLoginWithValidCredentials() {
        Response response = CourierSteps.loginCourier(
                new CourierCredentials(courierLogin, courierPassword)
        );

        response.then()
                .statusCode(200)
                .body("id", equalTo(courierId));
    }

    @Test
    @DisplayName("Ошибка при входе с неверным паролем")
    public void loginFailsWithWrongPassword() {
        Response response = CourierSteps.loginCourier(
                new CourierCredentials(courierLogin, "wrongpass")
        );

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при входе с неверным логином")
    public void loginFailsWithWrongLogin() {
        Response response = CourierSteps.loginCourier(
                new CourierCredentials("wronglogin", courierPassword)
        );

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при входе без пароля")
    public void loginFailsWithoutPassword() {
        Response response = CourierSteps.loginWithoutPassword(courierLogin);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при входе без логина")
    public void loginFailsWithoutLogin() {
        Response response = CourierSteps.loginWithoutLogin(courierPassword);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при входе для несуществующего курьера")
    public void loginFailsForNonExistentCourier() {
        Response response = CourierSteps.loginCourier(
                new CourierCredentials("nonexistent_" + System.currentTimeMillis(), "whatever")
        );

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}