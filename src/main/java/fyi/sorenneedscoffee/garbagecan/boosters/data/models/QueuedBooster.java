package fyi.sorenneedscoffee.garbagecan.boosters.data.models;

import java.time.temporal.ChronoUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class QueuedBooster {
    public final int id;
    public final float multiplier;
    public final long duration;
    public final ChronoUnit unit;

    public QueuedBooster(int id, float multiplier, long duration, ChronoUnit unit) {
        this.id = id;
        this.multiplier = multiplier;
        this.duration = duration;
        this.unit = unit;
    }
}
