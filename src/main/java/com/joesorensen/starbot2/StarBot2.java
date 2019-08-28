package com.joesorensen.starbot2;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.joesorensen.starbot2.commands.admin.TwitchPingCmd;
import com.joesorensen.starbot2.commands.fun.HelCmd;
import com.joesorensen.starbot2.commands.owner.ShutdownCmd;
import com.joesorensen.starbot2.listeners.Listener;
import com.joesorensen.starbot2.listeners.TwitchEventManager;
import com.joesorensen.starbot2.listeners.TwitchListener;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StarBot2 {
    private final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};

    private final static String version = "1.0-SNAPSHOT";
    private static JDA jda = null;
    private static boolean shuttingDown = false;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger log = LoggerFactory.getLogger("Startup");

        log.info("StarBot2 v" + version);

        log.info("Loading config...");

        Object raw = null;
        try {
            raw = new JSONParser().parse(new FileReader("config.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: config file not found. Please ensure that the config file exsists, is in the same directory as the jar, and is called config.json");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
        JSONObject config = (JSONObject) raw;
        String token = (String) config.get("token");
        String ownerID = (String) config.get("ownerID");
        String prefix = (String) config.get("prefix");
        String clientID = (String) config.get("clientID");
        if (token.equals("") || ownerID.equals("") || prefix.equals("") || clientID.equals("")) {
            log.error("Incomplete config file. Please ensure that properties token, ownerID, clientID, and prefix are present and not empty");
            System.exit(1);
        }

        log.info("Building Command Client...");

        AboutCommand ab = new AboutCommand(
                Color.CYAN.brighter(), "StarBot, but better! JoeSorensen's official server bot. (v" + version + ")",
                new String[]{"Stream Tracking", "Join Events"},
                RECOMMENDED_PERMS
        );

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder cb = new CommandClientBuilder().
                setOwnerId(ownerID).
                setPrefix(prefix).
                setHelpWord("help").
                setLinkedCacheSize(200).
                setActivity(Activity.playing("On Soren's server | >help for help")).
                addCommands(ab,
                        new HelCmd(),

                        new TwitchPingCmd(clientID),

                        new ShutdownCmd()
                );

        cb.setStatus(OnlineStatus.ONLINE);

        CommandClient client = cb.build();
        Listener listener = new Listener();

        log.info("Attempting login...");

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setActivity(Activity.playing("loading..."))
                    .addEventListeners(client, waiter, listener)
                    .build();
        } catch (LoginException ex) {
            log.error("Invalid Token");
            System.exit(1);
        }

        listener.setJDA(jda);

        TwitchListener twitchListener = new TwitchListener(clientID);
        TwitchEventManager.setListener(listener);
        twitchListener.track("JoeSorensen");
    }

    public static void shutdown() {
        if (shuttingDown)
            return;
        shuttingDown = true;
        jda.shutdown();
        System.exit(0);
    }
}
