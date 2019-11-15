package xyz.joesorensen.starbot2.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HelpCmd implements Consumer<CommandEvent> {
    @Override
    public void accept(CommandEvent event) {
        List<Command> commands = event.getClient().getCommands();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Commands for StarBot2");
        builder.setColor(Color.decode("#fc3003"));
        StringBuilder list = new StringBuilder();
        for (Command command : commands) {
            list.append("```ini\n");
            list.append(command.getName());
            if (command.getAliases().length > 0 && !command.getName().equals("hel"))
                list.append(" " + Arrays.toString(command.getAliases()));
            list.append(" | ")
                    .append(command.getHelp());

            if (!(command.getArguments() == null || command.getArguments().isBlank() || command.getArguments().isEmpty()))
                list.append(" | <" + command.getArguments() + ">");

            list.append("```");
        }
        builder.addField("", list.toString(), true);


        if (event.getChannelType().isGuild()) {
            event.replyInDm(builder.build());
        } else {
            event.reply(builder.build());
        }
    }

}
