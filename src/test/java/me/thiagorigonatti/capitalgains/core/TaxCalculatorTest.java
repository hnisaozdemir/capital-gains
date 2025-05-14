package me.thiagorigonatti.capitalgains.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import me.thiagorigonatti.capitalgains.exception.InsufficientSharesException;
import me.thiagorigonatti.capitalgains.exception.InvalidOperationException;
import me.thiagorigonatti.capitalgains.exception.ZeroOrNegativeQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link TaxCalculator} class, ensuring correct tax calculations
 * for various scenarios including sales with profit, losses, and different cost methods.
 *
 * @author <a href="https://github.com/thiagorigonatti">Thiago Rigonatti</a>
 * @version 1.0
 * @since 1.0
 */
public class TaxCalculatorTest {

    /**
     * Default constructor for TaxCalculatorTest.
     * This constructor can be used to initialize any resources or dependencies needed by the test class.
     */
    public TaxCalculatorTest() {
    }

    private TaxCalculator taxCalculator;

    /**
     * Sets up the {@link TaxCalculator} instance before each test.
     */
    @BeforeEach
    public void setUp() {
        taxCalculator = new TaxCalculator.Builder().build();
    }

    /**
     * Helper method to calculate tax based on the provided input string and index.
     *
     * @param line  The input JSON string representing the operations (buy/sell).
     * @param index The index of the operation to retrieve tax for.
     * @return The calculated tax as a {@link BigDecimal}.
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    private BigDecimal getTax(final String line, final int index) throws JsonProcessingException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(line.getBytes());
        taxCalculator.setInputStream(byteArrayInputStream);
        taxCalculator.setOutputStream(new ByteArrayOutputStream());
        taxCalculator.run();

        final String result = taxCalculator.getOutputStream().toString();
        final JsonNode jsonNode = taxCalculator.getObjectMapper().readTree(result);

        return BigDecimal.valueOf(jsonNode.get(index).get("tax").asDouble());
    }

    /**
     * Test case for calculating tax when the sale amount is under 20k with profit.
     * Verifies that no tax is due.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenSaleUnder20kWithProfit_WhenCalculatingTax_thenNoTaxIsDue() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 100},{"operation":"sell", "unit-cost":15.00, "quantity": 50},{"operation":"sell", "unit-cost":15.00, "quantity": 50}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
    }

    /**
     * Test case for calculating tax when the sale amount is over 20k with profit.
     * Verifies that tax is applied.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenSaleOver20kWithProfit_WhenCalculatingTax_thenTaxIsApplied() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":20.00, "quantity": 5000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.valueOf(10_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
    }

    /**
     * Test case for calculating tax with two independent simulations. Verifies that
     * tax is calculated correctly for each operation separately.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenTwoIndependentSimulation_whenCalculateTax_thenReturnCorrectTaxPerOperation() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 100},{"operation":"sell", "unit-cost":15.00, "quantity": 50},{"operation":"sell", "unit-cost":15.00, "quantity": 50}]
                """;

        final String input2 = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":20.00, "quantity": 5000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input2, 0));
        assertEquals(BigDecimal.valueOf(10_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input2, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input2, 2));
    }

    /**
     * Test case for calculating tax when there is a loss from previous sales. Verifies that
     * tax is applied only on the net profit from subsequent sales.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenLossFromPreviousSale_whenSellingWithProfit_thenTaxIsAppliedOnNetProfit() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 3000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.valueOf(1_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
    }

    /**
     * Test case for calculating tax when selling at the average cost. Verifies that no tax is due
     * when selling at the average price.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenAverageCost_whenSellingAtAveragePrice_thenNoTaxIsDue() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"buy", "unit-cost":25.00, "quantity": 5000},{"operation":"sell", "unit-cost":15.00, "quantity": 10000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
    }

    /**
     * Test case for calculating tax when selling at weighted average cost. Verifies that tax is
     * applied only to the profit portion.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenWeightedAverageCost_whenSellingAtBreakEvenAndThenAtProfit_thenOnlyProfitIsTaxed() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"buy", "unit-cost":25.00, "quantity": 5000},{"operation":"sell", "unit-cost":15.00, "quantity": 10000},{"operation":"sell", "unit-cost":25.00, "quantity": 5000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
        assertEquals(BigDecimal.valueOf(10_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 3));
    }

    /**
     * Test case for calculating tax with accumulated losses. Verifies that losses are properly deducted
     * when calculating tax on subsequent sales.
     *
     * @throws JsonProcessingException If there is an error processing the JSON input.
     */
    @Test
    public void givenSalesWithAccumulatedLosses_whenCalculatingTax_thenLossesAreProperlyDeducted() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":2.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 2000},{"operation":"sell", "unit-cost":20.00, "quantity": 2000},{"operation":"sell", "unit-cost":25.00, "quantity": 1000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 3));
        assertEquals(BigDecimal.valueOf(3_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 4));
    }

    /**
     * Test case for calculating tax when there are multiple sales with accumulated losses and gains.
     * It verifies that losses are properly deducted and tax is applied correctly based on the net profit.
     *
     * @throws JsonProcessingException if there is an error processing the JSON input
     */
    @Test
    public void givenMultipleSalesWithAccumulatedLossesAndGains_whenCalculatingTax_thenLossesAreDeductedAndTaxApplied() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":2.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 2000},{"operation":"sell", "unit-cost":20.00, "quantity": 2000},{"operation":"sell", "unit-cost":25.00, "quantity": 1000},{"operation":"buy", "unit-cost":20.00, "quantity": 10000},{"operation":"sell", "unit-cost":15.00, "quantity": 5000},{"operation":"sell", "unit-cost":30.00, "quantity": 4350},{"operation":"sell", "unit-cost":30.00, "quantity": 650}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 3));
        assertEquals(BigDecimal.valueOf(3_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 4));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 5));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 6));
        assertEquals(BigDecimal.valueOf(3_700).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 7));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 8));
    }

    /**
     * Test case for calculating tax on sales with high profits.
     * It ensures that tax is applied correctly on profits above the threshold.
     *
     * @throws JsonProcessingException if there is an error processing the JSON input
     */
    @Test
    public void givenSalesWithHighProfit_whenCalculatingTax_thenTaxIsApplied() throws JsonProcessingException {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":50.00, "quantity": 10000},{"operation":"buy", "unit-cost":20.00, "quantity": 10000},{"operation":"sell", "unit-cost":50.00, "quantity": 10000}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.valueOf(80_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
        assertEquals(BigDecimal.valueOf(60_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 3));
    }

    /**
     * Test case for mixed operations with both losses and profits.
     * It verifies that losses are properly offset and tax is applied correctly on the net profit.
     *
     * @throws JsonProcessingException if there is an error processing the JSON input
     */
    @Test
    public void givenMixedOperationsWithLossAndProfit_whenCalculatingTax_thenLossesAreOffsetAndTaxIsCorrectlyApplied() throws JsonProcessingException {

        final String input = """
                [{"operation": "buy", "unit-cost": 5000.00, "quantity": 10},{"operation": "sell", "unit-cost": 4000.00, "quantity": 5},{"operation": "buy", "unit-cost": 15000.00, "quantity": 5},{"operation": "buy", "unit-cost": 4000.00, "quantity": 2},{"operation": "buy", "unit-cost": 23000.00, "quantity": 2},{"operation": "sell", "unit-cost": 20000.00, "quantity": 1},{"operation": "sell", "unit-cost": 12000.00, "quantity": 10},{"operation": "sell", "unit-cost": 15000.00, "quantity": 3}]
                """;

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 0));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 1));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 2));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 3));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 4));
        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_EVEN), getTax(input, 5));
        assertEquals(BigDecimal.valueOf(1_000).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 6));
        assertEquals(BigDecimal.valueOf(2_400).setScale(1, RoundingMode.HALF_EVEN), getTax(input, 7));
    }

    /**
     * Test case for formatting a decimal number using a specific decimal format.
     * It checks that the formatted number matches the expected pattern.
     */
    @Test
    public void givenDecimalNumber_whenFormattedWithDecimalFormat_thenMatchExpectedPattern() {

        final double number = 1_234_56.789;

        final String regex = "^\\d{6}\\.\\d$";

        final String format = taxCalculator.getDecimalFormat().format(number);

        assertTrue(format.matches(regex), "format does not match");
    }

    /**
     * Test case to verify that an exception is thrown when a sale exceeds the available quantity of shares.
     * It ensures that the {@link InsufficientSharesException} is thrown when the quantity exceeds the available shares.
     */
    @Test
    public void givenSaleExceedsAvailableQuantity_whenCalculatingTax_thenThrowInsufficientSharesException() {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 100},{"operation":"sell", "unit-cost":15.00, "quantity": 50},{"operation":"sell", "unit-cost":15.00, "quantity": 60}]
                """;

        assertThrows(InsufficientSharesException.class, () -> getTax(input, 0));
    }

    /**
     * Test case to verify that an exception is thrown when an invalid operation is encountered.
     * It checks that the {@link InvalidOperationException} is thrown for unsupported operations.
     */
    @Test
    public void givenInvalidOperation_whenCalculatingTax_thenThrowInvalidOperationException() {

        final String input = """
                [{"operation":"buy", "unit-cost":10.00, "quantity": 100},{"operation":"short_sell", "unit-cost":15.00, "quantity": 50},{"operation":"sell", "unit-cost":15.00, "quantity": 50}]
                """;

        assertThrows(InvalidOperationException.class, () -> getTax(input, 0));
    }


    /**
     * Test case to verify that an exception is thrown when an operation is performed with a zero or negative quantity.
     * It ensures that the {@link ZeroOrNegativeQuantityException} is thrown for such cases.
     */
    @Test
    public void givenOperationWithZeroOrNegativeQuantity_whenCalculateTax_thenThrowZeroOrNegativeQuantityException() {

        final String input = """
                [{"operation":"buy", "unit-cost":20.00, "quantity": 0}]
                """;

        final String input2 = """
                [{"operation":"buy", "unit-cost":20.00, "quantity": -1}]
                """;

        assertThrows(ZeroOrNegativeQuantityException.class, () -> getTax(input, 0));
        assertThrows(ZeroOrNegativeQuantityException.class, () -> getTax(input2, 0));
    }
}
