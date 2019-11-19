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

    DbManager(String ip, String user, String pass) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        connect = DriverManager.getConnection("jdbc:mysql://"+ip+"/feedback?"
                + "user="+user+"&password="+pass);
    }
}
