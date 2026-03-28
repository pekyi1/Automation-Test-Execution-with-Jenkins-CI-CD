package com.example.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseTest {

    @Test
    @DisplayName("Verify user login and retrieval of authentication token.")
    public void userLogin() {
        Map<String, String> authData = new HashMap<>();
        authData.put("username", "mor_2314");
        authData.put("password", "83r5^_");

        given()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(201)
                .body("token", notNullValue());
    }
}
