package fyi.sorenneedscoffee.garbagecan.boosters.listeners;

import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.UserBooster;
import fyi.sorenneedscoffee.xputil.events.xp.LevelUpEvent;
import fyi.sorenneedscoffee.xputil.listener.XPListener;

import java.time.temporal.ChronoUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class BoosterXpListener extends XPListener {
    @Override
    public void onLevelUp(LevelUpEvent event) {
        if (event.getNewLevel() >= 20 && event.getNewLevel() % 10 == 0) {
            Main.manager.context.saveUserBooster(new UserBooster(
                    Main.manager.context.getNewUId(),
                    event.getUserId(),
                    2.5f,
                    90,
                    ChronoUnit.MINUTES
            ));
        }
    }
}
