package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class CourierSteps {
    @Step("Создать курьера")
    public static Response createCourier(Courier courier) {
        return given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(courier)
                .post("/courier");
    }

    @Step("Авторизовать курьера")
    public static Response loginCourier(CourierCredentials credentials) {
        return given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body(credentials)
                .post("/courier/login");
    }

    @Step("Удалить курьера")
    public static void deleteCourier(int courierId) {
        given()
                .baseUri(baseURI)
                .delete("/courier/" + courierId);
    }

    public static Response loginWithoutPassword(String login) {
        return given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body("{\"login\":\"" + login + "\"}")
                .post("/courier/login");
    }

    public static Response loginWithoutLogin(String password) {
        return given()
                .baseUri(baseURI)
                .contentType("application/json")
                .body("{\"password\":\"" + password + "\"}")
                .post("/courier/login");
    }
}