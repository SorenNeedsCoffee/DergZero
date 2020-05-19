package fyi.sorenneedscoffee.derg_zero.boosters;

import fyi.sorenneedscoffee.derg_zero.boosters.data.DataContext;
import fyi.sorenneedscoffee.derg_zero.boosters.data.models.Booster;
import fyi.sorenneedscoffee.derg_zero.boosters.data.models.QueuedBooster;
import fyi.sorenneedscoffee.derg_zero.commands.xp.BoosterCmd;
import fyi.sorenneedscoffee.derg_zero.config.BoostersDb;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class BoosterManager {
    public final Booster[] boosters = new Booster[3];
    public final DataContext context;
    private final String[] slots = new String[]{
            "710236798340694037",
            "710236963156000828",
            "710236974723629277"
    };
    private final ArrayBlockingQueue<QueuedBooster> queue;
    private final Timer timer = new Timer();
    private final Guild guild;

    public BoosterManager(JDA jda, BoostersDb db) {
        this.guild = jda.getGuildById("442552203694047232");

        context = new DataContext("jdbc:mariadb://" + db.getIp() + ":3306/" + db.getDb(), db.getUser(), db.getPass());
        BoosterCmd.setContext(context);

        queue = new ArrayBlockingQueue<>(50, true, context.getQueue());

        List<Booster> activeBoosters = context.getBoosters();

        for (Booster booster : activeBoosters) {
            if (booster.expiration.isAfter(LocalDateTime.now())) {
                for (int i = 0; i < boosters.length; i++) {
                    if (boosters[i] == null) {
                        boosters[i] = booster;
                        break;
                    }
                }
                activate(booster);
            } else {
                context.removeActiveBooster(booster.slotId);
            }
        }

        while (!isFull()) {
            if (!queue.isEmpty()) {
                QueuedBooster queuedBooster = queue.poll();
                context.removeQueuedBooster(queuedBooster.id);
                add(queuedBooster.multiplier, queuedBooster.duration, queuedBooster.unit, true);
            } else {
                break;
            }
        }
    }

    public boolean isFull() {
        for (Booster booster : boosters) {
            if (booster == null)
                return false;
        }

        return true;
    }

    public BoosterResult add(float multiplier, long duration, ChronoUnit unit, boolean queueIfFull) {
        if (isFull() && queueIfFull) {
            QueuedBooster queuedBooster = new QueuedBooster(context.getNewQId(), multiplier, duration, unit);
            if (queue.offer(queuedBooster)) {
                context.saveQueuedBooster(queuedBooster);
                return BoosterResult.QUEUED;
            } else {
                return BoosterResult.NO_SLOTS_AVAILABLE;
            }
        }

        Booster booster = new Booster(firstAvailableSlot(), multiplier, LocalDateTime.now().plus(duration, unit));

        for (int i = 0; i < boosters.length; i++) {
            if (boosters[i] == null) {
                boosters[i] = booster;
                break;
            }
        }

        activate(booster);

        context.saveActiveBooster(booster);

        return BoosterResult.ADDED;
    }

    private void activate(Booster booster) {
        timer.scheduleAtFixedRate(new Updater(booster), 0, 1000);
    }

    public String firstAvailableSlot() {
        String result = null;
        for (String slot : slots) {
            result = slot;
            for (Booster booster : boosters) {
                if (booster != null && booster.slotId.equals(slot)) {
                    result = null;
                }
            }
            if (result != null)
                break;
        }

        return result;
    }

    public void remove(Booster booster) {
        for (int i = 0; i < boosters.length; i++) {
            if (boosters[i] == booster) {
                boosters[i] = null;
                break;
            }
        }

        context.removeActiveBooster(booster.slotId);

        if (!queue.isEmpty()) {
            QueuedBooster queuedBooster = queue.poll();
            context.removeQueuedBooster(queuedBooster.id);
            add(queuedBooster.multiplier, queuedBooster.duration, queuedBooster.unit, true);
        }
    }

    private class Updater extends TimerTask {
        private final Booster booster;
        private int updateCounter = 0;
        private String countdownCache = "";

        protected Updater(Booster booster) {
            this.booster = booster;
        }

        @Override
        public void run() {
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(now, booster.expiration);

            if (duration.isZero() || duration.isNegative()) {
                updateName("-", booster);

                remove(booster);
                cancel();
            } else if (updateCounter == 0) {
                String countdown = String.format("%02d:%02d:%02d",
                        duration.toHours(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart());

                if (!countdown.equals(countdownCache)) {
                    updateName(String.format("%.2f", booster.multiplier) + "x [" + countdown + "]", booster);
                    countdownCache = countdown;
                }
            }

            updateCounter++;
            if (updateCounter == 5) {
                updateCounter = 0;
            }
        }

        private void updateName(String name, Booster booster) {
            GuildChannel channel = guild.getGuildChannelById(booster.slotId);
            channel.getManager().setName(name).queue();
        }
    }
}
