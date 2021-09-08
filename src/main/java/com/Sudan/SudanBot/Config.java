package com.Sudan.SudanBot;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();
    public static String get(String key) {
        return dotenv.get(key.replace(' ', '_').toUpperCase());
    }
    public static String get(String key, String value) {
        return dotenv.get(key.replace(' ', '_').toUpperCase(), value);
    }
}
