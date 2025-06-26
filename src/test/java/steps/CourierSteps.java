package steps;

import api.CourierApi;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;

public class CourierSteps {
    @Step("Создать курьера")
    public static Response createCourier(Courier courier) {
        return CourierApi.createCourier(courier);
    }

    @Step("Авторизовать курьера")
    public static Response loginCourier(CourierCredentials credentials) {
        return CourierApi.loginCourier(credentials);
    }

    @Step("Удалить курьера")
    public static void deleteCourier(int courierId) {
        CourierApi.deleteCourier(courierId);
    }

    @Step("Авторизация курьера без пароля")
    public static Response loginWithoutPassword(String login) {
        return CourierApi.loginCourier(new CourierCredentials(login, null));
    }

    @Step("Авторизация курьера без логина")
    public static Response loginWithoutLogin(String password) {
        return CourierApi.loginCourier(new CourierCredentials(null, password));
    }
}