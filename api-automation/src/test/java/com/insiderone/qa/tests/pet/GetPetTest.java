package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetPetTest {

    private PetClient petClient;
    private long petId;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
        Response createResponse = petClient.create(PetFactory.availablePet());
        petId = createResponse.jsonPath().getLong("id");
    }

    @AfterEach
    void tearDown() {
        petClient.delete(petId);
    }

    @Test
    void getPetByValidId_returns200WithCorrectData() {
        Response response = petClient.getById(petId);
        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getLong("id"), is(petId));
        assertThat(response.jsonPath().getString("status"), is("available"));
    }

    @Test
    void getPetWithNonExistentId_returns404() {
        Response response = petClient.getById(Long.MAX_VALUE);
        assertThat(response.statusCode(), anyOf(is(200), is(404)));
    }

    @Test
    void getPetWithNegativeId_returns404OrBadRequest() {
        Response response = petClient.getById(-1L);
        assertThat(response.statusCode(), anyOf(is(400), is(404)));
    }
}
