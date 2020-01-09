package xyz.joesorensen.starbot2.listeners.chains;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.sql.*;
import java.util.Random;

public class ScriptManager {
    private final Logger log = LoggerFactory.getLogger("ScriptManager");
    Random random = new Random();
    private String first;
    private final String url;
    private final String mTable;
    private final String sTable;

    ScriptManager() {
        url = "jdbc:mysql://" + "192.168.86.74" + "/" + "s4_starbot2" + "?"
                + "user=" + "u4_FTxSjk9AiF" + "&password=" + "zQeV9UsjANUrk4B8RzmrZmLF";
        this.mTable = "script_meta";
        this.sTable = "script";
    }

    void newScript() {
        Logger log = LoggerFactory.getLogger("loadScriptFromFile");
        int index = 0;
        String title;
        String[] script;

        JSONObject meta = null;
        try {
            meta = (JSONObject) new JSONParser().parse(new FileReader("scripts/meta.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: script meta file not found. Please ensure that the members file exists, is in the scripts directory, and is called meta.json");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        int available = Math.toIntExact((Long) meta.get("available"));
        int choice = new Random().nextInt(available);
        while (choice == Math.toIntExact((Long) meta.get("previous")))
            choice = random.nextInt(available);
        meta.put("previous", choice);

        try {
            Files.write(Paths.get("scripts/meta.json"), meta.toString().getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        JSONObject raw = null;
        try {
            raw = (JSONObject) new JSONParser().parse(new FileReader("scripts/" + choice + ".json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: script file not found.");
            System.exit(1);
        } catch (IOException | ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }

        String test = (String) raw.get("script");
        script = test.replaceAll("([^A-Za-z0-9\\s\\-':()])+", "").split(" ");

        title = (String) raw.get("title");

        first = script[0];

        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            statement.executeUpdate("DELETE FROM " + mTable);
            statement.executeUpdate("DELETE FROM " + sTable);

            statement.executeUpdate("INSERT INTO " + mTable + " VALUES('title', '" + title + "')");
            statement.executeUpdate("INSERT INTO " + mTable + " VALUES('index', '" + index + "')");
            statement.executeUpdate("INSERT INTO " + mTable + " VALUES('length', '" + script.length + "')");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        Thread th = new Thread(() -> {
            try (Connection connect = DriverManager.getConnection(url)) {
                Statement statement = connect.createStatement();

                for (int i = 0; i < script.length; i++) {
                    statement.addBatch("INSERT INTO " + sTable + " VALUES('" + i + "', '" + script[i] + "')");
                }
                int[] records = statement.executeBatch();
                log.info("Script loaded with " + records.length + " records");
            } catch (SQLException e) {
                log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
        th.start();
    }

    boolean isActive() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT 1 FROM " + mTable + " WHERE name = 'index'");

            if (rs.next() && rs.getRow() == 1)
                return true;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    boolean checkMsg(String msg) {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'index'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            int index = rs.getInt("value");

            rs = statement.executeQuery("SELECT * FROM " + sTable + " WHERE `index` = " + index);
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            String correct = rs.getString("value");

            if (msg.equalsIgnoreCase(correct))
                return true;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    boolean next() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'index'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            int index = rs.getInt("value");

            rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'length'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            int length = rs.getInt("value");

            index++;

            statement.executeUpdate("UPDATE " + mTable +
                    " SET " +
                    "value = " + index + " " +
                    "WHERE name = 'index'");
            if (index == length)
                return true;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    String nextWord() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM script_meta WHERE name = 'index'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            int index = rs.getInt("value");

            rs = statement.executeQuery("SELECT * FROM " + sTable + " WHERE `index` = " + index);
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            String word;
            try {
                word = rs.getString("value");
            } catch (Exception e) {
                return first;
            }

            return word;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    String title() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'title'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            return rs.getString("value");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    int length() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'length'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            return rs.getInt("value");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return 0;
    }

    int index() {
        try (Connection connect = DriverManager.getConnection(url)) {
            Statement statement = connect.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM " + mTable + " WHERE name = 'index'");
            if(!rs.next())
                throw new SQLException("Returned result is null.");

            return rs.getInt("value");
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return 0;
    }
}
