package fyi.sorenneedscoffee.garbagecan.moderation.util;

import fyi.sorenneedscoffee.garbagecan.moderation.warnings.OffenseType;
import fyi.sorenneedscoffee.garbagecan.moderation.warnings.Warning;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;


/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class DataContext {
    private final String url;
    private final String[] creds;
    private final DSLContext context = DSL.using(SQLDialect.MYSQL);
    private final Logger log;

    private final Table<Record> kTable;
    private final Field<String> kField_id;

    private final Table<Record> mTable;
    private final Field<Integer> mField_id;
    private final Field<Timestamp> mField_creation_time;
    private final Field<String> mField_user_id;
    private final Field<Integer> mField_offense_id;
    private final Field<String> mField_additional_comments;

    private final Table<Record> nTable;

    public DataContext(String url) {
        this.url = url.replaceAll("(\\w+:\\w+)@", "");
        Matcher matcher = Pattern.compile("(\\w+:\\w+)@")
                .matcher(url);
        if (!matcher.find())
            throw new IllegalArgumentException("No username or password was found");
        creds = matcher.group(1).split(":");

        log = LoggerFactory.getLogger("DataContext");

        kTable = table("kick_list");
        kField_id = field("id", String.class);

        mTable = table("kick_list");
        mField_id = field("id", Integer.class);
        mField_creation_time = field("creation_time", Timestamp.class);
        mField_user_id = field("user_id", String.class);
        mField_offense_id = field("offense_id", Integer.class);
        mField_additional_comments = field("additional_comments", String.class);

        nTable = table("nono_words");
    }

    public Warning addWarning(String uId, int offenseType, String additionalComments) {
        Timestamp stamp = Timestamp.valueOf(ModUtil.formatter.withZone(ZoneOffset.UTC).format(Instant.now()));

        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {

            Query query = context.insertInto(mTable,
                    mField_user_id,
                    mField_creation_time,
                    mField_offense_id,
                    mField_additional_comments)
                    .values(uId, stamp, offenseType, additionalComments)
                    .returning(mField_id);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));
            set.first();

            return getWarning(set.getInt("id"));
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public void clearModerationHistory(String uId) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Statement statement = conn.createStatement();

            statement.executeUpdate(
                context.delete(mTable)
                    .where(mField_user_id.eq(uId))
                    .getSQL(ParamType.INLINED)
            );

            statement.executeUpdate(
                    context.delete(kTable)
                            .where(kField_id.eq(uId))
                            .getSQL(ParamType.INLINED)
            );
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public Warning getWarning(int id) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            ResultSet set = conn.createStatement().executeQuery(
                    context.select()
                            .from(mTable)
                            .where(mField_id.eq(id))
                            .getSQL(ParamType.INLINED)
            );

            if (!set.next())
                return null;

            return new Warning(set.getInt("id"),
                    set.getString("user_id"),
                    OffenseType.getTypeById(set.getInt("offense_id")),
                    set.getString("additional_comments"),
                    Timestamp.valueOf(set.getString("creation_time"))
            );
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public List<Warning> getWarnings(String uId, boolean includeMisc) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.select()
                    .from(mTable)
                    .where(mField_user_id.eq(uId));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));

            List<Warning> warnings = new ArrayList<>();

            while (set.next()) {
                warnings.add(
                        new Warning(set.getInt("id"),
                                set.getString("user_id"),
                                OffenseType.getTypeById(set.getInt("offense_id")),
                                set.getString("additional_comments"),
                                Timestamp.valueOf(set.getString("creation_time"))
                        )
                );
            }

            if (!includeMisc)
                warnings.removeIf(w -> w.getOffenseType() == OffenseType.MISC);

            return warnings;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public List<Warning> getSimilarWarnings(Warning warning) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.select()
                    .from(mTable)
                    .where(mField_user_id.eq(warning.getuId()))
                    .and(mField_offense_id.eq(warning.getOffenseType().getId()));

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));

            List<Warning> warnings = new ArrayList<>();

            while (set.next()) {
                if (!set.getString("user_id").equals(warning.getuId()))
                    warnings.add(
                            new Warning(set.getInt("id"),
                                    set.getString("user_id"),
                                    OffenseType.getTypeById(set.getInt("offense_id")),
                                    set.getString("additional_comments"),
                                    Timestamp.valueOf(set.getString("creation_time"))
                            )
                    );
            }

            return warnings;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    public void addUserToKicklist(String uId) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            conn.createStatement().executeUpdate(
                context.insertInto(kTable, kField_id)
                    .values(uId)
                    .getSQL(ParamType.INLINED)
            );
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public boolean isOnKickList(String uId) {
        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query result = context.select()
                    .from(kTable)
                    .where(kField_id.eq(uId));

            return conn.createStatement().executeQuery(
                    result.getSQL(ParamType.INLINED)
            ).next();
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return false;
    }

    public Set<String> findNoNoWords(String input) {
        if(input == null || input.isBlank()) {
            return Collections.emptySet();
        }

        try (Connection conn = DriverManager.getConnection(url, creds[0], creds[1])) {
            Query query = context.select()
                    .from(nTable);

            ResultSet set = conn.createStatement().executeQuery(query.getSQL(ParamType.INLINED));

            HashMap<String, List<String>> words = new HashMap<>();
            while (set.next()) {
                String exceptions = set.getString("exceptions");
                words.put(
                    set.getString("word"),
                    exceptions == null ? null : Arrays.asList(exceptions.split(","))
                );
            }

            int largestWordLength = 0;

            for(String word : words.keySet()) {
                if(word.length() > largestWordLength) {
                    largestWordLength = word.length();
                }
            }

            // don't forget to remove leetspeak, probably want to move this to its own function and use regex if you want to use this

            input = input.replaceAll("1","l")
                    .replaceAll("!","i")
                    .replaceAll("3","e")
                    .replaceAll("4","a")
                    .replaceAll("@","a")
                    .replaceAll("5","s")
                    .replaceAll("7","t")
                    .replaceAll("0","o")
                    .replaceAll("9","g");


            input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

            Set<String> caughtWords = new HashSet<>();

            // iterate over each letter in the word
            for(int start = 0; start < input.length(); start++) {
                // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
                for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                    String wordToCheck = input.substring(start, start + offset);
                    if(words.containsKey(wordToCheck)) {
                        // for example, if you want to say the word bass, that should be possible.
                        List<String> ignoreCheck = words.get(wordToCheck);
                        boolean ignore = false;
                        for(String value : ignoreCheck) {
                            if(value != null && input.contains(value)) {
                                ignore = true;
                                break;
                            }
                        }
                        if(!ignore) {
                            caughtWords.add(wordToCheck);
                        }
                    }
                }
            }

            return caughtWords;
        } catch (SQLException e) {
            log.error("JDBC experienced the following error:" + ExceptionUtils.getMessage(e) + " Please see below for details");
            log.error(ExceptionUtils.getStackTrace(e));
        }

        return Collections.emptySet();
    }
}
