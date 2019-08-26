package com.joesorensen.starbot2.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.joesorensen.starbot2.StarBot2;
import com.joesorensen.starbot2.commands.OwnerCommand;

public class ShutdownCmd extends OwnerCommand {
    public ShutdownCmd() {
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyWarning("Shutting down...");
        StarBot2.shutdown();
    }
}
