package fyi.sorenneedscoffee.garbagecan.boosters.data.models;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class UserBooster {
    public final int id;
    public final String userId;
    public final float multiplier;
    public final long duration;
    public final ChronoUnit unit;

    public UserBooster(int id, String userId, float multiplier, long duration, ChronoUnit unit) {
        this.id = id;
        this.userId = userId;
        this.multiplier = multiplier;
        this.duration = duration;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return multiplier + "x for " + Duration.of(duration, unit).toString().replaceFirst("PT", "").toLowerCase();
    }
}
