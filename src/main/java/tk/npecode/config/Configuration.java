package tk.npecode.config;

import tk.npecode.config.annotation.ConfigurationClass;
import tk.npecode.config.annotation.Option;
import tk.npecode.config.type.ConfigurationType;
import tk.npecode.config.type.builtin.JSONConfigurationType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic configuration class that can have multiple types of backends.
 *
 * @author AmpTheDev
 */
public class Configuration {
    /**
     * The configuration backend used.
     */
    private final ConfigurationType type;
    /**
     * The classes with configuration options to store
     */
    private final List<Class<?>> configurationClasses = new ArrayList<>();

    /**
     * Whether this configuration has already been initialized.
     */
    private boolean initialized = false;

    /**
     * Constructs a configuration with the specified backend types.
     * If it's a built-in type, the newConfig methods are preferred.
     *
     * @param type the configuration type
     */
    public Configuration(ConfigurationType type) {
        this.type = type;
    }

    /**
     * Adds a class to the configuration.
     *
     * @param clazz the class to add
     * @return this configuration object
     */
    public Configuration addClass(Class<?> clazz) {
        ensureValidConfigurationClass(clazz);
        if (!configurationClasses.contains(clazz)) {
            configurationClasses.add(clazz);
        }
        return this;
    }

    /**
     * Returns the effective category name for a configuration class
     *
     * @param clazz      the class
     * @param annotation the class's annotation
     * @return the category name
     */
    private static String getEffectiveCategoryName(Class<?> clazz, ConfigurationClass annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " isn't a valid configuration class");
        }
        String name = annotation.value();
        return name.isEmpty() ? (annotation.hasCategory() ? clazz.getName() : null) : name;
    }

    /**
     * Saves this configuration.
     *
     * @return this configuration object
     * @throws IOException if it fails to save the configuration
     */
    public Configuration save() throws IOException {
        ensureInitialized();
        for (Class<?> clazz : configurationClasses) {
            ConfigurationClass annotation = clazz.getAnnotation(ConfigurationClass.class);
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (isValidConfigurationField(field) == null) {
                    try {
                        Object o = field.get(null);
                        if (type.canStore(o) == null) {
                            Option annotation2 = field.getAnnotation(Option.class);
                            String name = annotation2.value();
                            if (name.isEmpty()) name = field.getName();
                            type.set(getEffectiveCategoryName(clazz, annotation), name, o);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        type.flush();
        return this;
    }

    /**
     * Reloads this configuration.
     *
     * @return this configuration object
     * @throws IOException if it fails to reload the configuration
     */
    public Configuration reload() throws IOException {
        ensureInitialized();
        type.reload();
        for (Class<?> clazz : configurationClasses) {
            ConfigurationClass annotation = clazz.getAnnotation(ConfigurationClass.class);
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (isValidConfigurationField(field) == null) {
                    try {
                        Option annotation2 = field.getAnnotation(Option.class);
                        String name = annotation2.value();
                        if (name.isEmpty()) name = field.getName();
                        Object o = type.get(getEffectiveCategoryName(clazz, annotation), name, field.getType());
                        if (o != null) {
                            field.set(null, o);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return this;
    }

    /**
     * Initializes the configuration.
     *
     * @return this configuration object
     * @throws IOException if it fails to initialize the configuration
     */
    public Configuration initialize() throws IOException {
        if (!initialized) {
            type.initialize(this);
            initialized = true;
            reload().save();
        }
        return this;
    }

    /**
     * Deletes an entry from the configuration.
     * Useful if the {@code @ConfigurationClass} annotation has changed and you want to clean up the old values.
     *
     * @param category the category
     * @param name     the name
     * @return this configuration
     */
    public Configuration delete(String category, String name) {
        ensureInitialized();
        type.delete(category, name);
        return this;
    }

    /**
     * Returns an entry from this configuration using raw category and class name.
     * Useful if the {@code @ConfigurationClass} annotation has changed and you want to migrate the old values.
     *
     * @param category the category
     * @param name     the name
     * @param clazz    the value type
     * @param <T>      the value type
     * @return the value
     */
    public <T> T get(String category, String name, Class<T> clazz) {
        ensureInitialized();
        return type.get(category, name, clazz);
    }

    /**
     * Sets an entry in this configuration using raw category and class name.
     * Useful if the {@code @ConfigurationClass} has changed in a future release and you want to migrate the values now.
     *
     * @param category the category
     * @param name     the name
     * @param object   the value
     * @return this configuration
     */
    public Configuration set(String category, String name, Object object) {
        ensureInitialized();
        type.set(category, name, object);
        return this;
    }

    /**
     * Constructs a new JSON configuration.
     *
     * @param jsonFile the JSON file
     * @return the configuration
     */
    public static Configuration newJsonConfig(File jsonFile) {
        return new Configuration(new JSONConfigurationType(jsonFile));
    }

    /**
     * Returns the effective category name for a configuration class
     *
     * @param clazz the class
     * @return the category name
     */
    private static String getEffectiveCategoryName(Class<?> clazz) {
        return getEffectiveCategoryName(clazz, clazz.getAnnotation(ConfigurationClass.class));
    }

    /**
     * Throws an exception if the configuration hasn't been initialized.
     */
    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Configuration hasn't been initialized yet");
        }
    }

    /**
     * Ensures that a class is valid for the configuration
     *
     * @param clazz the class
     * @throws IllegalArgumentException if it isn't
     */
    private void ensureValidConfigurationClass(Class<?> clazz) {
        if (!isValidConfigurationClass(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " isn't a valid configuration class");
        }
    }

    /**
     * Determines if a class is a valid configuration class.
     *
     * @param clazz the class to determine
     * @return {@code true} if valid, {@code false} if not
     * @throws IllegalArgumentException if a field is annotated with @Option but can't be stored
     */
    private boolean isValidConfigurationClass(Class<?> clazz) {
        if (clazz.getAnnotation(ConfigurationClass.class) == null) return false;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Option.class) != null) {
                String reason = isValidConfigurationField(field);
                if (reason != null) {
                    throw new IllegalArgumentException("Field " + field.getName() + " in class " + clazz.getName() + " isn't a valid configuration option: " + reason);
                }
            }
        }
        return true;
    }

    /**
     * Determines if a field is a valid configuration field.
     *
     * @param field the field to determine
     * @return {@code null} if valid, the reason if it isn't
     */
    private String isValidConfigurationField(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) return "isn't static";
        if (field.getAnnotation(Option.class) == null) return "doesn't have the @Option annotation";
        String reason = type.canStore(field.getType());
        if (reason != null) return "backend " + type.getName() + " can't store the type: " + reason;
        return null;
    }
}
