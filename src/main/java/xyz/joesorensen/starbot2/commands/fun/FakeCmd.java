package xyz.joesorensen.starbot2.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class FakeCmd extends Command {

    public FakeCmd() {
        this.name = "iamtherealstarbotyoufake";
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyError("no u.");
    }
}