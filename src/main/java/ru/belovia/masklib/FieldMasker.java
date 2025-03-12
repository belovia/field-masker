package ru.belovia.masklib;

import java.util.List;
import java.util.Map;

/**
 * A utility class for masking sensitive data in strings.
 *
 * <p>This class provides methods for both full and partial masking of strings.
 * Developers can specify a masking character or use the default '*' character.
 * </p>
 * <h3>Features:</h3>
 * <ul>
 *     <li>Full masking of a string with a specified or default character</li>
 *     <li>Partial masking of a string within a given {@code Range}</li>
 *     <li>Flexible API to support different masking rules</li>
 * </ul>
 *
 * <h3>Example Usage:</h3>
 * <pre>
 *     FieldMasker masker = new FieldMasker();
 *
 *     String maskedFull = masker.maskFull("password");
 *     Output: ********
 *
 *     String maskedPartial = masker.maskPartial("secret123", new Range(2, 6));
 *     Output: se****123
 * </pre>
 *
 * <h3>Extensibility:</h3>
 * <p>The class is designed to be extended if additional masking strategies are required.
 * For example, a subclass could define custom rules for specific data types like emails or phone numbers.</p>
 *
 * @see Range
 */
public class FieldMasker {
    private static final char DEFAULT_MASK_CHAR = '*';

    public static String maskJson(String json, Map<String, MaskType> fieldNames, Range range) {
        if (json == null || json.isBlank()) return json;

        Map<String, Object> fields = JsonParser.parseJson(json);
        maskFields(fields, fieldNames, range);
        return JsonParser.toJsonString(fields);
    }

    private static void maskFields(Map<String, Object> fields, Map<String, MaskType> fieldNames, Range range) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            MaskType maskType = fieldNames.get(key);

            if (maskType != null && value instanceof String strValue) {
                fields.put(key, applyMask(strValue, maskType, range));
            } else if (value instanceof Map<?, ?> mapValue) {
                if (mapValue.keySet().stream().allMatch(k -> k instanceof String)) {
                    maskFields((Map<String, Object>) mapValue, fieldNames, range);
                }
            } else if (value instanceof List<?> listValue) {
                maskList((List<Object>) listValue, fieldNames, range);
            }
        }
    }

    private static void maskList(List<Object> list, Map<String, MaskType> fieldNames, Range range) {
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            if (value instanceof String strValue) {
                list.set(i, maskPartial(strValue, range, DEFAULT_MASK_CHAR));
            } else if (value instanceof Map<?, ?> mapValue) {
                maskFields((Map<String, Object>) mapValue, fieldNames, range);
            }
        }
    }


    private static String applyMask(String value, MaskType maskType, Range range) {
        return switch (maskType) {
            case FULL -> maskFull(value);
            case PARTIALLY -> maskPartial(value, range);
        };
    }

    public static String maskFull(String input) {
        return maskFull(input, DEFAULT_MASK_CHAR);
    }

    public static String maskFull(String input, char maskingChar) {
        if (emptyInput(input)) return input;
        return String.valueOf(maskingChar).repeat(input.length());
    }

    public static String maskPartial(String input, Range range) {
        return maskPartial(input, range, DEFAULT_MASK_CHAR);
    }

    public static String maskPartial(String input, Range range, char maskingChar) {
        if (emptyInput(input) || range == null) return input;

        int length = input.length();
        int start = Math.max(0, range.getFrom());
        int end = Math.min(length, range.getTo());

        StringBuilder masked = new StringBuilder(input);
        for (int i = start; i < end; i++) {
            masked.setCharAt(i, maskingChar);
        }
        return masked.toString();
    }

    private static boolean emptyInput(String input) {
        return input == null || input.isBlank();
    }
}
