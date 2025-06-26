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

public class CourierCreationTest {
    private static final String courierLogin = "MaksiBom1998";
    private static final String courierPassword = "1234";
    private static final String courierFirstName = "Maksim";

    private int createdCourierId = -1;

    @After
    @DisplayName("Удаление созданного курьера")
    public void tearDown() {
        if (createdCourierId != -1) {
            CourierSteps.deleteCourier(createdCourierId);
        }
    }

    @Test
    @DisplayName("Тест успешного создания курьера")
    public void createCourierSuccessfully() {
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);

        CourierSteps.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        createdCourierId = CourierSteps.loginCourier(new CourierCredentials(courierLogin, courierPassword))
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Тест невозможности создания дубликата курьера")
    public void cannotCreateDuplicateCourier() {
        Courier courier = new Courier(courierLogin, courierPassword, courierFirstName);
        CourierSteps.createCourier(courier);
        createdCourierId = CourierSteps.loginCourier(new CourierCredentials(courierLogin, courierPassword))
                .then()
                .extract()
                .path("id");

        CourierSteps.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Тест невозможности создания курьера без обязательных полей")
    public void cannotCreateCourierWithoutRequiredFields() {
        Courier invalidCourier = new Courier(courierLogin, null, courierFirstName);

        CourierSteps.createCourier(invalidCourier)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }
}