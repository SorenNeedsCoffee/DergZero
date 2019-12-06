package xyz.joesorensen.xputil.lib;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public enum LvlRoleIDs {
    LVL1("618904321500774414"),
    LVL5("618904412383084584"),
    LVL10("618904540355231745"),
    LVL15("618904667220344863"),
    LVL20("618904805485707276"),
    LVL25("618904888398839868"),
    LVL30("618904956245770302"),
    LVL35("618905019688681510"),
    LVL40("618905084352528434"),
    LVL45("618905139717210127"),
    LVL50("618905388565397536"),
    LVL55("618905445834424342"),
    LVL60("618905502780358657"),
    LVL65("618905564092825631"),
    LVL70("618905626743144489"),
    LVL75("618905744762470420");

    String id;

    LvlRoleIDs(String id) {
        this.id = id;
    }

    public static String getLvlRole(int lvl) {
        if (isBetween(lvl, 1, 4)) {
            return LVL1.getId();
        }
        if (isBetween(lvl, 5, 9)) {
            return LVL5.getId();
        }
        if (isBetween(lvl, 10, 14)) {
            return LVL10.getId();
        }
        if (isBetween(lvl, 15, 19)) {
            return LVL15.getId();
        }
        if (isBetween(lvl, 20, 24)) {
            return LVL20.getId();
        }
        if (isBetween(lvl, 25, 29)) {
            return LVL25.getId();
        }
        if (isBetween(lvl, 30, 34)) {
            return LVL30.getId();
        }
        if (isBetween(lvl, 35, 39)) {
            return LVL35.getId();
        }
        if (isBetween(lvl, 40, 44)) {
            return LVL40.getId();
        }
        if (isBetween(lvl, 45, 49)) {
            return LVL45.getId();
        }
        if (isBetween(lvl, 50, 54)) {
            return LVL50.getId();
        }
        if (isBetween(lvl, 55, 59)) {
            return LVL55.getId();
        }
        if (isBetween(lvl, 60, 64)) {
            return LVL60.getId();
        }
        if (isBetween(lvl, 65, 69)) {
            return LVL65.getId();
        }
        if (isBetween(lvl, 70, 74)) {
            return LVL70.getId();
        }
        return LVL75.getId();
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public String getId() {
        return this.id;
    }
}
