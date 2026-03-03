package com.insiderone.qa.factory;

import com.insiderone.qa.model.Pet;

import java.util.Collections;

public class PetFactory {

    private PetFactory() {}

    public static Pet availablePet() {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("TestDog");
        pet.setStatus("available");
        pet.setPhotoUrls(Collections.emptyList());
        pet.setTags(Collections.emptyList());
        return pet;
    }

    public static Pet pendingPet() {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("TestCat");
        pet.setStatus("pending");
        pet.setPhotoUrls(Collections.emptyList());
        pet.setTags(Collections.emptyList());
        return pet;
    }

    public static Pet soldPet() {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("TestBird");
        pet.setStatus("sold");
        pet.setPhotoUrls(Collections.emptyList());
        pet.setTags(Collections.emptyList());
        return pet;
    }

    public static Pet petWithId(long id) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName("TestDog");
        pet.setStatus("available");
        pet.setPhotoUrls(Collections.emptyList());
        pet.setTags(Collections.emptyList());
        return pet;
    }

    public static Pet petWithStatus(String status) {
        Pet pet = new Pet();
        pet.setId(System.currentTimeMillis());
        pet.setName("TestDog");
        pet.setStatus(status);
        pet.setPhotoUrls(Collections.emptyList());
        pet.setTags(Collections.emptyList());
        return pet;
    }
}
