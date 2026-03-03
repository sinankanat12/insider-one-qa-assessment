package com.insiderone.qa.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.http.HttpResponseException;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.function.Supplier;

public abstract class BaseClient {

    protected BaseClient() {}

    protected static final String BASE_URL = "https://petstore.swagger.io/v2";

    static {
        RestAssured.baseURI = BASE_URL;
    }

    protected RequestSpecification requestSpec() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new AllureRestAssured());
    }

    protected Response execute(Supplier<Response> call) {
        try {
            return call.get();
        } catch (Exception e) {
            if (e instanceof HttpResponseException hre) {
                return new ResponseBuilder()
                        .setStatusCode(hre.getResponse().getStatus())
                        .setStatusLine("HTTP/1.1 " + hre.getResponse().getStatus())
                        .setBody("")
                        .build();
            }
            throw new RuntimeException("Unexpected HTTP error", e);
        }
    }
}
