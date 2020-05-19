package fyi.sorenneedscoffee.derg_zero.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class InviteCmd extends Command {

    public InviteCmd() {
        this.name = "invite";
        this.help = "Gives you our invite link";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("If you want to invite people to our server, send them this link: https://draconium.productions/discord");
    }
}
