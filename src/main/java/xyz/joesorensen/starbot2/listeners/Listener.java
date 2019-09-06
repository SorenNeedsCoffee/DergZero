package xyz.joesorensen.starbot2.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.joesorensen.starbot2.models.LvlRoleIDs;
import xyz.joesorensen.starbot2.models.User;
import xyz.joesorensen.starbot2.models.UserManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.*;

public class Listener extends ListenerAdapter {
    private Logger log;
    private JDA jda;
    private String id;
    private String prefix;
    private List<String> cooldown = new ArrayList<>();
    private Timer timer = new Timer();

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
        File membersFile = new File("members.json");
        if (membersFile.exists()) {
            UserManager.loadFile();
        }
        for (Guild guild : guilds) {
            List<Member> members = guild.getMembers();
            for (Member member : members) {
                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        member.getRoles().contains(guild.getRoleById(id))))
                    guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(id))).queue();

                if (!member.getRoles().contains(guild.getRoleById(LvlRoleIDs.LVL1.getId())) &&
                        !(member.getUser().isBot() || member.getUser().isFake()) &&
                        (Objects.requireNonNull(UserManager.getUser(member.getId())).getLvl() < 5))
                    Objects.requireNonNull(event.getJDA().getGuildById("442552203694047232")).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(LvlRoleIDs.LVL1.getId()))).queue();

                if (!(member.getUser().isBot() || member.getUser().isFake() ||
                        UserManager.getUser(member.getId()) != null))
                    UserManager.addUser(member.getId());
            }

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    UserManager.saveFile();
                }
            }, 3000, 30000);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(id))).queue();
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(LvlRoleIDs.LVL1.getId()))).queue();
            UserManager.addUser(event.getMember().getId());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // do NOT remove this
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (cooldown.indexOf(event.getAuthor().getId()) == -1 || !event.getChannel().getId().equals("442556155856814080") || !(event.getMessage().getContentDisplay().startsWith("!>"))) {
            User update = UserManager.getUser(event.getAuthor().getId());
            update.addXp(Math.sqrt(event.getMessage().getContentDisplay().replaceAll(" ", "").length()));
            if (update.getXp() >= update.getLvl() * 250) {
                onLvlUp(event, update);
            }
            UserManager.updateUser(update);
            cooldown.add(event.getAuthor().getId());
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cooldown.remove(event.getAuthor().getId());
                }
            }, 5000);
        }

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
            String imgurl;
            while (true) {
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
                event.getChannel().sendMessage(imgurl).queue();
            } catch (Exception e) {
                event.getChannel().sendMessage("Sorry, there was an issue getting the freshest meme!").queue();
            }
        }
    }

    void onLive(Message embed) {
        log.info("live!");
        jda.getPresence().setActivity(Activity.streaming("JoeSorensen is live!", "https://twitch.tv/joesorensen"));
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("442552203694047232")).getTextChannelById("442556155856814080")).sendMessage("@everyone").queue();
        Objects.requireNonNull(Objects.requireNonNull(jda.getGuildById("442552203694047232")).getTextChannelById("442556155856814080")).sendMessage(embed).queue();
    }

    void onOffline() {
        log.info("offline");
        jda.getPresence().setActivity(Activity.playing("On Soren's server | " + prefix + "help for help"));
    }

    void onLvlUp(GuildMessageReceivedEvent event, User update) {
        update.setLvl(update.getLvl() + 1);

        EmbedBuilder embed = new EmbedBuilder();
        float[] rgb;

        embed.setAuthor("Level Up!", null, event.getAuthor().getAvatarUrl());
        embed.setDescription("Congrats to " + event.getAuthor().getName() + " for reaching level " + update.getLvl() + "!");
        rgb = Color.RGBtoHSB(204, 255, 94, null);
        embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]));

        event.getChannel().sendMessage(embed.build()).queue();
        UserManager.saveFile();

        switch (update.getLvl()) {
            case 5:
                replaceRole(event.getMember(), LvlRoleIDs.LVL1.getId(), LvlRoleIDs.LVL5.getId());
                break;
            case 10:
                replaceRole(event.getMember(), LvlRoleIDs.LVL5.getId(), LvlRoleIDs.LVL10.getId());
                break;
            case 15:
                replaceRole(event.getMember(), LvlRoleIDs.LVL10.getId(), LvlRoleIDs.LVL15.getId());
                break;
            case 20:
                replaceRole(event.getMember(), LvlRoleIDs.LVL15.getId(), LvlRoleIDs.LVL20.getId());
                break;
            case 25:
                replaceRole(event.getMember(), LvlRoleIDs.LVL20.getId(), LvlRoleIDs.LVL25.getId());
                break;
            case 30:
                replaceRole(event.getMember(), LvlRoleIDs.LVL25.getId(), LvlRoleIDs.LVL30.getId());
                break;
            case 35:
                replaceRole(event.getMember(), LvlRoleIDs.LVL30.getId(), LvlRoleIDs.LVL35.getId());
                break;
            case 40:
                replaceRole(event.getMember(), LvlRoleIDs.LVL35.getId(), LvlRoleIDs.LVL40.getId());
                break;
            case 45:
                replaceRole(event.getMember(), LvlRoleIDs.LVL40.getId(), LvlRoleIDs.LVL45.getId());
                break;
            case 50:
                replaceRole(event.getMember(), LvlRoleIDs.LVL45.getId(), LvlRoleIDs.LVL50.getId());
        }
    }

    private void replaceRole(Member member, String regex, String replace) {
        Objects.requireNonNull(jda.getGuildById("442552203694047232")).removeRoleFromMember(member, Objects.requireNonNull(jda.getRoleById(regex))).queue();
        Objects.requireNonNull(jda.getGuildById("442552203694047232")).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(replace))).queue();
    }
}
