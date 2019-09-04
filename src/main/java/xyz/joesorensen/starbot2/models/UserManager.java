package xyz.joesorensen.starbot2.models;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    static List<User> users = new ArrayList<>();

    public static void addUser(String id) {
        users.add(new User(id));
    }

    public static User getUser(String id) {
        for(User user : users) {
            if(user.getId().equals(id))
                return user;
        }
        return null;
    }

    public static void updateUser(User user) {
        if(user.getId().equals("") || user.getId() == null)
            throw new IllegalArgumentException("Id of user cannot be empty or null.");

        int index = users.indexOf(user);
        users.set(index, user);
    }

    public static void saveFile() {
        Logger log = LoggerFactory.getLogger("saveMembersToJSON");
        JSONArray data = new JSONArray(users);
        JSONObject file = new JSONObject();
        file.put("data", data);

        try {
            Files.write(Paths.get("members.json"), data.toString().getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void loadFile() {
        Logger log = LoggerFactory.getLogger("loadMembersFromFile");

        Object raw = null;
        try {
            raw = new JSONParser().parse(new FileReader("members.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: members file not found. Please ensure that the members file exsists, is in the same directory as the jar, and is called members.json");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        JSONArray members = (JSONArray) raw;

        for(Object user : members) {
            users.add((User) user);
        }
    }
}
