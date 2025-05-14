package me.thiagorigonatti.capitalgains;

import me.thiagorigonatti.capitalgains.core.TaxCalculator;

public class CapitalGainsCalculator {

    public static void main(String[] args) {
        final TaxCalculator taxCalculator = new TaxCalculator.Builder(args).build();
        taxCalculator.run();
    }
}
