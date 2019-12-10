package xyz.joesorensen.xputil.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
class DbManager {
    private Connection connect;
    private final Logger log = LoggerFactory.getLogger("DbManager");
    private String table;
    private final String url;

    DbManager(String ip, String db, String user, String pass) throws Exception {

        url = "jdbc:mysql://" + ip + "/" + db + "?"
                + "user=" + user + "&password=" + pass;
        log.info("Establishing initial connection to " + db + " at " + ip + "...");
        connect = DriverManager.getConnection(url);

        this.table = "users";

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log.info("Reestablishing DB connection...");
                try {
                    connect.close();
                } catch (SQLException e) {
                    connect = null;
                }
                try {
                    connect = DriverManager.getConnection(url);
                } catch (SQLException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
                log.info("Success");
            }
        }, 10800000, 10800000);
        log.info("Success.");
    }

    void addUser(String id, int lvl, double xp) throws SQLException {
        Statement statement = connect.createStatement();

        statement.executeUpdate("INSERT INTO " + table + " VALUES ('" + id + "', " + xp + ", " + lvl + ")");
    }

    void delUser(String id) throws SQLException {
        Statement statement = connect.createStatement();

        statement.executeUpdate("DELETE FROM " + table + " WHERE id = '" + id + "'");
    }

    void updateUser(User user) throws SQLException {
        Statement statement = connect.createStatement();

        statement.executeUpdate("UPDATE " + table + " " +
                "SET " +
                "xp = " + user.getXp() + ", " +
                "lvl = " + user.getLvl() +
                " WHERE id = '" + user.getId() + "'");
    }

    User getUser(String id) throws SQLException {
        Statement statement = connect.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM " + table + " WHERE id = '" + id + "'");
        rs.next();

        return new User(rs.getString("id"), rs.getDouble("xp"), rs.getInt("lvl"));
    }

    List<User> getUsers() throws SQLException {
        Statement statement = connect.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM " + table);
        List<User> users = new ArrayList<>();
        rs.next();
        while (!rs.isAfterLast()) {
            users.add(new User(rs.getString("id"), rs.getDouble("xp"), rs.getInt("lvl")));
            rs.next();
        }
        return users;
    }
}
