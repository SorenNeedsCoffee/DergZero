package xyz.joesorensen.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class OwnerCommand extends Command {
    protected OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}
