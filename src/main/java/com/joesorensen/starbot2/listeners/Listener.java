package com.joesorensen.starbot2.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Listener extends ListenerAdapter {
    private Logger log;
    private JDA jda;

    public Listener() {
        this.log = LoggerFactory.getLogger("Main");
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Ready!");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // do NOT remove this
        if(event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if(event.getMessage().getContentDisplay().toLowerCase().contains("yo, can i have some memes?"))
            event.getChannel().sendMessage("dude not out in the open!").queue();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if(event.getMessage().getAuthor().isBot())
            return;
        if(event.getMessage().getContentDisplay().toLowerCase().contains("yo, can i have some memes?")) {
            event.getChannel().sendMessage("Sadly, this hasn't been implemented yet. Check back later!").queue();
            String imgurl = "";
            while(true) {
                try {
                    String url = "https://www.reddit.com/r/memes/top/.json?count=1&t=all";
                    URL obj;

                    obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    con.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject result = (JSONObject) new JSONParser().parse(response.toString());
                    JSONObject data = (JSONObject) result.get("data");
                    JSONArray children = (JSONArray) data.get("children");
                    JSONObject post = (JSONObject) children.get(0);
                    JSONObject postdata = (JSONObject) post.get("data");
                    imgurl = (String) postdata.get("url");
                    break;
                } catch (IOException | ParseException ignore) {}
            }
            System.out.println(imgurl);
        }
    }

    void onLive() {
        log.info("live!");
        jda.getPresence().setActivity(Activity.streaming("JoeSorensen is live!", "https://twitch.tv/joesorensen"));
    }

    void onOffline() {
        log.info("offline");
        jda.getPresence().setActivity(Activity.playing("On Soren's server | >help for help"));
    }
}
