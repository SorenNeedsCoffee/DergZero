package xyz.joesorensen.starbot2;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.joesorensen.starbot2.commands.admin.*;
import xyz.joesorensen.starbot2.commands.fun.AvatarCmd;
import xyz.joesorensen.starbot2.commands.fun.FakeCmd;
import xyz.joesorensen.starbot2.commands.fun.HelCmd;
import xyz.joesorensen.starbot2.commands.fun.OobifyCmd;
import xyz.joesorensen.starbot2.commands.general.HelpCmd;
import xyz.joesorensen.starbot2.commands.general.InviteCmd;
import xyz.joesorensen.starbot2.commands.owner.ShutdownCmd;
import xyz.joesorensen.starbot2.listeners.Listener;
import xyz.joesorensen.twitchutil.TwitchEventManager;
import xyz.joesorensen.twitchutil.TwitchListener;
import xyz.joesorensen.xputil.UserManager;
import xyz.joesorensen.xputil.XPUtil;

import javax.security.auth.login.LoginException;
import java.awt.*;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class StarBot2 {
    private final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
    public static TwitchListener twitchListener;
    private static JDA jda = null;
    public static boolean shuttingDown = false;
    private static String version = StarBot2.class.getPackage().getImplementationVersion();

    public static void main(String[] args) throws Exception {
        Logger log = LoggerFactory.getLogger("Startup");
        final boolean enableDiscord = false;

        log.info("StarBot2 v" + version);

        log.info("Loading config...");
        Config config = Config.load();

        String token = config.getToken();
        String ownerID = config.getOwnerID();
        String defaultRoleID = config.getDefaultRoleID();
        String prefix = config.getPrefix();
        String clientID = config.getClientID();
        if (token.equals("") || ownerID.equals("") || prefix.equals("") || clientID.equals("") || defaultRoleID.equals("")) {
            log.error("Incomplete config file. Please ensure that properties token, ownerID, clientID, defaultRoleID, and prefix are present and not empty");
            System.exit(1);
        }

        log.info("Building Command Client...");

        AboutCommand ab = new AboutCommand(
                Color.BLUE, "StarBot, but better! JoeSorensen's official server bot. (v" + version + ")",
                new String[]{"Stream Tracking via TwitchUtil", "User engagement with XPUtil", "Random and fun stuff"},
                RECOMMENDED_PERMS
        );

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
                        ab,
                        new InviteCmd(),

                        new HelCmd(),
                        new OobifyCmd(),
                        new FakeCmd(),
                        new AvatarCmd(),

                        new PingCommand(),

                        new TwitchPingCmd(),
                        new SaveCmd(),
                        new PruneCmd(),
                        new ChangeLvlCmd(),
                        new ChangeXPCmd(),

                        new ShutdownCmd()
                );

        cb.setStatus(OnlineStatus.ONLINE);

        XPUtil xpUtil = new XPUtil(cb);
        xpUtil.db(config.getDbUrl(), config.getDbUser(), config.getDbPass());
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
                        .addEventListeners(client, waiter, xpUtil.listener(), listener)
                        .build();
            } catch (LoginException ex) {
                log.error("Invalid Token");
                System.exit(1);
            }

            listener.setJDA(jda);
            xpUtil.setJDA(jda);
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
