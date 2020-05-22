package orb.atomic.project.manager;

import orb.atomic.project.util.ValidationRule;

import java.util.HashMap;
import java.util.Map;

public final class ClzTypeManager {

    private final Map<Class<?>, ValidationRule<?>> ruleMap = new HashMap<>();

    public Object supplierType(Class<?> clazz, Object value) {
        try {
            final ValidationRule<?> validationRule = ruleMap.get(clazz);
            if (validationRule == null) return null;

            return validationRule.validate((String) value);
        } catch (Exception e) {
            return null;
        }
    }

    public void registerType(Class<?> clazz, ValidationRule<?> rule) {
        ruleMap.put(clazz, rule);
    }
}
