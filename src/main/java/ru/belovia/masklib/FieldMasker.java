package ru.belovia.masklib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;

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


    public String maskJson(String json, boolean maskFully, Set<String> fieldNames, Range range) {
        if (json == null || json.isBlank()) return json;

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            if (jsonNode.isObject()) {
                maskFields((ObjectNode) jsonNode, maskFully, fieldNames, range);
            }
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return json;
        }
    }

    private void maskFields(ObjectNode jsonNode, boolean maskFully, Set<String> fieldNames, Range range) {
        jsonNode.fieldNames().forEachRemaining(fieldName -> {
            JsonNode field = jsonNode.get(fieldName);

            if (fieldNames.contains(fieldName)) {
                    String fieldValue = field.asText();
                    if (maskFully) {
                        jsonNode.put(fieldName, maskFull(fieldValue));
                    } else {
                        jsonNode.put(fieldName, maskPartial(fieldValue, range));
                    }
            }
        });
    }

    public String maskFull(String input, char maskingChar) {
        if (emptyInput(input)) return input;
        return String.valueOf(maskingChar).repeat(input.length());
    }


    public String maskPartial(String input, Range range, char maskingChar) {
        if (emptyInput(input)) return input;

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
