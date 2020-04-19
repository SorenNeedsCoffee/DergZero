package fyi.sorenneedscoffee.derg_zero.moderation.warnings;

public enum OffenseType {
    MISC(-1, "Miscellaneous", "Fake warning, for mostly jokes. If you receive this offense type, don't worry! You're not actually in trouble"),
    GENERAL(0, "General", "A warning that doesn't fall under any other category"),
    RULE_VIOLATION(1, "Rule Violation", "You'll receive this warning if you broke a rule.");

    private int id;
    private String shortName, description;

    OffenseType(int id, String shortName, String description) {
        this.id = id;
        this.shortName = shortName;
        this.description = description;
    }
    
    public int getId() {
        return this.id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }
    
    public static OffenseType getTypeById(int id) {
        switch(id) {
            default:
                return null;
            case -1:
                return OffenseType.MISC;
            case 0:
                return OffenseType.GENERAL;
            case 1:
                return OffenseType.RULE_VIOLATION;
        }
    }
}
