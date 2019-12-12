package xyz.joesorensen.xputil;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import xyz.joesorensen.xputil.commands.xp.LvlCmd;
import xyz.joesorensen.xputil.commands.xp.TopCmd;
import xyz.joesorensen.xputil.util.UserManager;
import xyz.joesorensen.xputil.util.XpListener;

/**
 * -=XPUtil=-
 * A flexible User XP library in active development.
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */

public class XPUtil {
    private final CommandClientBuilder builder;
    private final XpListener listener;

    /**
     * On startup, you can initialize the XPUtil class to handle command client and listener setup. Be sure to add the XPListener to JDA!
     *
     * @param builder Pass your CommandClientBuilder here. This will only add the current level and top commands and will NOT build the client for you.
     */
    public XPUtil(CommandClientBuilder builder) {
        builder.addCommands(
                new LvlCmd(),
                new TopCmd()
        );
        this.builder = builder;
        this.listener = new XpListener();
    }

    public void db(String url, String name, String table, String user, String pass) throws Exception {
        UserManager.initDb(url, name, table, user, pass);
    }

    public CommandClientBuilder builder() {
        return this.builder;
    }

    public XpListener listener() {
        return this.listener;
    }

    public void setJDA(JDA jda) {
        this.listener.setJDA(jda);
    }
}
