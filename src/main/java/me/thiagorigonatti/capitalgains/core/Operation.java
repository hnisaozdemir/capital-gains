package me.thiagorigonatti.capitalgains.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents a single financial operation involving buying or selling assets.
 * <p>
 * This record is used to model input data for capital gains calculation. Each operation includes:
 * <ul>
 *     <li>The type of operation (e.g., "buy" or "sell")</li>
 *     <li>The unit cost of the asset</li>
 *     <li>The quantity involved in the operation</li>
 *     <li>The ticker symbol of the asset involved in the operation</li>
 * </ul>
 * <p>
 * The {@code ticker} field is now considered in the tax calculation logic to ensure operations are grouped and
 * processed separately by their respective tickers, allowing for accurate tax calculations per asset.
 * </p>
 * <p>
 * This class is intended to be deserialized from JSON input using Jackson.
 * </p>
 *
 * @param operation the type of operation, such as "buy" or "sell"
 * @param unitCost  the cost per unit of the asset
 * @param quantity  the number of units involved in the operation
 * @param ticker    the ticker symbol of the asset involved in the operation
 * @author <a href="https://github.com/thiagorigonatti">Thiago Rigonatti</a>
 * @version 1.1
 * @since 1.0
 */
public record Operation(
        @JsonProperty("operation") String operation,
        @JsonProperty("unit-cost") BigDecimal unitCost,
        @JsonProperty("quantity") long quantity,
        @JsonProperty("ticker") String ticker
) {
}
