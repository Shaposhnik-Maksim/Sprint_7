package api;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import models.CourierCredentialsWithoutLogin;
import models.CourierCredentialsWithoutPassword;


public class CourierApi {
    private static final String baseUrl = "https://qa-scooter.praktikum-services.ru/api/v1";

    static {
        RestAssured.baseURI = baseUrl;
    }

    public static Response createCourier(Courier courier) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/courier");
    }

    public static Response loginCourier(CourierCredentials credentials) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .post("/courier/login");
    }

    public static Response loginWithoutPassword(CourierCredentialsWithoutPassword credentials) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .post("/courier/login");
    }

    public static Response loginWithoutLogin(CourierCredentialsWithoutLogin credentials) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .post("/courier/login");
    }

    public static Response deleteCourier(int courierId) {
        return RestAssured.given()
                .delete("/courier/" + courierId);
    }
}