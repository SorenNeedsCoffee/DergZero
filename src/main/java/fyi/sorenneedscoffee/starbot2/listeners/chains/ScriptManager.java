package fyi.sorenneedscoffee.starbot2.listeners.chains;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
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

import static fyi.sorenneedscoffee.starbot2.listeners.chains.db.Tables.SCRIPT;
import static fyi.sorenneedscoffee.starbot2.listeners.chains.db.Tables.SCRIPT_META;

public class ScriptManager {
    private final Logger log = LoggerFactory.getLogger("ScriptManager");
    Random random = new Random();
    private String first;
    private final String url;

    ScriptManager() {
        url = "jdbc:mysql://" + "192.168.86.74" + "/" + "s4_starbot2" + "?"
                + "user=" + "u4_FTxSjk9AiF" + "&password=" + "zQeV9UsjANUrk4B8RzmrZmLF";
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

        String rawScript = (String) raw.get("script");
        script = rawScript.replaceAll("([^A-Za-z0-9\\s\\-':()])+", "").split(" ");

        title = (String) raw.get("title");

        first = script[0];

        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            context.deleteFrom(SCRIPT).execute();
            context.deleteFrom(SCRIPT_META).execute();

            context.insertInto(SCRIPT_META, SCRIPT_META.NAME, SCRIPT_META.VALUE)
                    .values("title", title)
                    .execute();
            context.insertInto(SCRIPT_META, SCRIPT_META.NAME, SCRIPT_META.VALUE)
                    .values("index", Integer.toString(index))
                    .execute();
            context.insertInto(SCRIPT_META, SCRIPT_META.NAME, SCRIPT_META.VALUE)
                    .values("length", Integer.toString(script.length))
                    .execute();
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        Thread th = new Thread(() -> {
            try (Connection connect = DriverManager.getConnection(url)) {
                DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

                for (int i = 0; i < script.length; i++) {
                    context.insertInto(SCRIPT, SCRIPT.INDEX, SCRIPT.VALUE)
                        .values(i, script[i])
                        .execute();
                }

                log.info("Script loaded with " + script.length + " records");
            } catch (SQLException e) {
                log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
        th.start();
    }

    boolean isActive() {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            Result<Record> result = context.select()
                    .from(SCRIPT_META)
                    .where(SCRIPT_META.NAME.eq("index"))
                    .fetch();

            if (result.isNotEmpty())
                return true;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    boolean checkMsg(String msg) {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            int index = index();

            Result<Record> resultScript = context.select()
                    .from(SCRIPT)
                    .where(SCRIPT.INDEX.eq(index))
                    .fetch();
            if(resultScript.isEmpty())
                throw new SQLException("Returned result is null.");

            String correct = resultScript.get(0).getValue(SCRIPT.VALUE);

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
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            int index = index();
            int length = length();
            index++;

            context.update(SCRIPT_META)
                    .set(SCRIPT_META.VALUE, Integer.toString(index))
                    .where(SCRIPT_META.NAME.eq("index"))
                    .execute();

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
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            int index = index();

            Result<Record> resultScript = context.select()
                    .from(SCRIPT)
                    .where(SCRIPT.INDEX.eq(index))
                    .fetch();

            String word;
            if(resultScript.isNotEmpty())
                word = resultScript.get(0).getValue(SCRIPT.VALUE);
            else
                return first;

            return word;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    String title() {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            Result<Record> result = context.select()
                    .from(SCRIPT_META)
                    .where(SCRIPT_META.NAME.eq("title"))
                    .fetch();
            if(result.isEmpty())
                throw new SQLException("Returned result is null.");

            return result.get(0).getValue(SCRIPT_META.VALUE);
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    int length() {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            Result<Record> result = context.select()
                    .from(SCRIPT_META)
                    .where(SCRIPT_META.NAME.eq("length"))
                    .fetch();
            if(result.isEmpty())
                throw new SQLException("Returned result is null.");

            return Integer.parseInt(result.get(0).getValue(SCRIPT_META.VALUE));
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return 0;
    }

    int index() {
        try (Connection connect = DriverManager.getConnection(url)) {
            DSLContext context = DSL.using(connect, SQLDialect.MARIADB);

            Result<Record> result = context.select()
                    .from(SCRIPT_META)
                    .where(SCRIPT_META.NAME.eq("index"))
                    .fetch();
            if(result.isEmpty())
                throw new SQLException("Returned result is null.");

            return Integer.parseInt(result.get(0).getValue(SCRIPT_META.VALUE));
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return 0;
    }
}
