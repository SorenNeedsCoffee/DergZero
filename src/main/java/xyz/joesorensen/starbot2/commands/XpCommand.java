package xyz.joesorensen.starbot2.commands;

import com.jagrosh.jdautilities.command.Command;

/**
 * -=StarBot2=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public abstract class XpCommand extends Command {

    protected XpCommand() {

        this.category = new Category("User");
        this.guildOnly = true;

    }

}
