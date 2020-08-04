package fyi.sorenneedscoffee.garbagecan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger("Config");

    public static Config load() {
        Config config = null;

        try {
            String token = System.getenv("TOKEN");
            String defaultRoleId = System.getenv("DEFAULT_ROLE_ID");
            String prefix = System.getenv("PREFIX");
            String dbUrl = System.getenv("JAWSDB_MARIA_URL");
            String helpWord = System.getenv("HELP_WORD");

            config = new Config(token, defaultRoleId, prefix, dbUrl, helpWord);
        } catch (NullPointerException e) {
            log.error("One or more of the required environment variables are not present.");
        }

        return config;
    }
}
