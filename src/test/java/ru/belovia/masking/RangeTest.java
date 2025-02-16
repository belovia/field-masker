package ru.belovia.masking;

import org.junit.jupiter.api.Test;
import ru.belovia.masklib.Range;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {

    @Test
    void getFrom_OK() {
        // Arrange
        Range range = new Range(2, 7);

        // Assert
        assertNotNull(range);
        assertEquals(2, range.getFrom());
    }

    @Test
    void getTo_OK() {
        // Arrange
        Range range = new Range(2, 7);

        // Assert
        assertNotNull(range);
        assertEquals(7, range.getTo());
    }

    @Test
    void IF_fromBiggerThanTo_THEN_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Range(7, 2));
    }

}