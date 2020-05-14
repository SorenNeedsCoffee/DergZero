package fyi.sorenneedscoffee.derg_zero.boosters.data.models;

import com.google.common.base.Objects;

import java.time.LocalDateTime;

public class Booster {
    public final double multiplier;
    public final LocalDateTime expiration;
    public String slotId;

    public Booster(String slotId, double multiplier, LocalDateTime expiration) {
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
