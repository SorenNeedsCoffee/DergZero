package fyi.sorenneedscoffee.garbagecan.boosters.data.models;

import com.google.common.base.Objects;

import java.time.LocalDateTime;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class Booster {
    public final float multiplier;
    public final LocalDateTime expiration;
    public String slotId;

    public Booster(String slotId, float multiplier, LocalDateTime expiration) {
        this.slotId = slotId;
        this.multiplier = multiplier;
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Booster) {
            Booster b = (Booster) obj;

            return Objects.equal(slotId, b.slotId)
                    && Objects.equal(multiplier, b.multiplier)
                    && Objects.equal(expiration, b.expiration);
        }

        return false;
    }
}
