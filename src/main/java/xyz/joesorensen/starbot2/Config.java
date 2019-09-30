package xyz.joesorensen.starbot2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    private String token;
    private String ownerID;
    private String defaultRoleID;
    private String prefix;
    private String clientID;
    private static Yaml yaml = new Yaml();
    private static Logger log = LoggerFactory.getLogger("Config");;


    static Config load() {
        Config config = null;
        try {
            InputStream in = Files.newInputStream(Paths.get("config.yml"));
             config = yaml.loadAs(in, Config.class);
        } catch(IOException e) {
            log.error("Config file not found. Expected: config.yml. Is the file named correctly?");
            System.exit(1);
        }

        return config;
    }

    String getClientID() {
        return clientID;
    }

    String getToken() {
        return token;
    }

    String getOwnerID() {
        return ownerID;
    }

    String getDefaultRoleID() {
        return defaultRoleID;
    }

    String getPrefix() {
        return prefix;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public void setDefaultRoleID(String defaultRoleID) {
        this.defaultRoleID = defaultRoleID;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}
