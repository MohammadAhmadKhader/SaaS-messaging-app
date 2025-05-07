package com.example.multitenant.utils;

import java.util.Random;

import com.example.multitenant.models.User;
import com.github.javafaker.Faker;

public class FakeDataGenerator {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    private static User generateFakeUser() {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var email = faker.internet().emailAddress();

        var password = generateRandomPassword(6, 36);

        return new User(email, firstName, lastName, password);
    }
    
    private static String generateRandomPassword(int minLength, int maxLength) {
        var length = random.nextInt(maxLength - minLength + 1) + minLength; // Random length between min and max
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(94) + 33);
            password.append(randomChar);
        }

        return password.toString();
    }
}