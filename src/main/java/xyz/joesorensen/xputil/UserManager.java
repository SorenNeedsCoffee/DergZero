package xyz.joesorensen.xputil;

import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class UserManager {
    private static List<User> users = new ArrayList<>();
    private static DbManager db;

    static void addUser(String id) {
        try {
            db.addUser(id, 0, 0.0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void removeUser(String id) {
        try {
            db.delUser(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void initDb(String url, String user, String pass) throws Exception {
        db = new DbManager(url, "starbot2", "users", user, pass);
    }

    public static void pruneUsers(Guild guild) {
        Logger log = LoggerFactory.getLogger("PruneMembers");
        List<String> toRemove = new ArrayList<>();
        for (User user : users) {
            if (guild.getMemberById(user.getId()) == null) {
                log.info("Removing user with ID " + user.getId());
                toRemove.add(user.getId());
            }
        }
        for (String id : toRemove) {
            removeUser(id);
            log.info("Removed user with ID " + id);
        }
    }

    public static User getUser(String id) {
        try {
            return db.getUser(id);
        } catch (SQLException e) {
            return null;
        }
    }

    public static List<User> getUsers() {
        try {
            return db.getUsers();
        } catch (SQLException e) {
            return null;
        }
    }

    public static void updateUser(User user) {
        if (user.getId().equals("") || user.getId() == null)
            throw new IllegalArgumentException("Id of user cannot be empty or null.");

        try {
            db.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile() {
        Logger log = LoggerFactory.getLogger("SaveMembersToJSON");
        JSONArray data = createJsonArrayFromList();
        JSONObject file = new JSONObject();
        file.put("data", data);

        try {
            Files.write(Paths.get("members.json"), file.toString().getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    static void loadFile() {
        Logger log = LoggerFactory.getLogger("loadMembersFromFile");

        JSONObject raw = null;
        try {
            raw = (JSONObject) new JSONParser().parse(new FileReader("members.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: members file not found. Please ensure that the members file exists, is in the same directory as the jar, and is called members.json");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        JSONArray members = (JSONArray) raw.get("data");

        for (Object user : members) {
            JSONObject obj = (JSONObject) user;
            users.add(new User((String) obj.get("id"), (double) obj.get("xp"), ((Long) obj.get("lvl")).intValue()));

        }

        for(User user : users) {
            try {
                db.addUser(user.getId(), user.getLvl(), user.getXp());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static JSONArray createJsonArrayFromList() {
        JSONArray result = new JSONArray();
        for (User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("xp", user.getXp());
            obj.put("lvl", user.getLvl());
            result.add(obj);
        }
        return result;
    }
}
