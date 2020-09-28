package fyi.sorenneedscoffee.garbagecan.xp;

import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.boosters.BoosterManager;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.Booster;
import org.decimal4j.util.DoubleRounder;

import java.util.Random;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
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
        double preboost = length * (Math.abs(random.nextGaussian()) * 5 + 1);

        for (Booster booster : BoosterManager.boosters) {
            if (booster != null)
                preboost = preboost * booster.multiplier;
        }

        return preboost;
    }
}
