package Proyek.DAO;

import Proyek.Model.Pengguna;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PenggunaDAO {

    private static final String SELECT_BY_NAMAAKUN_SANDI = 
        "SELECT idUser, namaAkun, sandi, namaLengkap, email, tipeAkun, aktif, kodeLink, tanggalBuat, loginTerakhir " +
        "FROM Pengguna WHERE namaAkun = ? AND sandi = ?";

    public Pengguna getPenggunaByNamaAkunSandi(String namaAkun, String sandi) {
        Connection connection = BaseDAO.getCon();
        
        if (connection == null) {
            System.err.println("✗ Gagal connect ke database!");
            return null;
        }
        
        Pengguna pengguna = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NAMAAKUN_SANDI)) {
            
            System.out.println("✓ Connection ke database berhasil!");
            
            preparedStatement.setString(1, namaAkun);
            preparedStatement.setString(2, sandi);
            
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int idUser = rs.getInt("idUser");
                String email = rs.getString("email");
                String namaLengkap = rs.getString("namaLengkap");
                String tipeAkun = rs.getString("tipeAkun");
                boolean aktif = rs.getBoolean("aktif");
                String kodeLink = rs.getString("kodeLink");
                LocalDateTime tanggalBuat = rs.getTimestamp("tanggalBuat").toLocalDateTime();
                LocalDateTime loginTerakhir = rs.getTimestamp("loginTerakhir") != null ? 
                    rs.getTimestamp("loginTerakhir").toLocalDateTime() : null;

                pengguna = new Pengguna(idUser, namaAkun, sandi, namaLengkap, email, tipeAkun, aktif, kodeLink, tanggalBuat, loginTerakhir);
                System.out.println("✓ User ditemukan: " + namaLengkap);
            } else {
                System.out.println("✗ User tidak ditemukan");
            }
        } catch (SQLException e) {
            System.err.println("✗ ERROR DATABASE:");
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }
        return pengguna;
    }
}
