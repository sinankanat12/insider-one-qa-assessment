package com.insiderone.qa.client;

import com.insiderone.qa.model.Pet;
import io.restassured.response.Response;

public class PetClient extends BaseClient {

    public Response create(Pet pet) {
        return execute(() -> requestSpec()
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .extract()
                .response());
    }

    public Response getById(long id) {
        return execute(() -> requestSpec()
                .pathParam("id", id)
                .when()
                .get("/pet/{id}")
                .then()
                .extract()
                .response());
    }

    public Response update(Pet pet) {
        return execute(() -> requestSpec()
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .extract()
                .response());
    }

    public Response delete(long id) {
        return execute(() -> requestSpec()
                .pathParam("id", id)
                .when()
                .delete("/pet/{id}")
                .then()
                .extract()
                .response());
    }

    public Response findByStatus(String status) {
        return execute(() -> requestSpec()
                .queryParam("status", status)
                .when()
                .get("/pet/findByStatus")
                .then()
                .extract()
                .response());
    }
}
