package ru.belovia.masking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.belovia.masklib.FieldMasker;
import ru.belovia.masklib.MaskType;
import ru.belovia.masklib.Range;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldMaskerTest {

    @Test
    void IF_maskFullWithNotEmptyString_THEN_returnFullMaskedString() {
        // Arrange
        String input = "inputValue";
        String expected = "**********";

        // Action
        String actual = FieldMasker.maskFull(input);

        // Asserts
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void IF_maskFullWithEmptyString_THEN_returnInput() {
        // Action
        String result = FieldMasker.maskFull(null);

        // Asserts
        assertNull(result);
    }

    @Test
    void IF_maskFullWithNotEmptyStringAndSomeChar_THEN_returnFullMaskedByInputChar() {
        // Arrange
        char inputChar = '$';
        String input = "inputValue";
        String expected = "$$$$$$$$$$";

        // Action
        String result = FieldMasker.maskFull(input, inputChar);

        // Asserts
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void IF_maskPartialWithValidData_THEN_returnPartiallyMaskedField() {
        // Arrange
        String input = "+79999999999";
        String expected = "+7999*****99";

        // Acton
        String result = FieldMasker.maskPartial(input, new Range(5, 10));

        // Asserts
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @SneakyThrows
    void IF_maskJsonWithValidData_THEN_returnMaskedFields() {

        Map<String, MaskType> fieldNames = new HashMap<>();
        fieldNames.put("testName", MaskType.FULL);
        fieldNames.put("testEmail", MaskType.FULL);
        fieldNames.put("testPhone", MaskType.FULL);
        fieldNames.put("testPassportId", MaskType.PARTIALLY);
        fieldNames.put("testArray", MaskType.PARTIALLY);
        String json = new String(Files.readAllBytes(Path.of("src/test/java/ru/belovia/masking/testdata/testJson.json")));

        String result = FieldMasker.maskJson(json, fieldNames, new Range(2, 6));

        assertNotNull(result);
        assertTrue(result.contains("testName"));
        assertTrue(result.contains("********"));
        assertTrue(result.contains("********************"));
        assertTrue(result.contains("testPhone"));
        assertTrue(result.contains("***********"));
        assertTrue(result.contains("42****8689"));
        assertTrue(result.contains("12****8"));
        assertTrue(result.contains("12****7890"));
    }


    @Test
    void IF_maskJsonWithObject_THEN_returnOK() {

        String json = """
                    {
                       "address": {
                         "street": "123 Main St",
                         "city": "Springfield",
                         "zipCode": 123456
                       }
                     }
            """;
        Map<String, MaskType> maskConfig = new HashMap<>();
        maskConfig.put("street", MaskType.FULL);
        maskConfig.put("zipCode", MaskType.PARTIALLY);

        String result = FieldMasker.maskJson(json, maskConfig, new Range(1, 5));

        assertNotNull(result);
        assertTrue(result.contains("1****6"));
        assertTrue(result.contains("***********"));
    }
}