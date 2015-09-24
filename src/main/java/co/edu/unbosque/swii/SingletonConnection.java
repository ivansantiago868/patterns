package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alejandro on 4/09/15.
 */
public class SingletonConnection {
    private static Connection connection;
    private static final Boolean control=true;

    public SingletonConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url="jdbc:postgresql://host:5432/software_2";
        synchronized (control) {
            if(connection!=null) return;
            connection = DriverManager.getConnection(url, "grupo7", "p_5t6TsJu8");
        }
    }
    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        if(connection==null){
            new SingletonConnection();
        }
        return connection;

    }
}
