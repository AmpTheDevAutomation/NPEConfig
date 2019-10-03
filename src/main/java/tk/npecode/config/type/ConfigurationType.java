package tk.npecode.config.type;

import tk.npecode.config.Configuration;

import java.io.IOException;

/**
 * A configuration type, for example JSON.
 *
 * @author AmpTheDev
 */
public interface ConfigurationType {
    /**
     * Initializes this configuration type
     *
     * @param config the configuration object
     */
    void initialize(Configuration config);

    /**
     * Reloads the cached configuration data.
     */
    void reload() throws IOException;

    /**
     * Flushes the updated configuration data
     */
    void flush() throws IOException;

    /**
     * Returns a value from this configuration.
     *
     * @param category the category
     * @param name     the name
     * @param clazz    the value type
     * @param <T>      the value type but it's a type parameter
     * @return the value ({@code null} if it doesn't exist)
     */
    <T> T get(String category, String name, Class<T> clazz);

    /**
     * Sets a value in this configuration.
     *
     * @param category the category
     * @param name     the name
     * @param obj      the object
     */
    void set(String category, String name, Object obj);

    /**
     * Returns the display name of this configuration type
     *
     * @return the display name
     */
    String getName();

    /**
     * Determines if this configuration type can store this type of object
     *
     * @param clazz the object type
     * @return {@code null} if the object type can be stored, if it can't be stored the reason why it can't be stored
     */
    String canStore(Class<?> clazz);

    /**
     * Determines if this configuration type can store this object
     *
     * @param obj the object
     * @return {@code null} if the object can be stored, if it can't be stored the reason why it can't be stored
     */
    String canStore(Object obj);
}
