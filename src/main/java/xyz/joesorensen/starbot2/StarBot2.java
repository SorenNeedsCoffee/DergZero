package xyz.joesorensen.starbot2;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.joesorensen.starbot2.commands.admin.*;
import xyz.joesorensen.starbot2.commands.fun.*;
import xyz.joesorensen.starbot2.commands.general.AboutCmd;
import xyz.joesorensen.starbot2.commands.general.HelpCmd;
import xyz.joesorensen.starbot2.commands.general.InviteCmd;
import xyz.joesorensen.starbot2.commands.general.PingCmd;
import xyz.joesorensen.starbot2.commands.owner.ShutdownCmd;
import xyz.joesorensen.starbot2.listeners.Listener;
import xyz.joesorensen.starbot2.listeners.chains.Hi;
import xyz.joesorensen.starbot2.listeners.chains.Script;
import xyz.joesorensen.twitchutil.TwitchEventManager;
import xyz.joesorensen.twitchutil.TwitchListener;
import xyz.joesorensen.xputil.XPUtil;
import xyz.joesorensen.xputil.util.UserManager;

import javax.security.auth.login.LoginException;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
@SuppressWarnings("ConstantConditions")
public class StarBot2 {
    public static TwitchListener twitchListener;
    private static boolean shuttingDown = false;
    private static JDA jda = null;
    public static final String version = StarBot2.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger("Startup");
        final boolean enableDiscord = true;

        if (version != null)
            log.info("StarBot2 | v" + version);
        else
            log.info("StarBot2 | DEVELOPMENT MODE");

        log.info("Loading config...");
        Config config = Config.load();

        String token = config.getToken();
        String ownerID = config.getOwnerID();
        String defaultRoleID = config.getDefaultRoleID();
        String prefix = config.getPrefix();
        String clientID = config.getClientID();
        if ("".equals(token) || "".equals(ownerID) || "".equals(prefix) || "".equals(clientID) || "".equals(defaultRoleID)) {
            log.error("Incomplete config file. Please ensure that properties token, ownerID, clientID, defaultRoleID, and prefix are present and not empty");
            System.exit(1);
        }

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
                        new FakeCmd(),
                        new AvatarCmd(),
                        new ThesaurusCmd(),

                        new PingCmd(),

                        new TwitchPingCmd(),
                        new BackupCmd(),
                        new PruneCmd(),
                        new ChangeLvlCmd(),
                        new ChangeXPCmd(),
                        new GetMembersJSONCmd(),

                        new ShutdownCmd()
                );

        cb.setStatus(OnlineStatus.ONLINE);

        XPUtil xpUtil = new XPUtil(cb);
        xpUtil.db(config.getDbUrl(), config.getDbName(), config.getDbTable(), config.getDbUser(), config.getDbPass());
        cb = xpUtil.builder();

        CommandClient client = cb.build();
        Listener listener = new Listener();
        listener.setRoleID(defaultRoleID);
        listener.setPrefix(prefix);

        log.info("Attempting login...");

        if (enableDiscord) {
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(token)
                        .setStatus(OnlineStatus.DO_NOT_DISTURB)
                        .setActivity(Activity.playing("loading..."))
                        .addEventListeners(client, waiter, xpUtil.listener(), listener, new Hi(), new Script())
                        .build();
            } catch (LoginException ex) {
                log.error("Invalid Token");
                System.exit(1);
            }
        } else {
            log.info("DEV: Discord functionality disabled for testing purposes.");
        }

        twitchListener = new TwitchListener(clientID);
        TwitchEventManager.setListener(listener);
    }

    public static void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        UserManager.saveFile();
        if (jda.getStatus() != JDA.Status.SHUTTING_DOWN)
            jda.shutdown();
        System.exit(0);
    }
}
