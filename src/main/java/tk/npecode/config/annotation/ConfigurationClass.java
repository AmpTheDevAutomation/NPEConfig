package tk.npecode.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a configuration class and all of its properties.
 *
 * @author AmpTheDev
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationClass {
    /**
     * Returns the category name of this configuration class.
     * If empty, the full name of this class is used.
     *
     * @return the category name
     */
    String value() default "";

    /**
     * Returns whether this configuration should have no category.
     * Only effective when value is set to an empty string or not set at all.
     *
     * @return {@code true} if it should be inside a category, {@code false} if it shouldn't be inside one.
     */
    boolean hasCategory() default false;
}
