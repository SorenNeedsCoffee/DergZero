package fyi.sorenneedscoffee.garbagecan.boosters.listeners;

import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.boosters.BoosterResult;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class BoosterListener extends ListenerAdapter {
    private final Random random = new Random();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int rand = random.nextInt(32);
                if (rand == 3) {
                    float multiplier = 1.1f + (2.0f - 1.1f) * random.nextFloat();

                    if (!Main.manager.isFull()) {
                        Main.manager.add(multiplier, 90, ChronoUnit.MINUTES, false);
                    }
                }
            }
        }, 0, TimeUnit.HOURS.toMillis(3));
    }

    @Override
    public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
        if (event.getNewBoostCount() > event.getOldBoostCount()) {
            float multiplier = 1.1f + (2.0f - 1.1f) * random.nextFloat();

            BoosterResult result = Main.manager.add(multiplier, 2, ChronoUnit.HOURS, true);
            if (result == BoosterResult.QUEUED) {
                event.getGuild().getTextChannelById("442556155856814080").sendMessage("Someone just boosted the server! A " + multiplier + "x booster has been queued!");
            } else if (result == BoosterResult.ADDED) {
                event.getGuild().getTextChannelById("442556155856814080").sendMessage("Someone just boosted the server! A " + multiplier + "x booster is now active!");
            }
        }
    }
}
