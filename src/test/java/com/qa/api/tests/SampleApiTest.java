package com.qa.api.tests;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Sample API tests using RestAssured.
 * Replace BASE_URL with your API (e.g. https://jsonplaceholder.typicode.com for a public demo).
 */
public class SampleApiTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test(description = "GET /posts returns 200 and non-empty list")
    public void getPosts_returns200() {
        given()
            .when()
            .get("/posts")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].userId", notNullValue())
            .body("[0].title", notNullValue());
    }

    @Test(description = "GET /posts/1 returns single post")
    public void getPostById_returns200() {
        given()
            .when()
            .get("/posts/1")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("userId", notNullValue())
            .body("title", notNullValue())
            .body("body", notNullValue());
    }
}
