package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DeletePetTest {

    private PetClient petClient;
    private long petId;
    private boolean petAlreadyDeleted;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
        petAlreadyDeleted = false;
        var pet = PetFactory.availablePet();
        petId = pet.getId();
        petClient.create(pet);
    }

    @AfterEach
    void tearDown() {
        if (!petAlreadyDeleted) {
            petClient.delete(petId);
        }
    }

    @Test
    void deleteExistingPet_returns200() {
        Response response = petClient.delete(petId);
        assertThat(response.statusCode(), is(200));
        petAlreadyDeleted = true;
    }

    @Test
    void deletedPet_isNoLongerRetrievable() {
        petClient.delete(petId);
        petAlreadyDeleted = true;

        Response getResponse = petClient.getById(petId);
        assertThat(getResponse.statusCode(), is(404));
    }

    @Test
    void deleteNonExistentPet_returns404() {
        long nonExistentId = Long.MAX_VALUE - 1;
        Response response = petClient.delete(nonExistentId);
        assertThat(response.statusCode(), is(404));
    }
}
