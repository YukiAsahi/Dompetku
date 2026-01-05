package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.Pengguna;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class PenggunaDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SELECT_BY_NAMAAKUN = "SELECT idUser, namaAkun, sandi, namaLengkap, email, tipeAkun, aktif, "
            + "tanggalDaftar, loginTerakhir, foto "
            + "FROM Pengguna WHERE namaAkun = ?";

    private static final String INSERT_PENGGUNA = "INSERT INTO Pengguna (namaAkun, sandi, namaLengkap, email, tipeAkun, aktif, tanggalDaftar) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_FOTO = "UPDATE Pengguna SET foto = ? WHERE idUser = ?";

    private static final String UPDATE_LOGIN_TERAKHIR = "UPDATE Pengguna SET loginTerakhir = ? WHERE idUser = ?";

    private static final String SELECT_BY_EMAIL = "SELECT idUser, namaAkun, sandi, namaLengkap, email, tipeAkun, aktif, "
            + "tanggalDaftar, loginTerakhir, foto "
            + "FROM Pengguna WHERE email = ?";

    @SuppressWarnings("CallToPrintStackTrace")
    public Pengguna cariByNamaAkun(String namaAkun) {
        Pengguna pengguna = null;
        try {
            con = getCon();
            if (con == null) {
                return null;
            }

            st = con.prepareStatement(SELECT_BY_NAMAAKUN);
            st.setString(1, namaAkun);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("idUser");
                String sandi = rs.getString("sandi");
                String namaLengkap = rs.getString("namaLengkap");
                String email = rs.getString("email");
                String tipeAkun = rs.getString("tipeAkun");
                String aktif = rs.getString("aktif");
                LocalDateTime tanggalDaftar = rs.getTimestamp("tanggalDaftar").toLocalDateTime();
                LocalDateTime loginTerakhir = rs.getTimestamp("loginTerakhir") != null
                        ? rs.getTimestamp("loginTerakhir").toLocalDateTime()
                        : null;
                byte[] foto = rs.getBytes("foto");

                pengguna = new Pengguna(idUser, namaAkun, sandi, namaLengkap, email,
                        tipeAkun, aktif, tanggalDaftar, loginTerakhir, foto);
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return pengguna;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tambahPengguna(Pengguna pengguna) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }
            st = con.prepareStatement(INSERT_PENGGUNA);
            st.setString(1, pengguna.getNamaAkun());
            st.setString(2, pengguna.getSandi());
            st.setString(3, pengguna.getNamaLengkap());
            st.setString(4, pengguna.getEmail());
            st.setString(5, pengguna.getTipeAkun());
            st.setString(6, pengguna.getAktif());
            st.setTimestamp(7, java.sql.Timestamp.valueOf(pengguna.getTanggalDaftar()));

            int result = st.executeUpdate();
            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean gantiFoto(int idUser, byte[] fotoBytes) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(UPDATE_FOTO);
            st.setBytes(1, fotoBytes);
            st.setInt(2, idUser);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateLoginTerakhir(int idUser, LocalDateTime waktuLogin) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(UPDATE_LOGIN_TERAKHIR);
            st.setTimestamp(1, java.sql.Timestamp.valueOf(waktuLogin));
            st.setInt(2, idUser);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Pengguna cariByEmail(String emailInput) {
        Pengguna pengguna = null;
        try {
            con = getCon();
            if (con == null)
                return null;

            st = con.prepareStatement(SELECT_BY_EMAIL);
            st.setString(1, emailInput);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("idUser");
                String namaAkun = rs.getString("namaAkun");
                String sandi = rs.getString("sandi");
                String namaLengkap = rs.getString("namaLengkap");
                String email = rs.getString("email");
                String tipeAkun = rs.getString("tipeAkun");
                String aktif = rs.getString("aktif");
                LocalDateTime tanggalDaftar = rs.getTimestamp("tanggalDaftar").toLocalDateTime();
                LocalDateTime loginTerakhir = rs.getTimestamp("loginTerakhir") != null
                        ? rs.getTimestamp("loginTerakhir").toLocalDateTime()
                        : null;
                byte[] foto = rs.getBytes("foto");

                pengguna = new Pengguna(idUser, namaAkun, sandi, namaLengkap, email,
                        tipeAkun, aktif, tanggalDaftar, loginTerakhir, foto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return pengguna;
    }

    public boolean apakahNamaAkunAda(String namaAkun) {
        return cariByNamaAkun(namaAkun) != null;
    }

    public boolean apakahEmailAda(String email) {
        return cariByEmail(email) != null;
    }
}
