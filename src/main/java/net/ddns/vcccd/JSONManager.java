package net.ddns.vcccd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class JSONManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;

    public JSONManager(File pluginFolder) {
        this.file = new File("plugins/LandLord/PropertyLocations.json");

        // Ensure the file exists and contains an empty JSON array if it's new
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]"); // Initialize with an empty array
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveJson(Object object) {
        JsonArray jsonArray = loadJsonArray();

        // Check if the object already exists in the array
        if (!isObjectInArray(object, jsonArray)) {
            jsonArray.add(gson.toJsonTree(object));

            // Save the updated array back to the file
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(jsonArray, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeJson(Object object) {
        JsonArray jsonArray = loadJsonArray();

        // Remove matching object from the array
        JsonElement targetElement = gson.toJsonTree(object);
        JsonArray updatedArray = new JsonArray();

        for (JsonElement element : jsonArray) {
            if (!Objects.equals(element, targetElement)) {
                updatedArray.add(element);
            }
        }

        // Save the updated array back to the file
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(updatedArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> loadJson(Class<T> clazz) {
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            // Correctly load an array from the JSON
            return gson.fromJson(reader, TypeToken.getParameterized(List.class, clazz).getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonArray loadJsonArray() {
        if (!file.exists()) return new JsonArray();

        try (FileReader reader = new FileReader(file)) {
            JsonElement element = new JsonParser().parse(reader);
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }

    private boolean isObjectInArray(Object object, JsonArray jsonArray) {
        JsonElement newElement = gson.toJsonTree(object);
        for (JsonElement element : jsonArray) {
            if (Objects.equals(element, newElement)) {
                return true; // Object already exists
            }
        }
        return false;
    }
}
