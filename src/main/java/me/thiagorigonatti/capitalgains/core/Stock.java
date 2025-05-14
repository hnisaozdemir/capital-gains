package me.thiagorigonatti.capitalgains.core;

import me.thiagorigonatti.capitalgains.exception.InsufficientSharesException;
import me.thiagorigonatti.capitalgains.exception.ZeroOrNegativeQuantityException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a stock position and manages buy and sell operations while tracking cost, shares, and tax calculations.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Maintaining the total number of shares owned</li>
 *     <li>Calculating the average cost per share after each buy</li>
 *     <li>Calculating capital gains taxes on sales</li>
 *     <li>Tracking accumulated losses to offset future gains</li>
 * </ul>
 *
 * <p>Exceptions are thrown if the quantity is zero or negative, or if trying to sell more shares than are owned.</p>
 *
 * <p>This class is not thread-safe.</p>
 *
 * @author <a href="https://github.com/thiagorigonatti">Thiago Rigonatti</a>
 * @version 1.0
 * @since 1.0
 */
public class Stock {

    /**
     * Default constructor for the {@code Stock} class.
     * <p>
     * Initializes a new stock instance with zero shares, zero total cost,
     * and zero accumulated losses. Intended for use when the initial
     * state will be populated through operations (e.g., buy/sell).
     */
    public Stock() {
    }

    private BigDecimal totalCost = BigDecimal.ZERO;
    private long totalShares;
    private BigDecimal averageCost = BigDecimal.ZERO;
    private BigDecimal accumulatedLoss = BigDecimal.ZERO;

    /**
     * Returns the applicable tax rate for capital gains.
     * <p>
     * The current rate is fixed at 20% (0.20).
     *
     * @return the capital gains tax rate as a {@link BigDecimal}
     */
    protected BigDecimal taxRate() {
        return BigDecimal.valueOf(.20);
    }

    /**
     * Returns the sales threshold under which capital gains are exempt from taxation.
     * <p>
     * Currently, operations with total sales less than or equal to R$20,000.00 are exempt.
     *
     * @return the tax exemption threshold as a {@link BigDecimal}
     */
    protected BigDecimal threshold() {
        return BigDecimal.valueOf(20_000);
    }

    /**
     * Processes a buy operation, increasing the number of shares and updating the average cost.
     *
     * @param quantity the number of shares to buy; must be greater than zero
     * @param unitCost the cost per share
     * @throws ZeroOrNegativeQuantityException if the quantity is less than or equal to zero
     */
    protected void buy(final long quantity, final BigDecimal unitCost) {
        if (quantity <= 0) throw new ZeroOrNegativeQuantityException(quantity);

        final BigDecimal valorPago = unitCost.multiply(BigDecimal.valueOf(quantity));

        totalCost = totalCost.add(valorPago);
        totalShares += quantity;
        averageCost = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_EVEN);
    }

    /**
     * Processes a sell operation, updating shares and calculating the capital gains tax based on profit and thresholds.
     * <p>
     * If the sale results in a loss, the value is added to {@code accumulatedLoss}. If there's a gain and the total
     * value of the sale exceeds R$20,000, tax is calculated at 20% on the net profit after subtracting accumulated losses.
     *
     * @param quantity the number of shares to sell; must be greater than zero and not exceed current holdings
     * @param unitCost the sale price per share
     * @return the amount of tax due from the operation, rounded to two decimal places
     * @throws ZeroOrNegativeQuantityException if the quantity is less than or equal to zero
     * @throws InsufficientSharesException     if attempting to sell more shares than currently owned
     */
    protected BigDecimal sell(final long quantity, final BigDecimal unitCost) {
        if (quantity <= 0) throw new ZeroOrNegativeQuantityException(quantity);
        if (quantity > totalShares) throw new InsufficientSharesException(quantity, totalShares);

        final BigDecimal saleTotal = unitCost.multiply(BigDecimal.valueOf(quantity));
        final BigDecimal cost = averageCost.multiply(BigDecimal.valueOf(quantity));
        final BigDecimal profit = saleTotal.subtract(cost);

        BigDecimal tax = BigDecimal.ZERO;

        if (profit.compareTo(BigDecimal.ZERO) < 0) {
            accumulatedLoss = accumulatedLoss.add(profit.abs());
        } else if (saleTotal.compareTo(threshold()) > 0) {
            if (accumulatedLoss.compareTo(profit) >= 0) {
                accumulatedLoss = accumulatedLoss.subtract(profit);
            } else {
                final BigDecimal taxable = profit.subtract(accumulatedLoss);
                accumulatedLoss = BigDecimal.ZERO;
                tax = taxable.multiply(taxRate()).setScale(2, RoundingMode.HALF_EVEN);
            }
        }

        totalShares -= quantity;
        totalCost = averageCost.multiply(BigDecimal.valueOf(totalShares));

        return tax;
    }
}
