package fyi.sorenneedscoffee.derg_zero.boosters.listeners;

import fyi.sorenneedscoffee.derg_zero.DergZero;
import fyi.sorenneedscoffee.derg_zero.boosters.BoosterResult;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BoosterListener extends ListenerAdapter {
    Random random = new Random();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int rand = random.nextInt(15);
                if (rand == 3) {
                    double multiplier = 1.1 + (2.0 - 1.1) * random.nextDouble();

                    if (!DergZero.manager.isFull()) {
                        DergZero.manager.add(multiplier, 90, ChronoUnit.MINUTES, false);
                    }
                }
            }
        }, 0, TimeUnit.HOURS.toMillis(3));
    }

    @Override
    public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
        if (event.getNewBoostCount() > event.getOldBoostCount()) {
            double multiplier = 1.1 + (2.0 - 1.1) * random.nextDouble();

            BoosterResult result = DergZero.manager.add(multiplier, 2, ChronoUnit.HOURS, true);
            if (result == BoosterResult.QUEUED) {
                event.getGuild().getTextChannelById("442556155856814080").sendMessage("Someone just boosted the server! A " + multiplier + "x booster has been queued!");
            } else if (result == BoosterResult.ADDED) {
                event.getGuild().getTextChannelById("442556155856814080").sendMessage("Someone just boosted the server! A " + multiplier + "x booster is now active!");
            }
        }
    }
}
