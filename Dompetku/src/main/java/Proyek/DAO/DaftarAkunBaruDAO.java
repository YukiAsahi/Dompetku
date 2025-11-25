package Proyek.DAO;

import Proyek.Model.DaftarAkunBaru;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DaftarAkunBaruDAO {

    private static final String INSERT_DAFTAR_AKUN = 
        "INSERT INTO DaftarAkunBaru (namaAkun, sandi, namaLengkap, email, tipeDaftar, statusDaftar, tanggalDaftar) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public boolean insertDaftarAkunBaru(DaftarAkunBaru daftarAkun) {
        Connection connection = BaseDAO.getCon();
        
        if (connection == null) {
            System.err.println("✗ Gagal connect ke database!");
            return false;
        }
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DAFTAR_AKUN)) {

            System.out.println("✓ Connection ke database berhasil!");
            
            preparedStatement.setString(1, daftarAkun.getNamaAkun());
            preparedStatement.setString(2, daftarAkun.getSandi());
            preparedStatement.setString(3, daftarAkun.getNamaLengkap());
            preparedStatement.setString(4, daftarAkun.getEmail());
            preparedStatement.setString(5, daftarAkun.getTipeDaftar());
            preparedStatement.setString(6, daftarAkun.getStatusDaftar());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(daftarAkun.getTanggalDaftar()));

            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("✓ Data berhasil disimpan! Rows: " + rowsInserted);
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("✗ ERROR DATABASE:");
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
}
