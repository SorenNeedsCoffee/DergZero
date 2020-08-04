package fyi.sorenneedscoffee.garbagecan;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fyi.sorenneedscoffee.garbagecan.boosters.BoosterManager;
import fyi.sorenneedscoffee.garbagecan.boosters.listeners.BoosterListener;
import fyi.sorenneedscoffee.garbagecan.boosters.listeners.BoosterXpListener;
import fyi.sorenneedscoffee.garbagecan.config.Config;
import fyi.sorenneedscoffee.garbagecan.config.ConfigManager;
import fyi.sorenneedscoffee.garbagecan.listeners.Listener;
import fyi.sorenneedscoffee.garbagecan.listeners.ModListener;
import fyi.sorenneedscoffee.garbagecan.listeners.chains.Hi;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import fyi.sorenneedscoffee.garbagecan.xp.HandlerEvents;
import fyi.sorenneedscoffee.garbagecan.xp.XPCalculator;
import fyi.sorenneedscoffee.garbagecan.xp.data.SQLDataContext;
import fyi.sorenneedscoffee.garbagecan.xp.messages.MessageListener;
import fyi.sorenneedscoffee.garbagecan.xp.messages.MessageListenerAdapter;
import fyi.sorenneedscoffee.garbagecan.xp.roles.RoleListener;
import fyi.sorenneedscoffee.garbagecan.xp.roles.RoleListenerAdapter;
import fyi.sorenneedscoffee.xputil.data.DataContext;
import fyi.sorenneedscoffee.xputil.handler.EventHandler;
import fyi.sorenneedscoffee.xputil.handler.EventHandlerBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class Main {
    public static final String version = Main.class.getPackage().getImplementationVersion();
    public static DataContext context;
    public static XPCalculator calculator;
    public static EventHandler handler;
    public static BoosterManager manager;
    public static Config config;
    public static JDA jda = null;

    private static boolean shuttingDown = false;

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("Startup");

        if (version != null)
            log.info("Garbage Can | v" + version);
        else
            log.info("Garbage Can | DEVELOPMENT MODE");

        log.info("Loading config...");
        config = ConfigManager.load();

        log.info("Building Command Client...");

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder cb = new CommandClientBuilder().
                setOwnerId("335416268452528129").
                setCoOwnerIds("275037176302141450", "231746019098886144").
                setPrefix(config.prefix).
                setHelpWord("help").
                setLinkedCacheSize(200).
                setActivity(Activity.playing("On Soren's server | " + config.prefix + "help for help")).
                setEmojis("\u2705", "\u26A0", "\u26D4");

        cb.setStatus(OnlineStatus.ONLINE);
        new Reflections("fyi.sorenneedscoffee.garbagecan.commands")
                .getSubTypesOf(Command.class)
                .stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .forEach(c -> {
                    try {
                        cb.addCommand(c.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });

        EventHandlerBuilder builder = new EventHandlerBuilder();
        context = new SQLDataContext("jdbc:" + config.dbUrl);
        calculator = new XPCalculator();
        MessageListener messageListener = new MessageListener();
        RoleListener roleListener = new RoleListener();
        builder = builder.addContext(context)
                .enableCooldown()
                .setCooldownValue(5, TimeUnit.SECONDS)
                .addContext(context)
                .addCalculator(calculator)
                .addListeners(messageListener, roleListener, new BoosterXpListener());

        handler = builder.build();

        CommandClient client = cb.build();
        Listener listener = new Listener();
        listener.setRoleID(config.defaultRoleID);

        log.info("Attempting login...");

        try {
            jda = JDABuilder.createDefault(config.token)
                    .enableIntents(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES
                    )
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.playing("loading..."))
                    .addEventListeners(client,
                            waiter,
                            listener,
                            new Initializer(),
                            new MessageListenerAdapter(),
                            new RoleListenerAdapter(),
                            new BoosterListener(),
                            new HandlerEvents(handler),
                            new ModListener(),
                            new BoosterListener(),
                            new Hi()
                    )
                    .build();
        } catch (LoginException ex) {
            log.error(ex.getMessage());
            System.exit(1);
        }
    }

    public static void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN)
            jda.shutdown();
    }

    private static class Initializer extends ListenerAdapter {

        @Override
        public void onReady(@Nonnull ReadyEvent event) {
            manager = new BoosterManager();
            ModUtil.init();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LoggerFactory.getLogger("Main").info("Shutting down...");
                shutdown();
            }));
        }
    }
}
