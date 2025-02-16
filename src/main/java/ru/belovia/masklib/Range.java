package ru.belovia.masklib;

import lombok.Getter;
/**
 * Represents a range of integer values used for partial masking of a string.
 *
 * <p>This class defines the boundaries of a masked section within a string.
 * The range is defined by the starting index {@code from} and the ending index {@code to}.
 * </p>
 *
 * <p>It can be extended to provide custom masking logic for specific data types.
 * For example, a subclass can define dynamic masking rules for a specific cases.</p>
 *
 * <p>Example of subclass usage:</p>
 * <pre>
 *     Range phoneRange = new PhoneMaskRange("+1234567890");
 *     Output: "+1234****90"
 * </pre>
 */
@Getter
public class Range {
    private final int from;
    private final int to;

    public Range(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("Start index cannot be greater than end index.");
        }
        this.from = from;
        this.to = to;
    }
}
