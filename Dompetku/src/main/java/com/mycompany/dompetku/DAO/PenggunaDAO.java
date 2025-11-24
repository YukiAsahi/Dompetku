package com.mycompany.dompetku.DAO;

import com.mycompany.dompetku.Model.Pengguna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PenggunaDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/dompetku"; // sesuaikan nama DB
    private final String jdbcUsername = "root"; // sesuaikan username DB
    private final String jdbcPassword = ""; // sesuaikan password DB

    private static final String SELECT_BY_USERNAME_PASSWORD = 
        "SELECT id, username, password, email, nama_lengkap, tipe_user, aktif, created_date " +
        "FROM pengguna WHERE username = ? AND password = ?";

    public Pengguna getPenggunaByUsernamePassword(String username, String password) {
        Pengguna pengguna = null;
        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME_PASSWORD)) {
            
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String namaLengkap = rs.getString("nama_lengkap");
                String tipeUser = rs.getString("tipe_user");
                boolean aktif = rs.getBoolean("aktif");
                LocalDateTime createdDate = rs.getTimestamp("created_date").toLocalDateTime();

                pengguna = new Pengguna(id, username, password, email, namaLengkap, tipeUser, aktif, createdDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pengguna;
    }
}
