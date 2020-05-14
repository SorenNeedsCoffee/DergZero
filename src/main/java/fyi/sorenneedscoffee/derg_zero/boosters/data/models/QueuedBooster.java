package fyi.sorenneedscoffee.derg_zero.boosters.data.models;

import java.time.temporal.ChronoUnit;

public class QueuedBooster {
    public final int id;
    public final double multiplier;
    public final long duration;
    public final ChronoUnit unit;

    public QueuedBooster(int id, double multiplier, long duration, ChronoUnit unit) {
        this.id = id;
        this.multiplier = multiplier;
        this.duration = duration;
        this.unit = unit;
    }
}
