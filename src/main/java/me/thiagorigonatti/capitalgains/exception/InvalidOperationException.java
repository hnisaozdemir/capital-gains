package me.thiagorigonatti.capitalgains.exception;

import java.text.MessageFormat;

/**
 * Exception thrown when an unrecognized operation type is encountered during processing.
 * <p>
 * This is a domain-specific {@link RuntimeException} used to enforce valid values
 * for the {@code operation} field in stock market data, typically limited to {@code \"buy\"} and {@code \"sell\"}.
 * </p>
 *
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Constructs an {@code InvalidOperationException} with a detailed error message.
     *
     * @param operation the invalid operation name encountered
     */
    public InvalidOperationException(String operation) {
        super(MessageFormat.format("Invalid operation type: `{0}`. Expected values: `buy`, `sell`.", operation));
    }
}
