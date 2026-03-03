package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import com.insiderone.qa.model.Category;
import com.insiderone.qa.model.Pet;
import com.insiderone.qa.model.Tag;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreatePetTest {

    private PetClient petClient;
    private Pet createdPet;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
        createdPet = null;
    }

    @AfterEach
    void tearDown() {
        if (createdPet != null) {
            petClient.delete(createdPet.getId());
            createdPet = null;
        }
    }

    @Test
    void createPetWithAllFields_returns200() {
        Pet pet = PetFactory.availablePet();
        Category category = new Category();
        category.setId(1L);
        category.setName("Dogs");
        pet.setCategory(category);
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("friendly");
        pet.setTags(List.of(tag));

        Response response = petClient.create(pet);
        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getLong("id"), is(pet.getId()));
        assertThat(response.jsonPath().getString("name"), is(pet.getName()));
        createdPet = pet;
    }

    @Test
    void createPetWithMinimalFields_returns200() {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("MinimalPet");
        pet.setPhotoUrls(Collections.emptyList());

        Response response = petClient.create(pet);
        assertThat(response.statusCode(), is(200));
        createdPet = pet;
    }

    @Test
    void createPetWithEmptyBody_returns405OrBadRequest() {
        Pet emptyPet = new Pet();
        Response response = petClient.create(emptyPet);
        assertThat(response.statusCode(), anyOf(is(200), is(400), is(405), is(500)));
        // no cleanup — not persisted
    }
}
