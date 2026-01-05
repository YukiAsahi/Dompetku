package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.BatasUang;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class BatasUangDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SELECT_BUDGETING_BY_USER = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            + "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori "
            + "FROM batasuang bu "
            + "LEFT JOIN kategori k ON bu.idKategori = k.idKategori "
            + "WHERE bu.idUser = ? AND bu.jenis = 'BUDGETING' ORDER BY bu.tanggalBikin DESC";

    private static final String SELECT_BUDGETING_AKTIF = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            + "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori "
            + "FROM batasuang bu "
            + "LEFT JOIN kategori k ON bu.idKategori = k.idKategori "
            + "WHERE bu.idUser = ? AND bu.jenis = 'BUDGETING' AND bu.selesaiSampai >= ? "
            + "ORDER BY bu.tanggalBikin DESC";

    private static final String SELECT_GOALS_BY_USER = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            + "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori, "
            + "d.namaDompet, d.saldoSekarang "
            + "FROM batasuang bu "
            + "LEFT JOIN kategori k ON bu.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON bu.dompetId = d.idDompet "
            + "WHERE bu.idUser = ? AND bu.jenis = 'GOALS' ORDER BY bu.tanggalBikin DESC";

    private static final String SELECT_GOALS_AKTIF = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            + "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori, "
            + "d.namaDompet, d.saldoSekarang "
            + "FROM batasuang bu "
            + "LEFT JOIN kategori k ON bu.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON bu.dompetId = d.idDompet "
            + "WHERE bu.idUser = ? AND bu.jenis = 'GOALS' AND bu.progressTabungan < bu.maxUang "
            + "ORDER BY bu.tanggalBikin DESC";

    private static final String SELECT_BY_ID = "SELECT bu.idBatas, bu.idUser, bu.idKategori, bu.maxUang, bu.mulaiDari, bu.selesaiSampai, "
            + "bu.tanggalBikin, bu.jenis, bu.progressTabungan, bu.dompetId, k.namaKategori, "
            + "d.namaDompet, d.saldoSekarang "
            + "FROM batasuang bu "
            + "LEFT JOIN kategori k ON bu.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON bu.dompetId = d.idDompet "
            + "WHERE bu.idBatas = ?";

    private static final String INSERT = "INSERT INTO batasuang (idUser, idKategori, maxUang, mulaiDari, selesaiSampai, "
            + "tanggalBikin, jenis, progressTabungan, dompetId) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE batasuang SET idKategori = ?, maxUang = ?, mulaiDari = ?, selesaiSampai = ?, "
            + "progressTabungan = ?, dompetId = ? WHERE idBatas = ?";

    private static final String UPDATE_PROGRESS = "UPDATE batasuang SET progressTabungan = ? WHERE idBatas = ?";

    private static final String DELETE = "DELETE FROM batasuang WHERE idBatas = ?";

    private static final String GET_SPENDING = "SELECT COALESCE(SUM(nominal), 0) AS total "
            + "FROM catatanuang "
            + "WHERE idUser = ? AND idKategori = ? AND jenisCatatan = 'Pengeluaran' "
            + "AND tanggalCatat BETWEEN ? AND ?";

    @SuppressWarnings("CallToPrintStackTrace")
    public List<BatasUang> ambilBudgetingByUserId(int userId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SELECT_BUDGETING_BY_USER);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetBudgeting(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<BatasUang> ambilBudgetingAktif(int userId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SELECT_BUDGETING_AKTIF);
            st.setInt(1, userId);
            st.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                BatasUang bu = mapResultSetBudgeting(rs);
                double spending = getCurrentSpending(userId, bu.getKategoriId(),
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

    public List<BatasUang> ambilBudgetMelebihi(int userId) {
        List<BatasUang> exceeded = new ArrayList<>();
        List<BatasUang> aktif = ambilBudgetingAktif(userId);
        for (BatasUang bu : aktif) {
            if (bu.isMelebihi()) {
                exceeded.add(bu);
            }
        }
        return exceeded;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public double getCurrentSpending(int userId, int kategoriId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            return 0;
        try {
            con = getCon();
            st = con.prepareStatement(GET_SPENDING);
            st.setInt(1, userId);
            st.setInt(2, kategoriId);
            st.setDate(3, Date.valueOf(startDate));
            st.setDate(4, Date.valueOf(endDate));
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
    public List<BatasUang> ambilGoalsByUserId(int userId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SELECT_GOALS_BY_USER);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetGoals(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<BatasUang> ambilGoalsAktif(int userId) {
        List<BatasUang> list = new ArrayList<>();
        try {
            con = getCon();
            st = con.prepareStatement(SELECT_GOALS_AKTIF);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetGoals(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return list;
    }

    public List<BatasUang> ambilGoalsTercapai(int userId) {
        List<BatasUang> completed = new ArrayList<>();
        List<BatasUang> all = ambilGoalsByUserId(userId);
        for (BatasUang g : all) {
            if (g.goalsTercapai()) {
                completed.add(g);
            }
        }
        return completed;
    }

    public void syncGoalsProgressWithBalance(BatasUang goals) {
        double adjustedProgress = goals.hitungProgressAktual();
        if (adjustedProgress != goals.getProgressTabungan()) {
            updateProgress(goals.getId(), adjustedProgress);
            goals.setProgressTabungan(adjustedProgress);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public BatasUang getById(int id) {
        try {
            con = getCon();
            st = con.prepareStatement(SELECT_BY_ID);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String jenis = rs.getString("jenis");
                if ("BUDGETING".equals(jenis)) {
                    return mapResultSetBudgeting(rs);
                } else {
                    return mapResultSetGoals(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tambah(BatasUang batasUang) {
        try {
            con = getCon();
            st = con.prepareStatement(INSERT);
            st.setInt(1, batasUang.getUserId());
            st.setInt(2, batasUang.getKategoriId());
            st.setDouble(3, batasUang.getMaxUang());

            if (batasUang.getMulaiDari() != null) {
                st.setDate(4, Date.valueOf(batasUang.getMulaiDari()));
            } else {
                st.setNull(4, Types.DATE);
            }

            if (batasUang.getSelesaiSampai() != null) {
                st.setDate(5, Date.valueOf(batasUang.getSelesaiSampai()));
            } else {
                st.setNull(5, Types.DATE);
            }

            st.setTimestamp(6, Timestamp.valueOf(batasUang.getTanggalBikin()));
            st.setString(7, batasUang.getJenis());
            st.setDouble(8, batasUang.getProgressTabungan());

            if (batasUang.getDompetId() > 0) {
                st.setInt(9, batasUang.getDompetId());
            } else {
                st.setNull(9, Types.INTEGER);
            }

            boolean success = st.executeUpdate() > 0;
            if (success) {
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean ubah(BatasUang batasUang) {
        try {
            con = getCon();
            st = con.prepareStatement(UPDATE);
            st.setInt(1, batasUang.getKategoriId());
            st.setDouble(2, batasUang.getMaxUang());

            if (batasUang.getMulaiDari() != null) {
                st.setDate(3, Date.valueOf(batasUang.getMulaiDari()));
            } else {
                st.setNull(3, Types.DATE);
            }

            if (batasUang.getSelesaiSampai() != null) {
                st.setDate(4, Date.valueOf(batasUang.getSelesaiSampai()));
            } else {
                st.setNull(4, Types.DATE);
            }

            st.setDouble(5, batasUang.getProgressTabungan());

            if (batasUang.getDompetId() > 0) {
                st.setInt(6, batasUang.getDompetId());
            } else {
                st.setNull(6, Types.INTEGER);
            }

            st.setInt(7, batasUang.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateProgress(int id, double newProgress) {
        try {
            con = getCon();
            st = con.prepareStatement(UPDATE_PROGRESS);
            st.setDouble(1, newProgress);
            st.setInt(2, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean hapus(int id) {
        try {
            con = getCon();
            st = con.prepareStatement(DELETE);
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    private BatasUang mapResultSetBudgeting(ResultSet rs) throws SQLException {
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

    private BatasUang mapResultSetGoals(ResultSet rs) throws SQLException {
        BatasUang bu = mapResultSetBudgeting(rs);
        bu.setNamaDompet(rs.getString("namaDompet"));
        bu.setSaldoDompet(rs.getDouble("saldoSekarang"));
        return bu;
    }
}


