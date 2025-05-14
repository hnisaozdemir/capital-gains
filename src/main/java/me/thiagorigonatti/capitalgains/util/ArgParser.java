package me.thiagorigonatti.capitalgains.util;

import me.thiagorigonatti.capitalgains.core.TaxCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for parsing command-line arguments and applying them
 * to a {@link TaxCalculator} instance.
 * <p>
 * Supports the following flags:
 * <ul>
 *   <li><b>-pel</b>: Enables printing of the tax result for every line (Print Every Line).</li>
 *   <li><b>-t</b>: Enables timing measurements for the execution.</li>
 *   <li><b>-bsi&lt;size&gt;&lt;unit&gt;</b>: Sets the input buffer size (e.g., -bsi512k, -bsi1m).</li>
 *   <li><b>-bso&lt;size&gt;&lt;unit&gt;</b>: Sets the output buffer size (e.g., -bso1m, -bso2g).</li>
 * </ul>
 *
 * <p>
 * Accepted units for buffer size: <code>k</code> (kilobytes), <code>m</code> (megabytes),
 * <code>g</code> (gigabytes). The size must be a number from 1 to 999.
 * </p>
 *
 * @author <a href="https://github.com/thiagorigonatti">Thiago Rigonatti</a>
 * @version 1.0
 * @since 1.0
 */
public class ArgParser {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws AssertionError always, as this class should not be instantiated
     */
    private ArgParser() {
        throw new AssertionError("Instantiation of utility class...");
    }

    /**
     * Regular expression pattern used to match and extract buffer size arguments.
     * Supported forms: <code>-bsi512k</code>, <code>-bso2m</code>, etc.
     */
    private static final Pattern pattern = Pattern
            .compile("^(?i)((?<arg>-bsi|-bso)(?<size>\\d{1,3})(?<exp>[kmg]))$");

    /**
     * Parses the given command-line arguments and applies the corresponding configuration
     * to the provided {@link TaxCalculator} instance.
     *
     * @param args          the array of command-line arguments
     * @param taxCalculator the {@link TaxCalculator} to configure
     * @throws NumberFormatException if a buffer size value exceeds {@link Integer#MAX_VALUE}
     */
    public static void parseArgs(final String[] args, final TaxCalculator taxCalculator) {

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-pel")) {
                taxCalculator.setPrintEveryLine(true);

            } else if (arg.equalsIgnoreCase("-t")) {
                taxCalculator.setTimings(true);

            } else if (arg.toLowerCase().startsWith("-bs")) {
                Matcher matcher = pattern.matcher(arg);

                if (matcher.matches()) {
                    int size = Integer.parseInt(matcher.group("size"));
                    char c = matcher.group("exp").toLowerCase().charAt(0);
                    int exp = c == 'g' ? 3 : c == 'm' ? 2 : 1;

                    double pow = Math.pow(1_024, exp) * size;

                    if (pow > Integer.MAX_VALUE)
                        throw new NumberFormatException("Too many bytes.");
                    size = (int) pow;

                    if (matcher.group("arg").toLowerCase().startsWith("-bsi"))
                        taxCalculator.setBufferSizeIn(size);
                    else if (matcher.group("arg").toLowerCase().startsWith("-bso"))
                        taxCalculator.setBufferSizeOut(size);

                } else {
                    System.err.println("Invalid argument: " + arg);
                    System.exit(1);
                }
            }
        }
    }
}
