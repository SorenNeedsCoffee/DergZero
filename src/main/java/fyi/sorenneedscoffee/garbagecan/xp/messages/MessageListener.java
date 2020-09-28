package fyi.sorenneedscoffee.garbagecan.xp.messages;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.xp.data.models.CacheKey;
import fyi.sorenneedscoffee.xputil.events.xp.LevelUpEvent;
import fyi.sorenneedscoffee.xputil.listener.XPListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class MessageListener extends XPListener {
    private final JDA jda = Main.jda;

    @Override
    public void onLevelUp(LevelUpEvent event) {
        try {
            Guild guild = jda.getGuildById(event.getGroupId());
            User user = jda.getUserById(event.getUserId());

            String name = guild.retrieveMember(user).complete().getEffectiveName();

            TextChannel channel = MessageListenerAdapter.cache.get(new CacheKey(user, guild));


            try {
                List<Webhook> hooks = new ArrayList<>(channel.retrieveWebhooks().complete());
                if(hooks.size() == 10) {
                    RestAction<Void> action = hooks.get(0).delete();
                    hooks.remove(0);
                    for (Webhook temp : hooks) {
                        action = action.flatMap((success) -> temp.delete());
                    }
                    action.queue();
                }

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
        } catch (Exception e) {
            LoggerFactory.getLogger(MessageListener.class).error(ExceptionUtils.getStackTrace(e));
        }
    }
}
