package fyi.sorenneedscoffee.derg_zero.xp;

import org.decimal4j.util.DoubleRounder;

import java.util.Random;

public class XPCalculator implements fyi.sorenneedscoffee.xputil.calculator.XPCalculator {
    private final Random random = new Random();

    @Override
    public double xpRequired(int i) {
        return DoubleRounder.round(250 + 75 * Math.pow(i, 0.6), 1);
    }

    @Override
    public double xpEarned(String s) {
        double length = Math.sqrt(s.replaceAll(" ", "").length());
        length = Math.min(10, length);
        return length * (Math.abs(random.nextGaussian()) * 5 + 1);
    }
}
