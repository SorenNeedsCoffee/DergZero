package xyz.joesorensen.xputil;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * -=XPUtil=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
class DbManager {
    private Connection connect;
    private String table;

    DbManager(String ip, String db, String table, String user, String pass) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        connect = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+db+"?"
                + "user="+user+"&password="+pass);

        this.table = table;
    }
}
