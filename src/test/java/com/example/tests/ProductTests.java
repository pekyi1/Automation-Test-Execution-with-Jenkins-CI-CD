package com.example.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductTests extends BaseTest {

    @Test
    @DisplayName("Retrieve a list of all available products.")
    public void getAllProducts() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    @DisplayName("Retrieve details of a specific product by ID.")
    public void getProductById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("title", notNullValue());
    }

    @Test
    @DisplayName("Create a new product.")
    public void addProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("title", "New Laptop");
        product.put("price", 999.99);
        product.put("description", "A high-performance gaming laptop.");
        product.put("image", "https://i.pravatar.cc");
        product.put("category", "electronics");

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("title", equalTo("New Laptop"));
    }

    @Test
    @DisplayName("Update an existing product by ID.")
    public void updateProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("title", "Updated Product Name");
        product.put("price", 45.0);

        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Product Name"));
    }

    @Test
    @DisplayName("Delete a specific product by ID.")
    public void deleteProduct() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(200);
    }
}
