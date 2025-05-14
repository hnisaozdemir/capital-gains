package me.thiagorigonatti.capitalgains.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.thiagorigonatti.capitalgains.exception.InvalidOperationException;
import me.thiagorigonatti.capitalgains.util.ArgParser;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>Compute taxes based on a sequence of stock trading operations such as "buy" and "sell". It supports configurable
 * input and output streams, buffer sizes, JSON serialization, and optional filtering of operations.</p>
 *
 * <p>This class is designed to be flexible and extensible, with a builder-based configuration system that allows
 * customization of behavior such as decimal formatting, predicate-based operation filtering, real-time output
 * flushing, and execution time tracking.</p>
 *
 * @author <a href="https://github.com/thiagorigonatti">Thiago Rigonatti</a>
 * @version 1.0
 * @since 1.0
 */

public class TaxCalculator {

    private String[] args;
    private ObjectMapper objectMapper;
    private DecimalFormat decimalFormat;
    private InputStream inputStream;
    private int bufferSizeIn;
    private OutputStream outputStream;
    private int bufferSizeOut;
    private boolean printEveryLine;
    private boolean timings;
    private Predicate<Operation> operationPredicate;
    private Supplier<? extends Stock> stockSupplier;

    /**
     * Returns the command-line arguments.
     *
     * @return the arguments array
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Sets the command-line arguments.
     *
     * @param args the arguments array to set
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Returns the ObjectMapper instance.
     *
     * @return the object mapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Sets the ObjectMapper instance.
     *
     * @param objectMapper the object mapper to set
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Returns the DecimalFormat instance used for formatting numbers.
     *
     * @return the decimal format
     */
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    /**
     * Sets the DecimalFormat instance.
     *
     * @param decimalFormat the decimal format to set
     */
    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    /**
     * Returns the input stream.
     *
     * @return the input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Sets the input stream.
     *
     * @param inputStream the input stream to set
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Returns the buffer size for input.
     *
     * @return the input buffer size
     */
    public int getBufferSizeIn() {
        return bufferSizeIn;
    }

    /**
     * Sets the buffer size for input.
     *
     * @param bufferSizeIn the input buffer size to set
     */
    public void setBufferSizeIn(int bufferSizeIn) {
        this.bufferSizeIn = bufferSizeIn;
    }

    /**
     * Returns the output stream.
     *
     * @return the output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Sets the output stream.
     *
     * @param outputStream the output stream to set
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Returns the buffer size for output.
     *
     * @return the output buffer size
     */
    public int getBufferSizeOut() {
        return bufferSizeOut;
    }

    /**
     * Sets the buffer size for output.
     *
     * @param bufferSizeOut the output buffer size to set
     */
    public void setBufferSizeOut(int bufferSizeOut) {
        this.bufferSizeOut = bufferSizeOut;
    }

    /**
     * Returns whether each line should be printed.
     *
     * @return true if each line should be printed, false otherwise
     */
    public boolean isPrintEveryLine() {
        return printEveryLine;
    }

    /**
     * Sets whether each line should be printed.
     *
     * @param printEveryLine true to print each line, false otherwise
     */
    public void setPrintEveryLine(boolean printEveryLine) {
        this.printEveryLine = printEveryLine;
    }

    /**
     * Returns whether timing information should be included.
     *
     * @return true if timings are enabled, false otherwise
     */
    public boolean isTimings() {
        return timings;
    }

    /**
     * Sets whether timing information should be included.
     *
     * @param timings true to enable timings, false to disable
     */
    public void setTimings(boolean timings) {
        this.timings = timings;
    }

    /**
     * Returns the predicate used to filter operations.
     *
     * @return the operation predicate
     */
    public Predicate<Operation> getOperationPredicate() {
        return operationPredicate;
    }

    /**
     * Sets the predicate used to filter operations.
     *
     * @param operationPredicate the operation predicate to set
     */
    public void setOperationPredicate(Predicate<Operation> operationPredicate) {
        this.operationPredicate = operationPredicate;
    }

    /**
     * Returns the supplier used to create Stock instances.
     *
     * @return the stock supplier
     */
    public Supplier<? extends Stock> getStockSupplier() {
        return stockSupplier;
    }

    /**
     * Sets the supplier used to create Stock instances.
     *
     * @param stockSupplier the stock supplier to set
     */
    public void setStockSupplier(Supplier<? extends Stock> stockSupplier) {
        this.stockSupplier = stockSupplier;
    }


    /**
     * Processes input data line by line from an {@link InputStream}, transforms each line using
     * the {@code calculate(String)} method, serializes the result to JSON using {@code objectMapper},
     * and writes it to an {@link OutputStream}.
     * <p>
     * The method can optionally flush the output after every line and display the total execution time,
     * depending on the values of the {@code printEveryLine} and {@code timings} flags.
     * </p>
     *
     * <p><strong>Additional behavior:</strong></p>
     * <ul>
     *   <li>If {@code printEveryLine} is {@code true}, sets the buffer size to 8192 bytes and flushes the output after each line.</li>
     *   <li>If {@code timings} is {@code true}, writes the total execution time in milliseconds at the end of the output.</li>
     * </ul>
     *
     * @throws RuntimeException if an {@link IOException} occurs while reading from or writing to the streams.
     */
    public void run() {

        final long startTime = System.currentTimeMillis();

        if (printEveryLine) {
            bufferSizeOut = 8_192;
        }

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), bufferSizeIn); final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream), bufferSizeOut)) {

            String line;
            while ((line = bufferedReader.readLine()) != null && !line.trim().isEmpty()) {
                bufferedWriter.write(this.objectMapper.writeValueAsString(this.calculate(line)));
                bufferedWriter.newLine();

                if (printEveryLine) bufferedWriter.flush();
            }

            final var endTime = System.currentTimeMillis();

            if (timings) {
                bufferedWriter.write("Total time taken: " + (endTime - startTime) + "ms");
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Parses a JSON-formatted string representing a list of operations, applies optional filtering,
     * processes each operation to calculate a tax, and returns the result as an {@link ArrayNode}.
     * <p>
     * Each operation is processed using the {@code processOperation} method, and the resulting tax is
     * formatted using {@code decimalFormat} and added to the returned JSON array as an object with a {@code "tax"} field.
     * </p>
     *
     * <p><strong>Filtering:</strong></p>
     * <ul>
     *   <li>If {@code operationPredicate} is set, it is used to filter the operations.</li>
     *   <li>If not set, all operations are processed.</li>
     * </ul>
     *
     * @param line the input JSON string representing a list of operations
     * @return an {@link ArrayNode} containing one object per operation, each with a formatted {@code "tax"} field
     * @throws JsonProcessingException if the input string cannot be parsed into a list of {@code Operation} objects
     */
    protected ArrayNode calculate(final String line) throws JsonProcessingException {

        final List<Operation> operationList = this.objectMapper.readerForListOf(Operation.class).readValue(line);
        final ArrayNode arrayNode = this.objectMapper.createArrayNode();
        final Stock stock = this.stockSupplier.get();

        final Predicate<Operation> operationPredicate = this.operationPredicate != null
                ? this.operationPredicate
                : operation -> true;

        operationList.stream().filter(operationPredicate).forEach(op -> {
            final BigDecimal taxa = processOperation(op, stock);
            arrayNode.add(this.objectMapper.createObjectNode().put("tax", this.decimalFormat.format(taxa)));
        });

        return arrayNode;
    }

    /**
     * Processes a single operation on the given {@link Stock} instance, modifying its state accordingly
     * and returning the calculated tax amount (if applicable).
     * <p>
     * Supports the following operations:
     * </p>
     * <ul>
     *   <li><b>"buy"</b> — Updates the stock with the purchased quantity and unit cost. Returns zero tax.</li>
     *   <li><b>"sell"</b> — Updates the stock based on the sale and returns the calculated tax as a {@link BigDecimal}.</li>
     * </ul>
     *
     * @param op    the {@link Operation} to be processed
     * @param stock the {@link Stock} instance to apply the operation to
     * @return the calculated tax for the operation, or {@code BigDecimal.ZERO} for a "buy"
     * @throws InvalidOperationException if the operation type is not recognized
     */
    protected BigDecimal processOperation(final Operation op, final Stock stock) {
        return switch (op.operation()) {
            case "buy" -> {
                stock.buy(op.quantity(), op.unitCost());
                yield BigDecimal.ZERO;
            }
            case "sell" -> stock.sell(op.quantity(), op.unitCost());
            default -> throw new InvalidOperationException(op.operation());
        };
    }

    /**
     * Constructs a new {@code TaxCalculator} instance using the provided {@link Builder}.
     * <p>
     * Initializes all necessary components such as input/output streams, object mapper,
     * formatting, predicates, and stock supplier. Also parses any provided arguments using
     * {@link ArgParser}.
     *
     * @param builder the builder instance containing all configuration and dependencies
     */
    protected TaxCalculator(final Builder builder) {
        this.args = builder.args;
        this.decimalFormat = builder.decimalFormat;
        this.inputStream = builder.inputStream;
        this.bufferSizeIn = builder.bufferSizeIn;
        this.outputStream = builder.outputStream;
        this.bufferSizeOut = builder.bufferSizeOut;
        this.objectMapper = builder.objectMapper;
        this.printEveryLine = builder.printEveryLine;
        this.timings = builder.timings;
        this.operationPredicate = builder.operationPredicate;
        this.stockSupplier = builder.stockSupplier;

        ArgParser.parseArgs(this.args, this);
    }

    /**
     * <p>Builder for new instances of {@code TaxCalculator}.</p>
     */
    public static class Builder {

        /**
         * <p>Constructs a new {@code Builder} instance, initializing the builder with the provided arguments.</p>
         *
         * @param args The array of arguments to initialize the builder.
         */
        public Builder(final String[] args) {
            this();
            this.args = args;
        }

        /**
         * <p>Constructs a new {@code Builder} instance.</p>
         */
        public Builder() {
            this.args = new String[0];
            final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            this.objectMapper = new ObjectMapper();
            this.decimalFormat = new DecimalFormat("#0.0", decimalFormatSymbols);
            this.inputStream = System.in;
            this.bufferSizeIn = 8_192;
            this.outputStream = System.out;
            this.bufferSizeOut = 8_192;
            this.stockSupplier = Stock::new;
        }

        private String[] args;
        private final ObjectMapper objectMapper;
        private DecimalFormat decimalFormat;
        private InputStream inputStream;
        private int bufferSizeIn;
        private OutputStream outputStream;
        private int bufferSizeOut;
        private boolean printEveryLine;
        private boolean timings;
        private Predicate<Operation> operationPredicate;
        private Supplier<? extends Stock> stockSupplier;


        /**
         * Sets the DecimalFormat to be used for formatting.
         *
         * @param decimalFormat the DecimalFormat instance
         * @return this builder instance
         */
        public Builder formattedWith(final DecimalFormat decimalFormat) {
            this.decimalFormat = decimalFormat;
            return this;
        }

        /**
         * Sets the input stream and input buffer size.
         *
         * @param inputStream  the input stream
         * @param bufferSizeIn the buffer size for reading
         * @return this builder instance
         */
        public Builder from(final InputStream inputStream, final int bufferSizeIn) {
            this.inputStream = inputStream;
            this.bufferSizeIn = bufferSizeIn;
            return this;
        }

        /**
         * Sets the output stream and output buffer size.
         *
         * @param outputStream  the output stream
         * @param bufferSizeOut the buffer size for writing
         * @return this builder instance
         */
        public Builder dumpTo(final OutputStream outputStream, final int bufferSizeOut) {
            this.outputStream = outputStream;
            this.bufferSizeOut = bufferSizeOut;
            return this;
        }

        /**
         * Specifies whether each processed line should be printed.
         *
         * @param printEveryLine true to print every line, false otherwise
         * @return this builder instance
         */
        public Builder printEveryLine(final boolean printEveryLine) {
            this.printEveryLine = printEveryLine;
            return this;
        }

        /**
         * Specifies whether timing metrics should be recorded.
         *
         * @param timings true to enable timings, false otherwise
         * @return this builder instance
         */
        public Builder timings(final boolean timings) {
            this.timings = timings;
            return this;
        }

        /**
         * Sets a predicate to filter operations that should be considered.
         *
         * @param operationPredicate the predicate to apply
         * @return this builder instance
         */
        public Builder onlyFor(final Predicate<Operation> operationPredicate) {
            this.operationPredicate = operationPredicate;
            return this;
        }

        /**
         * Sets the supplier responsible for creating Stock instances.
         *
         * @param stockSupplier the stock supplier
         * @return this builder instance
         */
        public Builder with(final Supplier<? extends Stock> stockSupplier) {
            this.stockSupplier = stockSupplier;
            return this;
        }

        /**
         * Builds and returns a new instance of {@link TaxCalculator} with the configured options.
         *
         * @return a new TaxCalculator instance
         */
        public TaxCalculator build() {
            return new TaxCalculator(this);
        }
    }
}
