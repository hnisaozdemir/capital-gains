package me.thiagorigonatti.capitalgains.exception;

import java.text.MessageFormat;

/**
 * Exception thrown when an operation attempts to process a quantity that is zero or negative.
 * <p>
 * This {@link RuntimeException} is used to enforce that all buy or sell operations involve
 * a positive quantity of shares.
 * </p>
 *
 */
public class ZeroOrNegativeQuantityException extends RuntimeException {

    /**
     * Constructs a {@code ZeroOrNegativeQuantityException} with a detailed error message.
     *
     * @param quantity the invalid quantity that was provided
     */
    public ZeroOrNegativeQuantityException(long quantity) {
        super(MessageFormat.format("Invalid quantity: `{0}`. The quantity must be greater than 0.", quantity));
    }
}
