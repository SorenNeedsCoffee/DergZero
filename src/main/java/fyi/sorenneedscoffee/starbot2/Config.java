package fyi.sorenneedscoffee.starbot2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class Config {
    private static final Yaml yaml = new Yaml();
    private static final Logger log = LoggerFactory.getLogger("Config");
    private String token;
    private String ownerID;
    private String defaultRoleID;
    private String prefix;
    private String clientID;
    private String dbUrl;
    private String dbName;
    private String dbUser;
    private String dbPass;

    public static Config load() {
        Config config = null;
        try {
            InputStream in = Files.newInputStream(Paths.get("config.yml"));
            config = yaml.loadAs(in, Config.class);
        } catch (IOException e) {
            log.error("Config file not found. Expected: config.yml. Is the file named correctly?");
            System.exit(1);
        }

        return config;
    }

    String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    String getDefaultRoleID() {
        return defaultRoleID;
    }

    public void setDefaultRoleID(String defaultRoleID) {
        this.defaultRoleID = defaultRoleID;
    }

    String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
