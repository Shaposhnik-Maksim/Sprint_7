package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;

public class CourierApi extends ApiClient {
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

    public static Response deleteCourier(int courierId) {
        return RestAssured.given()
                .delete("/courier/" + courierId);
    }
}