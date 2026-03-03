package com.insiderone.qa.tests.pet;

import com.insiderone.qa.client.PetClient;
import com.insiderone.qa.factory.PetFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FindByStatusTest {

    private PetClient petClient;
    private long petId;

    @BeforeEach
    void setUp() {
        petClient = new PetClient();
        var pet = PetFactory.availablePet();
        petId = pet.getId();
        petClient.create(pet);
    }

    @AfterEach
    void tearDown() {
        petClient.delete(petId);
    }

    @Test
    void findByStatusAvailable_returns200WithNonEmptyList() {
        Response response = petClient.findByStatus("available");
        assertThat(response.statusCode(), is(200));
        List<Map<String, Object>> pets = response.jsonPath().getList("$");
        assertThat(pets, not(empty()));
        pets.forEach(p -> assertThat(p.get("status"), is("available")));
    }

    @Test
    void findByStatusPending_returns200() {
        Response response = petClient.findByStatus("pending");
        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList("$"), instanceOf(List.class));
    }

    @Test
    void findByStatusSold_returns200() {
        Response response = petClient.findByStatus("sold");
        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList("$"), instanceOf(List.class));
    }

    @Test
    void findByInvalidStatus_doesNotReturn500() {
        Response response = petClient.findByStatus("not_a_real_status_xyz");
        assertThat(response.statusCode(), anyOf(is(400), is(200)));
        assertThat(response.statusCode(), not(is(500)));
    }
}
