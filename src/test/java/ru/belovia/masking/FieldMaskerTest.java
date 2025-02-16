package ru.belovia.masking;

import org.junit.jupiter.api.Test;
import ru.belovia.masklib.FieldMasker;
import ru.belovia.masklib.Range;

import static org.junit.jupiter.api.Assertions.*;

class FieldMaskerTest {

    private final FieldMasker fieldMasker = new FieldMasker();

    @Test
    void IF_maskFullWithNotEmptyString_THEN_returnFullMaskingString() {
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
    void IF_maskFullWithNotEmptyStringAndSomeChar_THEN_returnFullMaskingByInputChar() {
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
    void IF_maskPartialWithValidData_THEN_returnPartiallyMaskingField() {
        // Arrange
        String input = "+79999999999";
        String expected = "+7999*****99";

        // Acton
        String result = fieldMasker.maskPartial(input, new Range(5, 10));

        // Asserts
        assertNotNull(result);
        assertEquals(expected, result);
    }
}