package xyz.joesorensen.xputil.lib;

import java.util.Random;
import org.decimal4j.util.DoubleRounder;

public class XpInfo {
    private static Random random = new Random();

    public static double earnedXP(String msg) {
        double length = Math.sqrt(msg.replaceAll(" ", "").length());
        length = Math.min(10, length);
        return length * (Math.abs(random.nextGaussian()) * 5 + 1);
    }

    public static double lvlXpRequirement(int lvl) {
        //return lvl*250;
        return DoubleRounder.round(250 + 75 * Math.pow(lvl, 0.6), 1);
    }

    public static double lvlXpRequirementTotal(int lvl) {
        //return lvl*250;
        double result = 0;
        for(int i = 1; i <= lvl; i++) {
            result += 250 + 75 * Math.pow(i, 0.6);
        }
        result = DoubleRounder.round(result, 1);
        return result;
    }
}
