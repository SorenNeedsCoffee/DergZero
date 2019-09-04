package xyz.joesorensen.starbot2.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import xyz.joesorensen.starbot2.StarBot2;
import xyz.joesorensen.starbot2.commands.OwnerCommand;

public class ShutdownCmd extends OwnerCommand {
    public ShutdownCmd() {
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.aliases = new String[]{"off"};
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendMessage("\u26A0 Shutting down...").complete();
        StarBot2.shutdown();
    }
}
