package fyi.sorenneedscoffee.derg_zero;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fyi.sorenneedscoffee.derg_zero.boosters.BoosterManager;
import fyi.sorenneedscoffee.derg_zero.boosters.listeners.BoosterXpListener;
import fyi.sorenneedscoffee.derg_zero.commands.admin.ModClearCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.AvatarCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.HelCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.OobifyCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.ThesaurusCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.AboutCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.HelpCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.InviteCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.PingCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.ViewWarningCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.ViewWarningsCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.WarnCmd;
import fyi.sorenneedscoffee.derg_zero.commands.owner.ShutdownCmd;
import fyi.sorenneedscoffee.derg_zero.commands.xp.BoosterCmd;
import fyi.sorenneedscoffee.derg_zero.commands.xp.LvlCmd;
import fyi.sorenneedscoffee.derg_zero.commands.xp.TopCmd;
import fyi.sorenneedscoffee.derg_zero.config.Config;
import fyi.sorenneedscoffee.derg_zero.config.ConfigManager;
import fyi.sorenneedscoffee.derg_zero.config.UsersDb;
import fyi.sorenneedscoffee.derg_zero.listeners.Listener;
import fyi.sorenneedscoffee.derg_zero.listeners.ModListener;
import fyi.sorenneedscoffee.derg_zero.listeners.chains.Hi;
import fyi.sorenneedscoffee.derg_zero.xp.HandlerEvents;
import fyi.sorenneedscoffee.derg_zero.xp.XPCalculator;
import fyi.sorenneedscoffee.derg_zero.xp.messages.MessageListener;
import fyi.sorenneedscoffee.derg_zero.xp.messages.MessageListenerAdapter;
import fyi.sorenneedscoffee.derg_zero.xp.roles.RoleListener;
import fyi.sorenneedscoffee.derg_zero.xp.roles.RoleListenerAdapter;
import fyi.sorenneedscoffee.xputil.data.DataContext;
import fyi.sorenneedscoffee.xputil.data.implementations.SQLDataContext;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.Console;
import java.util.concurrent.TimeUnit;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class DergZero {
    public static final String version = DergZero.class.getPackage().getImplementationVersion();
    public static DataContext context;
    public static XPCalculator calculator;
    public static EventHandler handler;
    public static BoosterManager manager;
    public static Config config;

    private static boolean shuttingDown = false;
    private static JDA jda = null;

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger("Startup");

        if (version != null)
            log.info("DergZero | v" + version);
        else
            log.info("DergZero | DEVELOPMENT MODE");

        log.info("Loading config...");
        config = ConfigManager.load();

        String token = config.getToken();
        String ownerID = config.getOwnerID();
        String defaultRoleID = config.getDefaultRoleID();
        String prefix = config.getPrefix();

        log.info("Building Command Client...");

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder cb = new CommandClientBuilder().
                setOwnerId(ownerID).
                setCoOwnerIds("275037176302141450", "231746019098886144").
                setPrefix(prefix).
                setHelpWord("help").
                setHelpConsumer(new HelpCmd()).
                setLinkedCacheSize(200).
                setActivity(Activity.playing("On Soren's server | " + prefix + "help for help")).
                setEmojis("\u2705", "\u26A0", "\u26D4").
                addCommands(
                        new AboutCmd(),
                        new InviteCmd(),

                        new HelCmd(),
                        new OobifyCmd(),
                        new AvatarCmd(),
                        new ThesaurusCmd(),

                        new PingCmd(),

                        new ModClearCmd(),

                        new WarnCmd(),
                        new ViewWarningCmd(),
                        new ViewWarningsCmd(),

                        new BoosterCmd(),
                        new LvlCmd(),
                        new TopCmd(),

                        new ShutdownCmd()
                );

        cb.setStatus(OnlineStatus.ONLINE);

        EventHandlerBuilder builder = new EventHandlerBuilder();
        UsersDb usersDb = config.getUsersDb();
        context = new SQLDataContext("jdbc:mariadb://" + usersDb.getIp() + ":3306/" + usersDb.getDb(), usersDb.getUser(), usersDb.getPass(),
                "MARIADB",
                "users",
                "group_id",
                "user_id",
                "lvl",
                "xp"
        );
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
        listener.setRoleID(defaultRoleID);

        log.info("Attempting login...");

        try {
            jda = JDABuilder.createDefault(token,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_EMOJIS,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.DIRECT_MESSAGES)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.playing("loading..."))
                    .addEventListeners(client,
                            waiter,
                            listener,
                            new Initializer(),
                            new MessageListenerAdapter(),
                            new RoleListenerAdapter(),
                            new HandlerEvents(handler),
                            new ModListener(),
                            new Hi()
                    )
                    .build();
            messageListener.setJda(jda);
            roleListener.setJda(jda);
        } catch (LoginException ex) {
            log.error("Invalid Token");
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
            manager = new BoosterManager(jda, config.getBoostersDb());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LoggerFactory.getLogger("DergZero").info("Shutting down...");
                shutdown();
            }));

            Console console = System.console();
            Thread th = new Thread(() -> {
                while (true) {
                    String in = console.readLine();
                    if ("shutdown".equals(in))
                        System.exit(0);
                }
            });
            th.start();
        }
    }
}
