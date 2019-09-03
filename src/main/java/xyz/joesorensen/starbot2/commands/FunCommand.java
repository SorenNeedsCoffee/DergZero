package xyz.joesorensen.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class FunCommand extends Command {

    protected FunCommand() {

        this.category = new Category("Fun");

    }

}
