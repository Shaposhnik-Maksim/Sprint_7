package tests;

import io.qameta.allure.junit4.DisplayName;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.CourierSteps;

public class CourierLoginTest {
    private static final String COURIER_LOGIN = "MaksiBom1998";
    private static final String COURIER_PASSWORD = "1234";
    private static final String COURIER_FIRST_NAME = "Maksim";

    private int courierId = -1;

    @Before
    public void setUp() {
        // Создание курьера перед тестами
        Courier courier = new Courier(COURIER_LOGIN, COURIER_PASSWORD, COURIER_FIRST_NAME);
        CourierSteps.createCourier(courier);

        // Получение id созданного курьера
        courierId = CourierSteps.verifySuccessfulLogin(
                CourierSteps.loginCourier(new CourierCredentials(COURIER_LOGIN, COURIER_PASSWORD))
        );
    }

    @After
    public void tearDown() {
        if (courierId != -1) {
            CourierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешный вход с валидными учетными данными")
    public void courierCanLoginWithValidCredentials() {
        CourierSteps.verifySuccessfulLogin(
                CourierSteps.loginCourier(new CourierCredentials(COURIER_LOGIN, COURIER_PASSWORD))
        );
    }

    @Test
    @DisplayName("Ошибка при входе с неверным паролем")
    public void loginFailsWithWrongPassword() {
        CourierSteps.verifyLoginError(
                CourierSteps.loginCourier(new CourierCredentials(COURIER_LOGIN, "wrongpass")),
                "Учетная запись не найдена"
        );
    }

    @Test
    @DisplayName("Ошибка при входе с неверным логином")
    public void loginFailsWithWrongLogin() {
        CourierSteps.verifyLoginError(
                CourierSteps.loginCourier(new CourierCredentials("wronglogin", COURIER_PASSWORD)),
                "Учетная запись не найдена"
        );
    }

    @Test
    @DisplayName("Ошибка при входе без пароля")
    public void loginFailsWithoutPassword() {
        CourierSteps.verifyMissingLoginDataError(
                CourierSteps.loginWithoutPassword(COURIER_LOGIN)
        );
    }

    @Test
    @DisplayName("Ошибка при входе без логина")
    public void loginFailsWithoutLogin() {
        CourierSteps.verifyMissingLoginDataError(
                CourierSteps.loginWithoutLogin(COURIER_PASSWORD)
        );
    }

    @Test
    @DisplayName("Ошибка при входе для несуществующего курьера")
    public void loginFailsForNonExistentCourier() {
        CourierSteps.verifyLoginError(
                CourierSteps.loginCourier(new CourierCredentials("nonexistent", "whatever")),
                "Учетная запись не найдена"
        );
    }
}