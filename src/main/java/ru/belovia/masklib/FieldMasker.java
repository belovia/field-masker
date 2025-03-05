package ru.belovia.masklib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String maskFull(String input) {
        return maskFull(input, DEFAULT_MASK_CHAR);
    }


    public String maskPartial(String input, Range range) {
        return maskPartial(input, range, DEFAULT_MASK_CHAR);
    }


    public String maskJson(String json, Map<String, MaskType> fieldNames, Range range) {
        if (json == null || json.isBlank()) return json;

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            if (jsonNode.isObject()) {
                maskFields((ObjectNode) jsonNode, fieldNames, range, '*');
            }
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return json;
        }
    }

    private void maskFields(ObjectNode jsonNode, Map<String, MaskType> fieldNames, Range range, char maskChar) {
        jsonNode.fieldNames().forEachRemaining(currentFieldName -> {
            JsonNode field = jsonNode.get(currentFieldName);
            MaskType maskType = fieldNames.get(currentFieldName);

            if (maskType != null) {
                if (field.isTextual() || field.isNumber()) {
                    String fieldValue = field.asText();
                    if (maskType == MaskType.FULL) {
                        jsonNode.put(currentFieldName, maskFull(fieldValue));
                    } else if (maskType == MaskType.PARTIALLY) {
                        jsonNode.put(currentFieldName, maskPartial(fieldValue, range));
                    }
                } else if (field.isArray()) {
                    maskArray((ArrayNode) field, currentFieldName, fieldNames, range, maskChar);
                } else if (field.isObject()) {
                    maskFields((ObjectNode) field, fieldNames, range, maskChar);
                }
            }
        });
    }

    private void maskArray(ArrayNode arrayNode, String fieldName, Map<String, MaskType> fieldNames, Range range, char maskChar) {
        MaskType maskType = fieldNames.get(fieldName);

        if (maskType != null) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode arrayElement = arrayNode.get(i);

                if (arrayElement.isTextual() || arrayElement.isNumber()) {
                    String elementValue = arrayElement.asText();
                    if (maskType == MaskType.FULL) {
                        arrayNode.set(i, maskFull(elementValue));
                    } else if (maskType == MaskType.PARTIALLY) {
                        arrayNode.set(i, maskPartial(elementValue, range));
                    }
                } else if (arrayElement.isObject()) {
                    maskFields((ObjectNode) arrayElement, fieldNames, range, maskChar);
                } else if (arrayElement.isArray()) {
                    maskArray((ArrayNode) arrayElement, fieldName, fieldNames, range, maskChar);
                }
            }
        }
    }

    public String maskObject(Object obj, Map<String, MaskType> fieldNames, Range range) {
        if (obj == null) return "null";

        ObjectNode jsonNode = objectMapper.createObjectNode();

        Field[] fields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .toArray(Field[]::new);

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            MaskType maskType = fieldNames.get(fieldName);

            try {
                Object fieldValue = field.get(obj);
                if (fieldValue != null) {
                    String maskedValue = (maskType != null)
                            ? applyMask(fieldValue.toString(), maskType, range)
                            : fieldValue.toString();

                    jsonNode.put(fieldName, maskedValue);
                }
            } catch (Exception e) {
                jsonNode.put(fieldName, "[MASKED]");
            }
        }

        return jsonNode.toString();
    }

    private String applyMask(String value, MaskType maskType, Range range) {
        return switch (maskType) {
            case FULL -> maskFull(value);
            case PARTIALLY -> maskPartial(value, range);
        };
    }

    public String maskFull(String input, char maskingChar) {
        if (emptyInput(input)) return input;
        return String.valueOf(maskingChar).repeat(input.length());
    }

    public String maskPartial(String input, Range range, char maskingChar) {
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

    private boolean emptyInput(String input) {
        return input == null || input.isBlank();
    }
}
