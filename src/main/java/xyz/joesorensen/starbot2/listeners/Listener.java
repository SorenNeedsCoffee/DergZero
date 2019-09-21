package xyz.joesorensen.starbot2.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
import java.util.List;
import java.util.Objects;

public class Listener extends ListenerAdapter {
    private Logger log;
    private JDA jda;
    private String id;
    private String prefix;

    public Listener() {
        this.log = LoggerFactory.getLogger("Main");
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void setRoleID(String id) {
        this.id = id;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Ready!");
        List<Guild> guilds = event.getJDA().getGuilds();
        for (Guild guild : guilds) {
            List<Member> members = guild.getMembers();
            for (Member member : members) {
                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        member.getRoles().contains(guild.getRoleById(id))))
                    guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(id))).queue();
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(id))).queue();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // do NOT remove this
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (event.getChannel() == jda.getTextChannelById("506503200866697226"))
            if (!event.getMessage().getContentDisplay().equalsIgnoreCase("hi"))
                event.getMessage().delete().queue();

        if (event.getMessage().getContentDisplay().toLowerCase().contains("yo, can i have some memes?"))
            event.getChannel().sendMessage("dude not out in the open!").queue();
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot())
            return;
        if (event.getMessage().getContentDisplay().toLowerCase().contains("yo, can i have some memes?")) {
            event.getChannel().sendTyping().queue();
            String imgurl = null;
            while (imgurl == null) {
                try {
                    String url = "https://www.reddit.com/r/memes/best/.json?count=1&t=all";
                    URL obj;

                    obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    con.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

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
                } catch (IOException | ParseException ignore) {
                }
            }
            try {
                //event.getChannel().sendMessage(imgurl).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage("Sorry, there was an issue getting the freshest meme!").queue();
            }
        }
    }

    public void onLive(Message embed) {
        log.info("live!");
        jda.getPresence().setActivity(Activity.streaming("JoeSorensen is live!", "https://twitch.tv/joesorensen"));
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("442552203694047232")).getTextChannelById("442556155856814080")).sendMessage("@everyone").queue();
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("442552203694047232")).getTextChannelById("442556155856814080")).sendMessage(embed).queue();
    }

    public void onOffline() {
        log.info("offline");
        jda.getPresence().setActivity(Activity.playing("On Soren's server | " + prefix + "help for help"));
    }
}
