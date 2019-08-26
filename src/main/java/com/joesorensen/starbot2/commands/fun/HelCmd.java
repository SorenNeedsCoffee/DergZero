package com.joesorensen.starbot2.commands.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class HelCmd extends Command {

    public HelCmd() {
        this.name = "hel";
        this.help = "a \"special\" help command";
        this.aliases = new String[]{
                "halp",
                "elp",
                "hlp"
        };
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("hELP! ive fAlLeN and I ***CANT*** get uP!");
    }
}
