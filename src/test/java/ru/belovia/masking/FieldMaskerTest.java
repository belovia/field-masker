package ru.belovia.masking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.belovia.masklib.FieldMasker;
import ru.belovia.masklib.Range;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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

        Set<String> fieldNames = new HashSet<>();
        fieldNames.add("testName");
        fieldNames.add("testEmail");
        fieldNames.add("testPhone");
        fieldNames.add("testPassportId");
        String json = new String(Files.readAllBytes(Path.of("src/test/java/ru/belovia/masking/testJson.json")));

        String s = fieldMasker.maskJson(json, true, fieldNames, null);

        assertNotNull(s);
        assertTrue(s.contains("testName"));
        assertTrue(s.contains("********"));
        assertTrue(s.contains("********************"));
        assertTrue(s.contains("testPhone"));
        assertTrue(s.contains("***********"));
    }
}