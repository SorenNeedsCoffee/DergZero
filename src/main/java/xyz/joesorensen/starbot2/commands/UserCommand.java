package xyz.joesorensen.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class UserCommand extends Command {

    protected UserCommand() {

        this.category = new Category("User");
        this.guildOnly = true;

    }

}
