package me.thiagorigonatti.capitalgains.exception;

import java.text.MessageFormat;

/**
 * Exception thrown when a sell operation attempts to sell more shares than are currently held.
 * <p>
 * This is a domain-specific {@link RuntimeException} used to indicate a logical inconsistency
 * in stock operations, where the requested quantity exceeds the available shares.
 * </p>
 *
 */
public class InsufficientSharesException extends RuntimeException {

    /**
     * Constructs an {@code InsufficientSharesException} with details about the invalid request.
     *
     * @param requested the number of shares requested to be sold
     * @param available the number of shares actually available in the portfolio
     */
    public InsufficientSharesException(long requested, long available) {
        super(MessageFormat.format("Insufficient shares available for sale. Requested: `{0}`, Available: `{1}`", requested, available));
    }
}
