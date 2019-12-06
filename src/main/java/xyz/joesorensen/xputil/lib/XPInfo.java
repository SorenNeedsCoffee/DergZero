package xyz.joesorensen.xputil.lib;

import java.util.Random;

public class XPInfo {
    private static Random random = new Random();

    public static double earnedXP(String msg) {
        double length = Math.sqrt(msg.replaceAll(" ", "").length());
        length = Math.min(10, length);
        return length * (Math.abs(random.nextGaussian()) * 5 + 1);
    }

    public static double lvlXpRequirement(int lvl) {
        //return lvl*250;
        return 250 + 75 * Math.pow(lvl, 0.6);
    }
}
