package fyi.sorenneedscoffee.garbagecan.xp.data.models;

import com.google.common.base.Objects;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class LevelRole {
    private final int level;
    private final String roleID;

    public LevelRole(int level, String roleID) {
        this.level = level;
        this.roleID = roleID;
    }

    public int getLevel() {
        return level;
    }

    public String getRoleID() {
        return roleID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(level, roleID);
    }
}
