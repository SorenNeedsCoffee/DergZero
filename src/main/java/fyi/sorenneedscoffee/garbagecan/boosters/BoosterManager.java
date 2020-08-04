package fyi.sorenneedscoffee.garbagecan.boosters;

import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.boosters.data.DataContext;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.Booster;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.QueuedBooster;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class BoosterManager {
    public static final Booster[] boosters = new Booster[3];
    public static DataContext context;
    private final String[] slots = new String[]{
            "710236798340694037",
            "710236963156000828",
            "710236974723629277"
    };
    private final ArrayBlockingQueue<QueuedBooster> queue;
    private final Timer timer = new Timer();
    private final Guild guild;

    public BoosterManager() {
        this.guild = Main.jda.getGuildById("442552203694047232");

        context = new DataContext("jdbc:" + Main.config.dbUrl);

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

        if(!isFull()) {
            for(String slot : slots) {
                boolean isInUse = false;
                for(Booster booster : boosters) {
                    if(booster == null)
                        continue;

                    isInUse = slot.equals(booster.slotId);
                }

                if (!guild.getGuildChannelById(slot).getName().equals("-") && !isInUse) {
                    guild.getGuildChannelById(slot).getManager().setName("-").queue();
                }
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
                    break;
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
        private int waitTime = 5;
        private boolean rateLimit = false;
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
                String countdown = getCountdownString(duration);

                if (!countdown.equals(countdownCache) && !rateLimit) {
                    updateName(String.format("%.2f", booster.multiplier) + "x [" + countdown + "]", booster);
                    countdownCache = countdown;
                }
            }

            updateCounter++;
            if (updateCounter == waitTime) {
                updateCounter = 0;
            }
        }

        private String getCountdownString(Duration duration) {
            return String.format("%02d:%02d:%02d",
                                duration.toHours(),
                                duration.toMinutesPart(),
                                duration.toSecondsPart());
        }

        private void updateName(String name, Booster booster) {
            if(!rateLimit) {
                GuildChannel channel = guild.getGuildChannelById(booster.slotId);
                channel.getManager().setName(name).queue(null, (error) -> {
                    System.out.println("fail");
                    this.rateLimit = true;
                    channel.getManager()
                            .delay(60, TimeUnit.SECONDS)
                            .flatMap(m -> channel.getManager().setName(
                                    String.format(
                                            "%.2f", booster.multiplier) + "x [" +
                                            getCountdownString(Duration.between(LocalDateTime.now().plus(60, ChronoUnit.SECONDS), booster.expiration))
                                            + "]"
                            )).queue((success) -> {
                                this.rateLimit = false;});
                });
            }
        }
    }
}
