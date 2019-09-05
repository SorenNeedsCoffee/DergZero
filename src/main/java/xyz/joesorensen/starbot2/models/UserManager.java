package xyz.joesorensen.starbot2.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        JSONArray data = createJsonArrayFromList();
        JSONObject file = new JSONObject();
        file.put("data", data);

        try {
            Files.write(Paths.get("members.json"), file.toString().getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void loadFile() {
        Logger log = LoggerFactory.getLogger("loadMembersFromFile");

        JSONObject raw = null;
        try {
            raw = (JSONObject) new JSONParser().parse(new FileReader("members.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: members file not found. Please ensure that the members file exsists, is in the same directory as the jar, and is called members.json");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        JSONArray members = (JSONArray) raw.get("data");

        for(Object user : members) {
            JSONObject obj = (JSONObject) user;
            users.add(new User((String) obj.get("id"), (double) obj.get("xp"), ((Long) obj.get("lvl")).intValue()));

        }
    }

    private static JSONArray createJsonArrayFromList() {
        JSONArray result = new JSONArray();
        for(User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("xp", user.getXp());
            obj.put("lvl", user.getLvl());
            result.add(obj);
        }
        return result;
    }
}
