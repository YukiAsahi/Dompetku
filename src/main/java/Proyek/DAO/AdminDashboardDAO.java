package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.Pengguna;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class AdminDashboardDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SQL_PENDING_APPLICANTS = "SELECT idUser, namaAkun, namaLengkap, email, tipeAkun, aktif, tanggalDaftar "
            + "FROM pengguna "
            + "WHERE aktif = 'belum aktif' "
            + "ORDER BY tanggalDaftar ASC";

    private static final String SQL_APPROVE_APPLICANT = "UPDATE pengguna SET aktif = 'aktif' WHERE idUser = ?";

    private static final String SQL_REJECT_APPLICANT = "UPDATE pengguna SET aktif = 'deaktif' WHERE idUser = ?";

    private static final String SQL_GET_MANAGED_USERS = "SELECT idUser, namaAkun, namaLengkap, email, tipeAkun, aktif, tanggalDaftar "
            + "FROM pengguna "
            + "WHERE aktif IN ('aktif', 'deaktif') "
            + "ORDER BY namaAkun ASC";

    private static final String SQL_UPDATE_USER_STATUS = "UPDATE pengguna SET aktif = ? WHERE idUser = ?";

    private static final String SQL_GET_LOGIN_HISTORY = "SELECT idUser, namaAkun, email, loginTerakhir "
            + "FROM Pengguna "
            + "WHERE loginTerakhir IS NOT NULL "
            + "ORDER BY loginTerakhir DESC";

    private static final String SQL_GET_ALL_HISTORY_DATA = "SELECT namaAkun, tanggalDaftar, loginTerakhir FROM Pengguna";

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Pengguna> getPendingApplicants() {
        List<Pengguna> result = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return result;
            }

            st = con.prepareStatement(SQL_PENDING_APPLICANTS);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Pengguna p = mapResultSetToPengguna(rs);
                result.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }

    public boolean approveApplicant(int idUser) {
        return executeUpdate(SQL_APPROVE_APPLICANT, idUser);
    }

    public boolean rejectApplicant(int idUser) {
        return executeUpdate(SQL_REJECT_APPLICANT, idUser);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Pengguna> getAllUsers() {
        List<Pengguna> list = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return list;
            }

            st = con.prepareStatement(SQL_GET_MANAGED_USERS);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Pengguna p = mapResultSetToPengguna(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateUserStatus(int idUser, String newStatus) {
        try {
            con = getCon();
            if (con == null)
                return false;

            st = con.prepareStatement(SQL_UPDATE_USER_STATUS);
            st.setString(1, newStatus);
            st.setInt(2, idUser);

            int rows = st.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    private Pengguna mapResultSetToPengguna(ResultSet rs) throws SQLException {
        Pengguna p = new Pengguna();
        p.setIdUser(rs.getInt("idUser"));
        p.setNamaAkun(rs.getString("namaAkun"));
        p.setNamaLengkap(rs.getString("namaLengkap"));
        p.setEmail(rs.getString("email"));
        p.setTipeAkun(rs.getString("tipeAkun"));
        p.setAktif(rs.getString("aktif"));
        return p;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private boolean executeUpdate(String sql, int id) {
        try {
            con = getCon();
            if (con == null)
                return false;
            st = con.prepareStatement(sql);
            st.setInt(1, id);
            int rows = st.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Pengguna> getUserLoginHistory() {
        List<Pengguna> list = new ArrayList<>();
        try {
            con = getCon();
            if (con == null)
                return list;

            st = con.prepareStatement(SQL_GET_LOGIN_HISTORY);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Pengguna p = new Pengguna();
                p.setIdUser(rs.getInt("idUser"));
                p.setNamaAkun(rs.getString("namaAkun"));
                p.setEmail(rs.getString("email"));

                if (rs.getTimestamp("loginTerakhir") != null) {
                    p.setLoginTerakhir(rs.getTimestamp("loginTerakhir").toLocalDateTime());
                }
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Pengguna> getAllUsersForHistory() {
        List<Pengguna> list = new ArrayList<>();
        try {
            con = getCon();
            if (con == null)
                return list;

            st = con.prepareStatement(SQL_GET_ALL_HISTORY_DATA);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Pengguna p = new Pengguna();
                p.setNamaAkun(rs.getString("namaAkun"));

                if (rs.getTimestamp("tanggalDaftar") != null) {
                    p.setTanggalDaftar(rs.getTimestamp("tanggalDaftar").toLocalDateTime());
                }

                if (rs.getTimestamp("loginTerakhir") != null) {
                    p.setLoginTerakhir(rs.getTimestamp("loginTerakhir").toLocalDateTime());
                }
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    public int getLoginsToday() {
        String sql = "SELECT COUNT(DISTINCT idUser) FROM pengguna WHERE DATE(loginTerakhir) = CURDATE()";
        return executeCountQuery(sql);
    }

    public int getLoginsThisMonth() {
        String sql = "SELECT COUNT(DISTINCT idUser) FROM pengguna WHERE YEAR(loginTerakhir) = YEAR(CURDATE()) AND MONTH(loginTerakhir) = MONTH(CURDATE())";
        return executeCountQuery(sql);
    }

    public int getTotalActiveUsers() {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE aktif = 'aktif'";
        return executeCountQuery(sql);
    }

    public int getTotalInactiveUsers() {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE aktif = 'deaktif'";
        return executeCountQuery(sql);
    }

    public int getTotalPendingUsers() {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE aktif = 'belum aktif'";
        return executeCountQuery(sql);
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM pengguna";
        return executeCountQuery(sql);
    }

    public int getTotalMahasiswa() {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE tipeAkun = 'Mahasiswa'";
        return executeCountQuery(sql);
    }

    public int getTotalOrangTua() {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE tipeAkun = 'OrangTua'";
        return executeCountQuery(sql);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private int executeCountQuery(String sql) {
        try {
            con = getCon();
            if (con == null)
                return 0;

            st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
}


