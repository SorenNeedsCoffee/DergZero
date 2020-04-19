package fyi.sorenneedscoffee.derg_zero;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import fyi.sorenneedscoffee.derg_zero.commands.admin.ModClearCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.ViewWarningCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.ViewWarningsCmd;
import fyi.sorenneedscoffee.derg_zero.commands.moderation.WarnCmd;
import fyi.sorenneedscoffee.derg_zero.config.Config;
import fyi.sorenneedscoffee.derg_zero.config.ConfigManager;
import fyi.sorenneedscoffee.derg_zero.config.UsersDb;
import fyi.sorenneedscoffee.derg_zero.moderation.util.DbManager;
import fyi.sorenneedscoffee.derg_zero.moderation.util.ModListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Testing {
    public static void main(String[] args) {
        Config config = ConfigManager.load();

        CommandClientBuilder cb = new CommandClientBuilder().
                setOwnerId(config.getOwnerID()).
                setHelpWord("help").
                setPrefix(".").
                setEmojis("\u2705", "\u26A0", "\u26D4").
                addCommands(
                        new WarnCmd(),
                        new ViewWarningCmd(),
                        new ViewWarningsCmd(),

                        new ModClearCmd()
                );

        UsersDb usersDb = config.getUsersDb();

        DbManager.init(usersDb.getIp(), usersDb.getDb(), usersDb.getUser(), usersDb.getPass());

        CommandClient client = cb.build();

        try {
            new JDABuilder(AccountType.BOT)
                    .setToken("")
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setActivity(Activity.playing("loading..."))
                    .addEventListeners(client,
                            new ModListener()
                    ).
                    build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
