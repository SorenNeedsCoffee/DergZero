package xyz.joesorensen.starbot2.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class FakeCmd extends Command {

    public FakeCmd() {
        this.name = "iamtherealstarbotyoufake";
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        switch (event.getMember().getUser().getId()) {
            case "312736834415493123":
                event.replyError("no u.");
                break;
            default:
                event.replyError("You're not even StarBot, you fake.");
                break;
        }
    }
}
