package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.Type;
import java.time.LocalTime;

public class Database {
    private static final String DATA_DIR = "data";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            // Register LocalTime adapter to handle serialization/deserialization
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                            context.serialize(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                            LocalTime.parse(json.getAsString()))
            .create();

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    public static <T> void saveToFile(String filename, T data) throws IOException {
        Path path = Paths.get(DATA_DIR, filename);
        String json = gson.toJson(data);
        Files.write(path, json.getBytes());
    }

    // Overloaded method for Type (used with TypeToken for generic collections)
    public static <T> T loadFromFile(String filename, Type type) throws IOException {
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) {
            return null;
        }
        String json = new String(Files.readAllBytes(path));
        if (json.trim().isEmpty() || json.trim().equals("[]")) {
            return null;
        }
        return gson.fromJson(json, type);
    }

    // Original method for Class
    public static <T> T loadFromFile(String filename, Class<T> clazz) throws IOException {
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) {
            return null;
        }
        String json = new String(Files.readAllBytes(path));
        if (json.trim().isEmpty() || json.trim().equals("[]")) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }
}