package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
public class MahasiswaDashboardDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SQL_NET_CASHFLOW_PER_MONTH = "SELECT MONTH(tanggalCatat) AS bulan, " +
            "       SUM(CASE " +
            "               WHEN jenisCatatan = 'Pemasukan' THEN nominal " +
            "               WHEN jenisCatatan = 'Pengeluaran' THEN -nominal " +
            "               ELSE 0 " +
            "           END) AS neto " +
            "FROM catatanuang " +
            "WHERE idUser = ? AND YEAR(tanggalCatat) = ? " +
            "GROUP BY bulan " +
            "ORDER BY bulan";

    private static final String SQL_INCOME_PER_MONTH = "SELECT MONTH(tanggalCatat) AS bulan, " +
            "       SUM(nominal) AS total " +
            "FROM catatanuang " +
            "WHERE idUser = ? " +
            "  AND jenisCatatan = 'Pemasukan' " +
            "  AND YEAR(tanggalCatat) = ? " +
            "GROUP BY bulan " +
            "ORDER BY bulan";

    private static final String SQL_EXPENSE_PER_MONTH = "SELECT MONTH(tanggalCatat) AS bulan, " +
            "       SUM(nominal) AS total " +
            "FROM catatanuang " +
            "WHERE idUser = ? " +
            "  AND jenisCatatan = 'Pengeluaran' " +
            "  AND YEAR(tanggalCatat) = ? " +
            "GROUP BY bulan " +
            "ORDER BY bulan";

    private static final String SQL_SPENDING_PER_CATEGORY = "SELECT k.namaKategori AS kategori, " +
            "       SUM(c.nominal) AS total " +
            "FROM catatanuang c " +
            "JOIN kategori k ON c.idKategori = k.idKategori " +
            "WHERE c.idUser = ? " +
            "  AND c.jenisCatatan = 'Pengeluaran' " +
            "  AND YEAR(c.tanggalCatat) = ? " +
            "GROUP BY k.namaKategori " +
            "ORDER BY total DESC";
    private static final String SQL_GOALS_PROGRESS = "SELECT k.namaKategori AS kategori, " +
            "       bu.maxUang AS target, " +
            "       bu.progressTabungan AS saldo " +
            "FROM batasuang bu " +
            "JOIN kategori k ON bu.idKategori = k.idKategori " +
            "WHERE bu.idUser = ? AND bu.jenis = 'GOALS' " +
            "ORDER BY (bu.progressTabungan / bu.maxUang) DESC " +
            "LIMIT 3";
    private static final String SQL_BUDGET_USAGE = "SELECT k.namaKategori AS kategori, " +
            "       bu.maxUang AS budget, " +
            "       COALESCE(SUM(c.nominal), 0) AS spent " +
            "FROM batasuang bu " +
            "JOIN kategori k ON bu.idKategori = k.idKategori " +
            "LEFT JOIN catatanuang c ON c.idKategori = bu.idKategori " +
            "    AND c.idUser = bu.idUser AND c.jenisCatatan = 'Pengeluaran' " +
            "    AND c.tanggalCatat BETWEEN bu.mulaiDari AND bu.selesaiSampai " +
            "WHERE bu.idUser = ? AND bu.jenis = 'BUDGETING' " +
            "GROUP BY bu.idBatas, k.namaKategori, bu.maxUang " +
            "ORDER BY (COALESCE(SUM(c.nominal), 0) / bu.maxUang) DESC " +
            "LIMIT 5";
    private static final String SQL_TOTAL_BALANCE = "SELECT SUM(saldo) AS totalSaldo FROM dompet WHERE idUser = ?";

    @SuppressWarnings("CallToPrintStackTrace")
    public Map<Integer, Double> getNetCashFlowPerMonth(int idUser, int year) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_NET_CASHFLOW_PER_MONTH);
            st.setInt(1, idUser);
            st.setInt(2, year);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int bulan = rs.getInt("bulan");
                double neto = rs.getDouble("neto");
                result.put(bulan, neto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Map<Integer, Double> getIncomePerMonth(int idUser, int year) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_INCOME_PER_MONTH);
            st.setInt(1, idUser);
            st.setInt(2, year);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int bulan = rs.getInt("bulan");
                double total = rs.getDouble("total");
                result.put(bulan, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Map<Integer, Double> getExpensePerMonth(int idUser, int year) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_EXPENSE_PER_MONTH);
            st.setInt(1, idUser);
            st.setInt(2, year);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                int bulan = rs.getInt("bulan");
                double total = rs.getDouble("total");
                result.put(bulan, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Map<String, Double> getSpendingPerCategory(int idUser, int year) {
        Map<String, Double> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_SPENDING_PER_CATEGORY);
            st.setInt(1, idUser);
            st.setInt(2, year);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                double total = rs.getDouble("total");
                result.put(kategori, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public Map<String, double[]> getGoalsProgress(int idUser) {
        Map<String, double[]> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_GOALS_PROGRESS);
            st.setInt(1, idUser);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                double target = rs.getDouble("target");
                double saldo = rs.getDouble("saldo");
                result.put(kategori, new double[] { target, saldo });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public Map<String, double[]> getBudgetUsage(int idUser) {
        Map<String, double[]> result = new LinkedHashMap<>();
        try {
            con = getCon();
            if (con == null) {
                return Collections.emptyMap();
            }

            st = con.prepareStatement(SQL_BUDGET_USAGE);
            st.setInt(1, idUser);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                double budget = rs.getDouble("budget");
                double spent = rs.getDouble("spent");
                result.put(kategori, new double[] { budget, spent });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return result;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public double getTotalBalance(int idUser) {
        double total = 0;
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(SQL_TOTAL_BALANCE);
            st.setInt(1, idUser);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("totalSaldo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return total;
    }

    private static final String SQL_GET_PARENT_CODE = "SELECT kodeOrangTua FROM pengguna WHERE idUser = ?";
    private static final String SQL_UPDATE_PARENT_CODE = "UPDATE pengguna SET kodeOrangTua = ? WHERE idUser = ?";
    @SuppressWarnings("CallToPrintStackTrace")
    public String getParentCode(int userId) {
        try {
            con = getCon();
            if (con == null)
                return null;
            st = con.prepareStatement(SQL_GET_PARENT_CODE);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("kodeOrangTua");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return null;
    }
    @SuppressWarnings("CallToPrintStackTrace")
    public String generateParentCode(int userId) {
        String code = generateRandomCode(6);
        try {
            con = getCon();
            if (con == null)
                return null;
            st = con.prepareStatement(SQL_UPDATE_PARENT_CODE);
            st.setString(1, code);
            st.setInt(2, userId);
            int updated = st.executeUpdate();
            if (updated > 0) {
                return code;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return null;
    }
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    public double getTotalSaldo(int idUser) {
        String sql = "SELECT COALESCE(SUM(saldoSekarang), 0) AS total FROM dompet WHERE idUser = ?";
        try {
            con = getCon();
            st = con.prepareStatement(sql);
            st.setInt(1, idUser);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(con);
        }
        return 0.0;
    }
    public double getMonthExpense(int idUser, int year, int month) {
        String sql = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                "WHERE idUser = ? AND jenisCatatan = 'Pengeluaran' " +
                "AND YEAR(tanggalCatat) = ? AND MONTH(tanggalCatat) = ?";
        try {
            con = getCon();
            st = con.prepareStatement(sql);
            st.setInt(1, idUser);
            st.setInt(2, year);
            st.setInt(3, month);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(con);
        }
        return 0.0;
    }
    public double getMonthIncome(int idUser, int year, int month) {
        String sql = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                "WHERE idUser = ? AND jenisCatatan = 'Pemasukan' " +
                "AND YEAR(tanggalCatat) = ? AND MONTH(tanggalCatat) = ?";
        try {
            con = getCon();
            st = con.prepareStatement(sql);
            st.setInt(1, idUser);
            st.setInt(2, year);
            st.setInt(3, month);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(con);
        }
        return 0.0;
    }
    public int getMonthTransactionCount(int idUser, int year, int month) {
        String sql = "SELECT COUNT(*) AS total FROM catatanuang " +
                "WHERE idUser = ? AND YEAR(tanggalCatat) = ? AND MONTH(tanggalCatat) = ?";
        try {
            con = getCon();
            st = con.prepareStatement(sql);
            st.setInt(1, idUser);
            st.setInt(2, year);
            st.setInt(3, month);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(con);
        }
        return 0;
    }
}


