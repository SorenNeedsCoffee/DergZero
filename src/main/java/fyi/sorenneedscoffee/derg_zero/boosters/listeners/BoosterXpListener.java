package fyi.sorenneedscoffee.derg_zero.boosters.listeners;

import fyi.sorenneedscoffee.derg_zero.DergZero;
import fyi.sorenneedscoffee.derg_zero.boosters.data.models.UserBooster;
import fyi.sorenneedscoffee.xputil.events.xp.LevelUpEvent;
import fyi.sorenneedscoffee.xputil.listener.XPListener;

import java.time.temporal.ChronoUnit;

public class BoosterXpListener extends XPListener {
    @Override
    public void onLevelUp(LevelUpEvent event) {
        if(event.getNewLevel() >= 20 && event.getNewLevel() % 10 == 0) {
            DergZero.manager.context.saveUserBooster(new UserBooster(
                    DergZero.manager.context.getNewUId(),
                    event.getUserId(),
                    2.5,
                    90,
                    ChronoUnit.MINUTES
            ));
        }
    }
}
