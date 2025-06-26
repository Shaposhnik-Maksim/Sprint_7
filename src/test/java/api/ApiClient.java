package api;

import io.restassured.RestAssured;
public class ApiClient {
    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    static {
        RestAssured.baseURI = BASE_URL;
    }
}