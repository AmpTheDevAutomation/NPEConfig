package tk.npecode.config.type.builtin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.npecode.config.Configuration;
import tk.npecode.config.type.ConfigurationType;
import tk.npecode.config.utils.ClassUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Gson-based JSON configuration backend.
 *
 * @author AmpTheDev
 */
public class JSONConfigurationType implements ConfigurationType {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser PARSER = new JsonParser();
    private final File jsonFile;

    public JSONConfigurationType(File jsonFile) {
        this.jsonFile = jsonFile.getAbsoluteFile();
    }

    private JsonObject object = new JsonObject();

    @Override
    public void initialize(Configuration config) {

    }

    @Override
    public void reload() throws IOException {
        object = new JsonObject();
        if (jsonFile.exists()) {
            FileReader reader = new FileReader(jsonFile);
            JsonElement element = PARSER.parse(reader);
            reader.close();
            if (element.isJsonObject()) {
                object = element.getAsJsonObject();
            }
        }
    }

    @Override
    public void flush() throws IOException {
        File parentFile = jsonFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IllegalStateException("Failed to create directory " + parentFile.getPath());
            }
        }
        FileWriter writer = new FileWriter(jsonFile);
        GSON.toJson(object, writer);
        writer.close();
    }

    @Override
    public <T> T get(String category, String name, Class<T> clazz) {
        JsonElement element = object.get(category);
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject object = element.getAsJsonObject();
        JsonElement element1 = object.get(name);
        if (element1 == null) return null;
        return GSON.fromJson(element1, clazz);
    }

    @Override
    public void set(String category, String name, Object obj) {
        JsonElement element = object.get(category);
        if (element == null || !element.isJsonObject()) {
            element = new JsonObject();
            object.add(category, element);
        }
        JsonObject object = element.getAsJsonObject();
        object.add(name, GSON.toJsonTree(obj));
    }

    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public String canStore(Class<?> clazz) {
        return null;
    }

    @Override
    public String canStore(Object obj) {
        return ClassUtils.hasRecursiveFields(obj) ? "the object has recursive fields" : null;
    }
}
