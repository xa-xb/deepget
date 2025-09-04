package my.iris.model;

/**
 * clone and sanitize object for logging
 * @param <T>
 */
public interface CloneAndSanitize<T extends CloneAndSanitize<T>> {
    T cloneAndSanitize();
}
