package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import com.insiderone.qa.model.Pet;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PetTechAndPerformanceTest {

    private PetClient petClient;
    private Long createdPetId;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
    }

    @AfterEach
    void tearDown() {
        if (createdPetId != null) {
            petClient.delete(createdPetId);
            createdPetId = null;
        }
    }

    @Test
    void getPet_ResponseTimeCheck_MustBeUnder3Seconds() {
        Pet pet = PetFactory.availablePet();
        Response createResponse = petClient.create(pet);
        createdPetId = createResponse.jsonPath().getLong("id");

        // GET isteğini atıyoruz
        Response getResponse = petClient.getById(createdPetId);
        assertThat(getResponse.statusCode(), is(200));

        // Yanıt süresi kontrolü (Performans)
        long responseTime = getResponse.timeIn(TimeUnit.MILLISECONDS);
        assertThat("Yanıt süresi 3000ms'den kısa olmalı", responseTime, lessThan(3000L));
    }

    @Test
    void getPet_CheckRequiredHeadersInResponse() {
        Pet pet = PetFactory.availablePet();
        Response createResponse = petClient.create(pet);
        createdPetId = createResponse.jsonPath().getLong("id");

        Response getResponse = petClient.getById(createdPetId);
        assertThat(getResponse.statusCode(), is(200));

        // Header (Teknik) kontrolleri
        assertThat(getResponse.header("Content-Type"), containsString("application/json"));
        assertThat(getResponse.header("Date"), notNullValue());
    }
}
