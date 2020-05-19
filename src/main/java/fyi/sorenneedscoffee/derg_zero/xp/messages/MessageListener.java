package fyi.sorenneedscoffee.derg_zero.xp.messages;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fyi.sorenneedscoffee.derg_zero.xp.data.models.CacheKey;
import fyi.sorenneedscoffee.xputil.events.xp.LevelUpEvent;
import fyi.sorenneedscoffee.xputil.listener.XPListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class MessageListener extends XPListener {
    private JDA jda;

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onLevelUp(LevelUpEvent event) {
        Guild guild = jda.getGuildById(event.getGroupId());
        User user = jda.getUserById(event.getUserId());

        String name = guild.getMember(user).getEffectiveName();

        TextChannel channel = MessageListenerAdapter.cache.get(new CacheKey(user, guild));


        try {
            Webhook hook = channel.createWebhook(name).setAvatar(Icon.from(new URL(user.getAvatarUrl()).openStream())).complete();

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
            float[] rgb;

            embed.setTitle(new WebhookEmbed.EmbedTitle("Level up!", null));
            embed.setDescription("Congrats to " + name + " for reaching level " + event.getNewLevel() + "!");
            rgb = Color.RGBtoHSB(204, 255, 94, null);
            embed.setColor(Color.getHSBColor(rgb[0], rgb[1], rgb[2]).getRGB());

            WebhookClient client = WebhookClient.withUrl(hook.getUrl());
            client.send(embed.build())
                    .thenAccept((message) -> {
                        hook.delete().queue();
                        client.close();
                    });
        } catch (IOException ignore) {
        }
    }
}
