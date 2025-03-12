package ru.belovia.masklib;

import java.util.*;

public class JsonParser {
    public static Map<String, Object> parseJson(String json) {
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        parseObject(json.substring(1, json.length() - 1).trim(), result);
        return result;
    }

    private static void parseObject(String json, Map<String, Object> result) {
        int i = 0, length = json.length();
        while (i < length) {
            i = skipWhitespace(json, i);
            if (i >= length) break;

            int keyStart = json.indexOf('"', i);
            int keyEnd = json.indexOf('"', keyStart + 1);
            String key = json.substring(keyStart + 1, keyEnd);

            i = keyEnd + 1;
            i = skipWhitespace(json, i);
            if (json.charAt(i) != ':') throw new IllegalArgumentException("Expected ':' after key");
            i++;

            i = skipWhitespace(json, i);
            Object value;
            if (json.charAt(i) == '"') {
                int valueStart = i + 1;
                int valueEnd = json.indexOf('"', valueStart);
                value = json.substring(valueStart, valueEnd);
                i = valueEnd + 1;
            } else if (json.charAt(i) == '{') {
                int closingIndex = findClosingBracket(json, i, '{', '}');
                Map<String, Object> nestedObject = new LinkedHashMap<>();
                parseObject(json.substring(i + 1, closingIndex).trim(), nestedObject);
                value = nestedObject;
                i = closingIndex + 1;
            } else if (json.charAt(i) == '[') {
                int closingIndex = findClosingBracket(json, i, '[', ']');
                value = parseArray(json.substring(i + 1, closingIndex).trim());
                i = closingIndex + 1;
            } else {
                int valueEnd = findValueEnd(json, i);
                value = json.substring(i, valueEnd).trim();
                i = valueEnd;
            }

            result.put(key, value);
            i = skipWhitespace(json, i);
            if (i < length && json.charAt(i) == ',') i++;
        }
    }

    private static List<Object> parseArray(String json) {
        List<Object> result = new ArrayList<>();
        int i = 0, length = json.length();
        while (i < length) {
            i = skipWhitespace(json, i);
            if (i >= length) break;

            Object value;
            if (json.charAt(i) == '"') {
                int valueStart = i + 1;
                int valueEnd = json.indexOf('"', valueStart);
                value = json.substring(valueStart, valueEnd);
                i = valueEnd + 1;
            } else if (json.charAt(i) == '{') {
                int closingIndex = findClosingBracket(json, i, '{', '}');
                Map<String, Object> nestedObject = new LinkedHashMap<>();
                parseObject(json.substring(i + 1, closingIndex).trim(), nestedObject);
                value = nestedObject;
                i = closingIndex + 1;
            } else if (json.charAt(i) == '[') {
                int closingIndex = findClosingBracket(json, i, '[', ']');
                value = parseArray(json.substring(i + 1, closingIndex).trim());
                i = closingIndex + 1;
            } else {
                int valueEnd = findValueEnd(json, i);
                value = json.substring(i, valueEnd).trim();
                i = valueEnd;
            }

            result.add(value);
            i = skipWhitespace(json, i);
            if (i < length && json.charAt(i) == ',') i++;
        }
        return result;
    }

    private static int findClosingBracket(String json, int start, char open, char close) {
        int count = 1;
        for (int i = start + 1; i < json.length(); i++) {
            if (json.charAt(i) == open) count++;
            if (json.charAt(i) == close) count--;
            if (count == 0) return i;
        }
        throw new IllegalArgumentException("Unmatched brackets in JSON");
    }

    private static int findValueEnd(String json, int start) {
        int i = start;
        while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}' && json.charAt(i) != ']') {
            i++;
        }
        return i;
    }

    private static int skipWhitespace(String json, int index) {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        return index;
    }

    public static String toJsonString(Map<String, Object> map) {
        return map.toString();
    }
}