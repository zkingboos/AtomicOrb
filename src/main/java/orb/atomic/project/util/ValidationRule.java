package orb.atomic.project.util;

@FunctionalInterface
public interface ValidationRule<T> {

    T validate(final String key);

}
