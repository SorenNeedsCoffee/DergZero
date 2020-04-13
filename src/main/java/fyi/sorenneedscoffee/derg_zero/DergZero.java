package fyi.sorenneedscoffee.derg_zero;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fyi.sorenneedscoffee.derg_zero.commands.admin.*;
import fyi.sorenneedscoffee.derg_zero.commands.fun.AvatarCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.HelCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.OobifyCmd;
import fyi.sorenneedscoffee.derg_zero.commands.fun.ThesaurusCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.AboutCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.HelpCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.InviteCmd;
import fyi.sorenneedscoffee.derg_zero.commands.general.PingCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderator.WarnCmd;
import fyi.sorenneedscoffee.derg_zero.commands.owner.ShutdownCmd;
import fyi.sorenneedscoffee.derg_zero.config.Config;
import fyi.sorenneedscoffee.derg_zero.config.ConfigManager;
import fyi.sorenneedscoffee.derg_zero.config.UsersDb;
import fyi.sorenneedscoffee.derg_zero.listeners.Listener;
import fyi.sorenneedscoffee.derg_zero.listeners.chains.Hi;
import fyi.sorenneedscoffee.derg_zero.listeners.chains.Script;
import fyi.sorenneedscoffee.derg_zero.moderation.DbManager;
import fyi.sorenneedscoffee.derg_zero.moderation.ModListener;
import fyi.sorenneedscoffee.xputil.XPUtil;
import fyi.sorenneedscoffee.xputil.util.UserManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.Console;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
@SuppressWarnings("ConstantConditions")
public class DergZero {
    public static final String version = DergZero.class.getPackage().getImplementationVersion();
    public static Script script;
    private static boolean shuttingDown = false;
    private static JDA jda = null;

    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger("Startup");
        final boolean enableDiscord = true;

        if (version != null)
            log.info("DergZero | v" + version);
        else
            log.info("DergZero | DEVELOPMENT MODE");

        log.info("Loading config...");
        Config config = ConfigManager.load();

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

                        new BackupCmd(),
                        new PruneCmd(),
                        new ChangeLvlCmd(),
                        new ChangeXPCmd(),
                        new GetMembersJSONCmd(),
                        new NewScriptCmd(),

                        new WarnCmd(),

                        new ShutdownCmd()
                );

        cb.setStatus(OnlineStatus.ONLINE);

        XPUtil xpUtil = new XPUtil(cb);
        UsersDb usersDb = config.getUsersDb();
        xpUtil.db(usersDb.getIp(), usersDb.getDb(), usersDb.getUser(), usersDb.getPass());
        cb = xpUtil.builder();

        DbManager.init(usersDb.getIp(), usersDb.getDb(), usersDb.getUser(), usersDb.getPass());

        CommandClient client = cb.build();
        Listener listener = new Listener();
        listener.setRoleID(defaultRoleID);

        script = new Script(config.getScriptDb());

        log.info("Attempting login...");

        if (enableDiscord) {
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(token)
                        .setStatus(OnlineStatus.DO_NOT_DISTURB)
                        .setActivity(Activity.playing("loading..."))
                        .addEventListeners(client,
                                waiter,
                                xpUtil.listener(),
                                listener,
                                new ModListener(),
                                new Hi(),
                                script)
                        .build();
            } catch (LoginException ex) {
                log.error("Invalid Token");
                System.exit(1);
            }
        } else {
            log.info("DEV: Discord functionality disabled for testing purposes.");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LoggerFactory.getLogger("DergZero").info("Shutting down...");
            shutdown();
        }));

        Console console = System.console();
        Thread th = new Thread(() -> {
            while (true) {
                String in = console.readLine();
                if("shutdown".equals(in))
                    System.exit(0);
            }
        });
        th.start();
    }

    public static void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        UserManager.saveFile();
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN)
            jda.shutdown();
    }
}
