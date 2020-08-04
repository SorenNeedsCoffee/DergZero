package fyi.sorenneedscoffee.garbagecan.config;

import org.apache.commons.lang3.Validate;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public final class Config {
    public final String token, defaultRoleID, prefix, dbUrl, helpWord;

    public Config(String token, String defaultRoleID, String prefix, String dbUrl, String helpWord) throws NullPointerException {
        this.dbUrl = dbUrl;
        this.helpWord = helpWord;
        Validate.notNull(token);
        Validate.notNull(defaultRoleID);
        Validate.notNull(prefix);
        Validate.notNull(dbUrl);

        this.token = token;
        this.defaultRoleID = defaultRoleID;
        this.prefix = prefix;
    }
}
