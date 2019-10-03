package tk.npecode.config.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for other classes and objects
 *
 * @author AmpTheDev
 */
public final class ClassUtils {
    /**
     * An array of all primitive types (and {@link String})
     */
    private static final Class<?>[] PRIMITIVE_CLASSES = {
            Boolean.class,
            boolean.class,
            Byte.class,
            byte.class,
            Character.class,
            char.class,
            Short.class,
            short.class,
            Integer.class,
            int.class,
            Long.class,
            long.class,
            Float.class,
            float.class,
            Double.class,
            double.class,
            String.class
    };

    /**
     * Since this is a static utility class, we don't want to be able to initiate instances of it.
     */
    private ClassUtils() {
    }

    /**
     * Determines if a class is a primitive.
     *
     * @param clazz the class
     * @return {@code true} if it's a primitive, {@code false} if it isn't.
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isArray()) {
            return isPrimitive(clazz.getComponentType());
        }
        for (Class<?> primitiveClass : PRIMITIVE_CLASSES) {
            if (primitiveClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a object has recursive fields.
     *
     * @param obj the object
     * @return {@code true} if it has recursive fields, {@code false} if it doesn't.
     */
    public static boolean hasRecursiveFields(Object obj) {
        return hasRecursiveFields(obj, new ArrayList<>());
    }

    /**
     * Determines if a object has recursive fields.
     *
     * @param obj the object
     * @param previous the objects to check for
     * @return {@code true} if it has recursive fields, {@code false} if it doesn't.
     */
    public static boolean hasRecursiveFields(Object obj, List<Object> previous) {
        /*previous.add(obj);
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object o = field.get(obj);
                if (previous.contains(o)) {
                    return true;
                }
                if (hasRecursiveFields(o, previous)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        previous.remove(obj);*/
        return false; // TODO: Make this code work
    }
}
