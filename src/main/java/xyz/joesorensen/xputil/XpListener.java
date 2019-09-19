package xyz.joesorensen.xputil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class XpListener extends ListenerAdapter {
    private Logger log;
    private JDA jda;
    private List<String> cooldown = new ArrayList<>();
    private Timer timer = new Timer();

    public XpListener() {
        this.log = LoggerFactory.getLogger("XpManager");
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

   @Override
   public void onReady(ReadyEvent event) {
        log.info("Getting things ready...");
       List<Guild> guilds = event.getJDA().getGuilds();
       File membersFile = new File("members.json");
       if (membersFile.exists()) {
           UserManager.loadFile();
       }
       for (Guild guild : guilds) {
           List<Member> members = guild.getMembers();
           for (Member member : members) {
               if (!(member.getUser().isBot() || member.getUser().isFake() ||
                       UserManager.getUser(member.getId()) != null))
                   UserManager.addUser(member.getId());

               if (!member.getRoles().contains(guild.getRoleById(LvlRoleIDs.LVL1.getId())) &&
                       !(member.getUser().isBot() || member.getUser().isFake()) &&
                       (Objects.requireNonNull(UserManager.getUser(member.getId())).getLvl() < 5))
                   Objects.requireNonNull(event.getJDA().getGuildById("442552203694047232")).addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(LvlRoleIDs.LVL1.getId()))).queue();
           }

           timer.scheduleAtFixedRate(new TimerTask() {
               @Override
               public void run() {
                   UserManager.saveFile();
               }
           }, 3000, 30000);
       }
       Runtime.getRuntime().addShutdownHook(new Thread() {

           @Override
           public void run() {
               log.info("Saving members.json before shutdown...");
               UserManager.saveFile();
           }

       });
       log.info("XPManager Version 0.1 ready");
   }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(event.getUser().isBot() || event.getUser().isFake())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(LvlRoleIDs.LVL1.getId()))).queue();
            UserManager.addUser(event.getMember().getId());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (cooldown.indexOf(event.getAuthor().getId()) == -1 || !event.getChannel().getId().equals("442556155856814080")) {
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
