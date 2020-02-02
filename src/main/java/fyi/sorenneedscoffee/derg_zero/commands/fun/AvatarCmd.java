package fyi.sorenneedscoffee.derg_zero.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import fyi.sorenneedscoffee.derg_zero.commands.FunCommand;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class AvatarCmd extends FunCommand {

    public AvatarCmd() {
        this.name = "avatar";
        this.help = "gets the avatar of a user";
        this.arguments = "User Ping (leave blank for self)";
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();

        if (args.equals(""))
            event.reply(event.getAuthor().getAvatarUrl());

        Member target = event.getMessage().getMentionedMembers().get(0);

        event.reply(target.getUser().getAvatarUrl());
    }
}
