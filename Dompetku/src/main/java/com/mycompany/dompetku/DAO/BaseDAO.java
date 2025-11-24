/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dompetku.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author AERO
 */
public class BaseDAO {

    private static String DB_NAME = "dompetku";
    private static String DB_HOST = "localhost";
    private static String DB_USER = "akun";
    private static String DB_PASS = "1234";

    public static Connection getCon() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":3307/" + DB_NAME, DB_USER, DB_PASS);
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

