package com.example.multitenant.utils;

public class ConsoleColorUtils {
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    public static String blue(String message) {
        return BLUE + message + RESET;
    }

    public static String green(String message) {
        return GREEN + message + RESET;
    }

    public static String cyan(String message) {
        return CYAN + message + RESET;
    }

    public static String yellow(String message) {
        return YELLOW + message + RESET;
    }

    public static String red(String message) {
        return RED + message + RESET;
    }

    public static String blueNoReset(String message) {
        return BLUE + message;
    }

    public static String greenNoReset(String message) {
        return GREEN + message;
    }

    public static String cyanNoReset(String message) {
        return CYAN + message;
    }

    public static String yellowNoReset(String message) {
        return YELLOW + message;
    }

    public static String redNoReset(String message) {
        return RED + message;
    }

    public static String reset(String message) {
        return message + RESET;
    }
}
