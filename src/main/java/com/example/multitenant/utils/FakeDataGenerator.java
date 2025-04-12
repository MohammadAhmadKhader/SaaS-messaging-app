package com.example.multitenant.utils;

import java.util.Random;

import com.example.multitenant.models.User;
import com.github.javafaker.Faker;

public class FakeDataGenerator {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static User generateFakeUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress(); // Generate an email
        // Generate a password with length between 6 and 36
        String password = generateRandomPassword(6, 36);
        

        return new User(email, firstName, lastName, password);
    }

    private static String generateRandomPassword(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength; // Random length between min and max
        StringBuilder password = new StringBuilder(length);

        // Generate a random password
        for (int i = 0; i < length; i++) {
            // Randomly choose a character from a set of characters
            char randomChar = (char) (random.nextInt(94) + 33); // ASCII range from 33 to 126
            password.append(randomChar);
        }

        return password.toString();
    }
}

