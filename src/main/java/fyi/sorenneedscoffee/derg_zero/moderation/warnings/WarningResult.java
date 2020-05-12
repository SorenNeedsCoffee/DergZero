package fyi.sorenneedscoffee.derg_zero.moderation.warnings;

public enum WarningResult {
    NO_ACTION(new Warning()), KICK_ACTION(new Warning()), BAN_ACTION(new Warning()), ERROR(null);

    private Warning warning;
    public boolean previouslyKicked = false;

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
