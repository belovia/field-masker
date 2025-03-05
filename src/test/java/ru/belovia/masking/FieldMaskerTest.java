package ru.belovia.masking;

import lombok.Builder;
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

    private final FieldMasker fieldMasker = new FieldMasker();

    @Test
    void IF_maskFullWithNotEmptyString_THEN_returnFullMaskedString() {
        // Arrange
        String input = "inputValue";
        String expected = "**********";

        // Action
        String actual = fieldMasker.maskFull(input);

        // Asserts
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void IF_maskFullWithEmptyString_THEN_returnInput() {
        // Action
        String result = fieldMasker.maskFull(null);

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
        String result = fieldMasker.maskFull(input, inputChar);

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
        String result = fieldMasker.maskPartial(input, new Range(5, 10));

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

        String result = fieldMasker.maskJson(json, fieldNames, new Range(2,6));

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


    @Builder
    public static class TestClass {
        private String testStringField;
        private Integer testIntegerField;

        public String toString() {
            return "TestClass{" +
                    " 'testStringField' : " + testStringField +
                    ", 'testIntegerField' : " + testIntegerField +
                    "}";
        }
    }

    @Test
    void IF_maskObjectWithFullMaskType_THEN_returnOK() {
        // Arrange
        TestClass testClass = TestClass.builder()
                .testStringField("testStringField")
                .testIntegerField(12345)
                .build();

        Map<String, MaskType> pattern = new HashMap<>();
        pattern.put("testStringField", MaskType.FULL);
        pattern.put("testIntegerField", MaskType.FULL);

        // Action
        String result = fieldMasker.maskObject(testClass, pattern, null);

        // Asserts
        assertNotNull(result);
        assertTrue(result.contains("**********"));
        assertTrue(result.contains("*****"));
    }

    @Test
    void IF_maskObjectWithPartialMaskType_THEN_returnOK() {
        // Arrange
        TestClass testClass = TestClass.builder()
                .testStringField("testStringField")
                .testIntegerField(12345)
                .build();

        Map<String, MaskType> pattern = new HashMap<>();
        pattern.put("testStringField", MaskType.PARTIALLY);
        pattern.put("testIntegerField", MaskType.PARTIALLY);

        // Action
        String result = fieldMasker.maskObject(testClass, pattern, new Range(2, 10));

        // Asserts
        assertNotNull(result);
        assertTrue(result.contains("te********Field"));
        assertTrue(result.contains("12***"));
    }
}