package com.example.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTests extends BaseTest {

    @Test
    @DisplayName("Retrieve a list of all users.")
    public void getAllUsers() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Retrieve details of a specific user by ID.")
    public void getUserById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("username", notNullValue());
    }

    @Test
    @DisplayName("Create a new user.")
    public void addUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("email", "johndoe@gmail.com");
        user.put("username", "johnd");
        user.put("password", "m38rmF$");

        Map<String, String> name = new HashMap<>();
        name.put("firstname", "John");
        name.put("lastname", "Doe");
        user.put("name", name);

        Map<String, Object> address = new HashMap<>();
        address.put("city", "kilcoole");
        address.put("street", "7835 new road");
        address.put("number", 3);
        address.put("zipcode", "12926-3874");

        Map<String, String> geolocation = new HashMap<>();
        geolocation.put("lat", "-37.3159");
        geolocation.put("long", "81.1496");
        address.put("geolocation", geolocation);
        user.put("address", address);

        user.put("phone", "1-570-236-7033");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Update an existing user by ID.")
    public void updateUser() {
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("email", "updated_johndoe@gmail.com");

        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .body(userUpdate)
                .when()
                .put("/users/{id}")
                .then()
                .statusCode(200)
                .body("email", equalTo("updated_johndoe@gmail.com"));
    }

    @Test
    @DisplayName("Delete a specific user by ID.")
    public void deleteUser() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(200);
    }
}
