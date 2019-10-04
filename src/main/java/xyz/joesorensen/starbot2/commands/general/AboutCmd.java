package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class AboutCmd extends Command {

    public AboutCmd() {
        this.name = "about";
        this.help = "shows info about the bot";
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}
