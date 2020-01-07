package xyz.joesorensen.starbot2.listeners.chains;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Script extends ListenerAdapter {
    private ScriptManager man;
    private boolean toPurge = false;

    @Override
    public void onReady(ReadyEvent event) {
        man = new ScriptManager();
        if (!man.isActive()) {
            toPurge = true;
            man.newScript();
        }

        event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord()).queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (!event.getChannel().getId().equals("663544151547314255"))
            return;

        if (man.checkMsg(event.getMessage().getContentDisplay())) {
            if (man.next()) {
                man.newScript();
                purgeMsgs(event.getChannel(), (messages) -> event.getChannel().purgeMessages(messages));
            }
            event.getJDA().getGuildChannelById("663544151547314255").getManager().setTopic("Title: " + man.title() + " | Next Word: " + man.nextWord()).queue();
        } else {
            if (toPurge) {
                purgeMsgs(event.getChannel(), (messages) -> event.getChannel().purgeMessages(messages));
                toPurge = false;
            } else
                event.getMessage().delete().queue();
        }
    }

    private void purgeMsgs(MessageChannel channel, Consumer<List<Message>> callback) {
        List<Message> messages = new ArrayList<>(10000);
        channel.getIterableHistory().cache(false).forEachAsync((message) ->
        {
            messages.add(message);
            return messages.size() < 10000;
        }).thenRun(() -> callback.accept(messages));
    }
}