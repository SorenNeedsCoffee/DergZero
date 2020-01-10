package xyz.joesorensen.xputil.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
class DbManager {
    private final Logger log = LoggerFactory.getLogger("DbManager");
    private final String url;
    private final String table;

    DbManager(String ip, String db, String table, String user, String pass) throws SQLException {
        Connection connect = null;

        url = "jdbc:mysql://" + ip + "/" + db + "?"
                + "user=" + user + "&password=" + pass;
        this.table = table;
        log.info("Validating connection to " + db + " at " + ip + "...");

        try {
            connect = DriverManager.getConnection(url);
            if (connect.isValid(5))
                log.info("Success.");
            else
                log.error("Failed. Please check your configuration");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (connect != null)
                connect.close();
        }
    }

    void addUser(String id, int lvl, double xp) {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            statement.executeUpdate("INSERT INTO " + table + " VALUES ('" + id + "', " + xp + ", " + lvl + ")");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    void delUser(String id) {

        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            statement.executeUpdate("DELETE FROM " + table + " WHERE id = '" + id + "'");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    void updateUser(User user) {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            statement.executeUpdate("UPDATE " + table + " " +
                    "SET " +
                    "xp = " + user.getXp() + ", " +
                    "lvl = " + user.getLvl() +
                    " WHERE id = '" + user.getId() + "'");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    User getUser(String id) {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + table + " WHERE id = '" + id + "'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            return new User(rs.getString("id"), rs.getDouble("xp"), rs.getInt("lvl"));
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    List<User> getUsers() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + table);
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            List<User> users = new ArrayList<>();
            while (!rs.isAfterLast()) {
                users.add(new User(rs.getString("id"), rs.getDouble("xp"), rs.getInt("lvl")));
                if(!rs.next())
                    throw new SQLException("Returned result is null.");
            }
            return users;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
