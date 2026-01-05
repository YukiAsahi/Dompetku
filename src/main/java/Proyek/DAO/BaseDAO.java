package Proyek.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
public class BaseDAO {

    private static String DB_NAME = "dompetku";
    private static String DB_HOST = "localhost";
    private static String DB_USER = "root";
    private static String DB_PASS = "";

    public static Connection getCon() {
        Connection con = null;
        try {
            String url = "jdbc:mysql://" + DB_HOST + ":3307/" + DB_NAME;
            con = DriverManager.getConnection(url, DB_USER, DB_PASS);
            if (con != null) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void closeCon(Connection con) {
        try {
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


