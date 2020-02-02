package fyi.sorenneedscoffee.derg_zero.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class InviteCmd extends Command {

    public InviteCmd() {
        this.name = "invite";
        this.help = "Gives you our invite link.";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("If you want to invite people to our server, send them this link: https://joesorensen.xyz/discord");
    }
}
