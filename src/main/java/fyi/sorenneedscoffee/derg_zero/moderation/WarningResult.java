package fyi.sorenneedscoffee.derg_zero.moderation;

public enum WarningResult {
    NO_ACTION(new Warning()), KICK_ACTION(new Warning()), BAN_ACTION(new Warning()), ERROR(null);

    private Warning warning;

    WarningResult(Warning warning) {
        this.warning = warning;
    }

    void overrideWarning(Warning warning) {
        this.warning = warning;
    }

    public Warning getWarning() {
        return this.warning;
    }
}
