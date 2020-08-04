package fyi.sorenneedscoffee.garbagecan.moderation.warnings;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public enum WarningResult {
    NO_ACTION(new Warning()),
    KICK_ACTION(new Warning()),
    BAN_ACTION(new Warning()),
    ERROR(null);

    public boolean previouslyKicked = false;
    private Warning warning;

    WarningResult(Warning warning) {
        this.warning = warning;
    }

    public void overrideWarning(Warning warning) {
        this.warning = warning;
    }

    public Warning getWarning() {
        return this.warning;
    }
}
