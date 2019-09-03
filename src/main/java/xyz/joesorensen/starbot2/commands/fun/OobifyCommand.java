package xyz.joesorensen.starbot2.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.commands.FunCommand;

public class OobifyCommand extends FunCommand {

    public OobifyCommand() {
        this.name = "oobify";
        this.help = "replace all vowels in arguments with oob";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();

        if(args.equals(""))
            event.replyError("You must provide a statement!");

        String result = args;
        result = result.replaceAll("o", "oob");
        result = result.replaceAll("a", "oob");
        result = result.replaceAll("e", "oob");
        result = result.replaceAll("i", "oob");
        result = result.replaceAll("u", "oob");

        event.reply(result);
    }
}
