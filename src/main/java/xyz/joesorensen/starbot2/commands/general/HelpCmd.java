package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;
import java.util.function.Consumer;

public class HelpCmd implements Consumer<CommandEvent> {
    @Override
    public void accept(CommandEvent event) {
        List<Command> commands = event.getClient().getCommands();
    }
}
