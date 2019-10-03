package tk.npecode.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an option inside a configuration class and all of its properties.
 *
 * @author AmpTheDev
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {
    /**
     * Returns this option's serialized name.
     * Uses the field name if empty.
     *
     * @return the option's serialized name
     */
    String value() default "";
}
