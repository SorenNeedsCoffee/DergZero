package xyz.joesorensen.xputil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
class DbManager {
    private Connection connect;
    private String table;
    private String url;

    DbManager(String ip, String db, String table, String user, String pass) throws Exception {

        url = "jdbc:mysql://" + ip + "/" + db + "?"
                + "user=" + user + "&password=" + pass;
        connect = DriverManager.getConnection(url);

        this.table = table;
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
