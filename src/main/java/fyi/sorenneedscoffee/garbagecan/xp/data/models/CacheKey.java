package fyi.sorenneedscoffee.garbagecan.xp.data.models;

import com.google.common.base.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class CacheKey {
    private final User user;
    private final Guild guild;

    public CacheKey(User user, Guild guild) {
        this.user = user;
        this.guild = guild;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CacheKey) {
            CacheKey c = (CacheKey) obj;

            return Objects.equal(c.getGuild(), guild) && Objects.equal(c.getUser(), user);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, guild);
    }
}
