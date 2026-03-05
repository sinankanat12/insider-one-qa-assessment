package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import com.insiderone.qa.model.Pet;
import com.insiderone.qa.model.Tag;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void updatePetWithDuplicateTags_CheckDataConsistency() {
        Pet updatePayload = PetFactory.petWithId(petId);

        Tag tag1 = new Tag();
        tag1.setId(5L);
        tag1.setName("VIP");

        Tag tag2 = new Tag();
        tag2.setId(5L);
        tag2.setName("VIP");

        updatePayload.setTags(List.of(tag1, tag2));

        Response putResponse = petClient.update(updatePayload);
        assertThat(putResponse.statusCode(), is(200));

        Response getResponse = petClient.getById(petId);
        assertThat(getResponse.jsonPath().getList("tags").size(), greaterThanOrEqualTo(1));
    }

    @Test
    void updatePetWithNonExistentId_UsingFormData_Returns404() {
        long nonExistentId = 5343535453534535313L;

        Response postResponse = petClient.updateWithFormData(nonExistentId, "GhostPet", "sold");

        assertThat(postResponse.statusCode(), is(404));

        assertThat(postResponse.jsonPath().getInt("code"), is(404));
        assertThat(postResponse.jsonPath().getString("type"), is("unknown"));
        assertThat(postResponse.jsonPath().getString("message"), is("not found"));
    }

    @Test
    void updateExistingPet_UsingFormData_Returns200AndPersists() {
        String newName = "FormDataPet";
        String newStatus = "pending";

        Response postResponse = petClient.updateWithFormData(petId, newName, newStatus);

        assertThat(postResponse.statusCode(), is(200));

        Response getResponse = petClient.getById(petId);
        assertThat(getResponse.jsonPath().getString("name"), is(newName));
        assertThat(getResponse.jsonPath().getString("status"), is(newStatus));
    }
}
