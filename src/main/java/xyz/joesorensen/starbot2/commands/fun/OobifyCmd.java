package xyz.joesorensen.starbot2.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.FunCommand;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class OobifyCmd extends FunCommand {

    public OobifyCmd() {
        this.name = "oobify";
        this.help = "replace all vowels in arguments with oob";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();

        if (args.isBlank() || args.isEmpty())
            event.replyError("You must provide a statement!");
        else {
            args = args.replaceAll("o", "oob");
            args = args.replaceAll("O", "Oob");

            args = args.replaceAll("a", "oob");
            args = args.replaceAll("e", "oob");
            args = args.replaceAll("i", "oob");
            args = args.replaceAll("u", "oob");

            args = args.replaceAll("A", "Oob");
            args = args.replaceAll("E", "Oob");
            args = args.replaceAll("I", "Oob");
            args = args.replaceAll("U", "Oob");

            event.reply(args);

        }
    }
}
