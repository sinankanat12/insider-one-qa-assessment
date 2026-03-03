package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import com.insiderone.qa.model.Pet;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpdatePetTest {

    private PetClient petClient;
    private long petId;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
        Pet pet = PetFactory.availablePet();
        petId = pet.getId();
        petClient.create(pet);
    }

    @AfterEach
    void tearDown() {
        petClient.delete(petId);
    }

    @Test
    void updatePetName_returns200AndNamePersists() {
        Pet updatePayload = PetFactory.petWithId(petId);
        updatePayload.setName("UpdatedName");
        Response putResponse = petClient.update(updatePayload);
        assertThat(putResponse.statusCode(), is(200));

        Response getResponse = petClient.getById(petId);
        assertThat(getResponse.jsonPath().getString("name"), is("UpdatedName"));
    }

    @Test
    void updatePetStatus_returns200AndStatusPersists() {
        Pet updatePayload = PetFactory.petWithId(petId);
        updatePayload.setStatus("sold");
        Response putResponse = petClient.update(updatePayload);
        assertThat(putResponse.statusCode(), is(200));

        Response getResponse = petClient.getById(petId);
        assertThat(getResponse.jsonPath().getString("status"), is("sold"));
    }
}
