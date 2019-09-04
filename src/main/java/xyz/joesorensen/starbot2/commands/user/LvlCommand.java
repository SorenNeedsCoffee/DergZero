package xyz.joesorensen.starbot2.commands.user;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import xyz.joesorensen.starbot2.commands.UserCommand;
import xyz.joesorensen.starbot2.models.User;
import xyz.joesorensen.starbot2.models.UserManager;

public class LvlCommand extends UserCommand {

    public LvlCommand() {
        this.name = "lvl";
        this.help = "display user xp and level.";
        this.aliases = new String[]{
                "rank"
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        User user = UserManager.getUser(event.getAuthor().getId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("User Level");
        embed.setDescription("Level for "+event.getAuthor().getName());
        embed.addField("Level", Integer.toString(user.getLvl()), true);
        embed.addField("XP", Double.toString(user.getXp()), false);
    }
}
