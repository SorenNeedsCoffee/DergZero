package fyi.sorenneedscoffee.garbagecan.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class InviteCmd extends Command {

    public InviteCmd() {
        super();
        this.name = "invite";
        this.help = "Gives you our invite link";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("If you want to invite people to our server, send them this link: https://draconium.productions/discord");
    }
}
