package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.BatasUang;
import Proyek.Model.CatatanUang;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DashboardOrangTuaDAO {

    private static PreparedStatement st;
    private static Connection con;
    private static final String SQL_VALIDATE_CHILD_CODE = "SELECT oal.idAnak as idUser, p.namaLengkap FROM ortuanaklink oal "
            +
            "JOIN pengguna p ON oal.idAnak = p.idUser " +
            "WHERE oal.kodeAkses = ?";
    private static final String SQL_GET_CHILD_NAME = "SELECT namaLengkap FROM pengguna WHERE idUser = ?";
    private static final String SQL_CHILD_TOTAL_BALANCE = "SELECT COALESCE(SUM(saldoSekarang), 0) AS total FROM dompet WHERE idUser = ?";
    private static final String SQL_CHILD_MONTHLY_EXPENSE = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang "
            +
            "WHERE idUser = ? AND jenisCatatan = 'Pengeluaran' " +
            "AND MONTH(tanggalCatat) = MONTH(CURRENT_DATE()) AND YEAR(tanggalCatat) = YEAR(CURRENT_DATE())";
    private static final String SQL_CHILD_MONTHLY_INCOME = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang "
            +
            "WHERE idUser = ? AND jenisCatatan = 'Pemasukan' " +
            "AND MONTH(tanggalCatat) = MONTH(CURRENT_DATE()) AND YEAR(tanggalCatat) = YEAR(CURRENT_DATE())";
    private static final String SQL_CHILD_TRANSACTION_COUNT = "SELECT COUNT(*) AS total FROM catatanuang WHERE idUser = ?";
    private static final String SQL_CHILD_TRANSACTIONS = "SELECT c.idTransaksi, c.idUser, c.idDompet, c.idKategori, c.nominal, c.jenisCatatan, "
            +
            "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet " +
            "FROM catatanuang c " +
            "LEFT JOIN kategori k ON c.idKategori = k.idKategori " +
            "LEFT JOIN dompet d ON c.idDompet = d.idDompet " +
            "WHERE c.idUser = ? ORDER BY c.tanggalCatat DESC LIMIT ?";
    private static final String SQL_CHILD_EXPENSE_BY_CATEGORY = "SELECT k.namaKategori, COALESCE(SUM(c.nominal), 0) AS total "
            +
            "FROM catatanuang c " +
            "JOIN kategori k ON c.idKategori = k.idKategori " +
            "WHERE c.idUser = ? AND c.jenisCatatan = 'Pengeluaran' " +
            "AND MONTH(c.tanggalCatat) = MONTH(CURRENT_DATE()) AND YEAR(c.tanggalCatat) = YEAR(CURRENT_DATE()) " +
            "GROUP BY k.namaKategori ORDER BY total DESC";
    private static final String SQL_CHILD_BUDGETS = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            +
            "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori " +
            "FROM batasuang bu " +
            "LEFT JOIN kategori k ON bu.idKategori = k.idKategori " +
            "WHERE bu.idUser = ? AND bu.jenis = 'BUDGETING' ORDER BY bu.tanggalBikin DESC";
    private static final String SQL_CHILD_GOALS = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            +
            "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori, " +
            "d.namaDompet, d.saldoSekarang " +
            "FROM batasuang bu " +
            "LEFT JOIN kategori k ON bu.idKategori = k.idKategori " +
            "LEFT JOIN dompet d ON bu.dompetId = d.idDompet " +
            "WHERE bu.idUser = ? AND bu.jenis = 'GOALS' ORDER BY bu.tanggalBikin DESC";
    private static final String SQL_LARGE_TRANSACTIONS = "SELECT c.idTransaksi, c.idUser, c.idDompet, c.idKategori, c.nominal, c.jenisCatatan, "
            +
            "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet " +
            "FROM catatanuang c " +
            "LEFT JOIN kategori k ON c.idKategori = k.idKategori " +
            "LEFT JOIN dompet d ON c.idDompet = d.idDompet " +
            "WHERE c.idUser = ? AND c.nominal >= ? " +
            "ORDER BY c.tanggalCatat DESC LIMIT 20";
    private static final String SQL_INSERT_BUDGET = "INSERT INTO batasuang (idUser, idKategori, maxUang, mulaiDari, selesaiSampai, "
            +
            "tanggalBikin, jenis, progressTabungan, dompetId) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'BUDGETING', 0, NULL)";
    private static final String SQL_GET_SPENDING = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
            "WHERE idUser = ? AND idKategori = ? AND jenisCatatan = 'Pengeluaran' " +
            "AND tanggalCatat BETWEEN ? AND ?";
    @SuppressWarnings("CallToPrintStackTrace")
    public int validateAndGetChildId(String code) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_VALIDATE_CHILD_CODE);
            st.setString(1, code);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int childId = rs.getInt("idUser");
                return childId;
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return -1;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public String getChildName(int childId) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_GET_CHILD_NAME);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("namaLengkap");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return null;
    }
    public boolean checkChildCode(String code) {
        return false;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public double getChildTotalBalance(int childId) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_TOTAL_BALANCE);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public double getChildMonthlyExpense(int childId) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_MONTHLY_EXPENSE);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public double getChildMonthlyIncome(int childId) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_MONTHLY_INCOME);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public int getChildTransactionCount(int childId) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_TRANSACTION_COUNT);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> getChildTransactions(int childId, int limit) {
        List<CatatanUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_TRANSACTIONS);
            st.setInt(1, childId);
            st.setInt(2, limit);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public Map<String, Double> getChildExpenseByCategory(int childId) {
        Map<String, Double> map = new HashMap<>();
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_EXPENSE_BY_CATEGORY);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("namaKategori"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return map;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public List<BatasUang> getChildBudgets(int childId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_BUDGETS);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                BatasUang bu = mapResultSetToBudget(rs);
                double spending = getCurrentSpending(childId, bu.getKategoriId(),
                        bu.getMulaiDari(), bu.getSelesaiSampai());
                bu.setCurrentSpending(spending);
                list.add(bu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean setChildBudget(int childId, int kategoriId, double maxUang,
            LocalDate startDate, LocalDate endDate) {
        try {
            con = getCon();
            st = con.prepareStatement(SQL_INSERT_BUDGET);
            st.setInt(1, childId);
            st.setInt(2, kategoriId);
            st.setDouble(3, maxUang);
            st.setDate(4, Date.valueOf(startDate));
            st.setDate(5, Date.valueOf(endDate));
            st.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public double getCurrentSpending(int userId, int kategoriId, LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return 0;
        try {
            con = getCon();
            st = con.prepareStatement(SQL_GET_SPENDING);
            st.setInt(1, userId);
            st.setInt(2, kategoriId);
            st.setDate(3, Date.valueOf(start));
            st.setDate(4, Date.valueOf(end));
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public List<BatasUang> getChildGoals(int childId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SQL_CHILD_GOALS);
            st.setInt(1, childId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToGoals(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> getChildLargeTransactions(int childId, double threshold) {
        List<CatatanUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SQL_LARGE_TRANSACTIONS);
            st.setInt(1, childId);
            st.setDouble(2, threshold);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    private CatatanUang mapResultSetToTransaction(ResultSet rs) throws SQLException {
        CatatanUang cu = new CatatanUang();
        cu.setId(rs.getInt("idTransaksi"));
        cu.setIdUser(rs.getInt("idUser"));
        cu.setIdDompet(rs.getInt("idDompet"));
        cu.setIdKategori(rs.getInt("idKategori"));
        cu.setNominal(rs.getDouble("nominal"));
        cu.setJenisCatatan(rs.getString("jenisCatatan"));
        cu.setCatatan(rs.getString("catatan"));
        Timestamp ts = rs.getTimestamp("tanggalCatat");
        if (ts != null) {
            cu.setTanggalCatat(ts.toLocalDateTime());
        }
        cu.setNamaKategori(rs.getString("namaKategori"));
        cu.setNamaDompet(rs.getString("namaDompet"));
        return cu;
    }

    private BatasUang mapResultSetToBudget(ResultSet rs) throws SQLException {
        BatasUang bu = new BatasUang(
                rs.getInt("idBatas"),
                rs.getInt("idUser"),
                rs.getInt("idKategori"),
                rs.getDouble("maxUang"),
                rs.getDate("mulaiDari") != null ? rs.getDate("mulaiDari").toLocalDate() : null,
                rs.getDate("selesaiSampai") != null ? rs.getDate("selesaiSampai").toLocalDate() : null,
                rs.getTimestamp("tanggalBikin") != null ? rs.getTimestamp("tanggalBikin").toLocalDateTime() : null,
                rs.getString("jenis"),
                rs.getDouble("progressTabungan"),
                rs.getInt("dompetId"));
        bu.setKategoriNama(rs.getString("namaKategori"));
        return bu;
    }

    private BatasUang mapResultSetToGoals(ResultSet rs) throws SQLException {
        BatasUang bu = mapResultSetToBudget(rs);
        bu.setNamaDompet(rs.getString("namaDompet"));
        bu.setSaldoDompet(rs.getDouble("saldoSekarang"));
        return bu;
    }
}


