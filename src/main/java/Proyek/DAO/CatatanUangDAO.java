package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.CatatanUang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class CatatanUangDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SELECT_BY_ID = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idTransaksi = ?";

    private static final String SELECT_BY_USER = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idUser = ? ORDER BY c.tanggalCatat DESC";

    private static final String SELECT_BY_DOMPET = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idDompet = ? ORDER BY c.tanggalCatat DESC";

    private static final String SELECT_BY_USER_AND_JENIS = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idUser = ? AND c.jenisCatatan = ? ORDER BY c.tanggalCatat DESC";

    private static final String SELECT_BY_USER_AND_KATEGORI = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idUser = ? AND c.idKategori = ? ORDER BY c.tanggalCatat DESC";

    private static final String SELECT_BY_USER_AND_DATE_RANGE = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
            + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
            + "FROM catatanuang c "
            + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
            + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
            + "WHERE c.idUser = ? AND c.tanggalCatat BETWEEN ? AND ? ORDER BY c.tanggalCatat DESC";

    private static final String INSERT_CATATAN = "INSERT INTO catatanuang (idDompet, idUser, jenisCatatan, idKategori, nominal, catatan, tanggalCatat) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_CATATAN = "UPDATE catatanuang SET idDompet = ?, jenisCatatan = ?, idKategori = ?, "
            + "nominal = ?, catatan = ?, tanggalCatat = ? WHERE idTransaksi = ?";

    private static final String DELETE_CATATAN = "DELETE FROM catatanuang WHERE idTransaksi = ?";

    private static final String SUM_BY_USER_AND_JENIS = "SELECT COALESCE(SUM(nominal), 0) FROM catatanuang WHERE idUser = ? AND jenisCatatan = ?";

    private static final String SUM_BY_DOMPET_AND_JENIS = "SELECT COALESCE(SUM(nominal), 0) FROM catatanuang WHERE idDompet = ? AND jenisCatatan = ?";

    private static final String SUM_BY_DOMPET_AND_JENIS_MONTHLY = "SELECT COALESCE(SUM(nominal), 0) FROM catatanuang "
            + "WHERE idDompet = ? AND jenisCatatan = ? AND YEAR(tanggalCatat) = ? AND MONTH(tanggalCatat) = ?";

    private static final String SUM_BY_USER_KATEGORI = "SELECT COALESCE(SUM(nominal), 0) FROM catatanuang WHERE idUser = ? AND idKategori = ?";

    private static final String COUNT_BY_USER = "SELECT COUNT(*) FROM catatanuang WHERE idUser = ?";

    @SuppressWarnings("CallToPrintStackTrace")
    public CatatanUang ambilCatatanById(int id) {
        CatatanUang catatan = null;
        try {
            con = getCon();
            if (con == null) {
                return null;
            }

            st = con.prepareStatement(SELECT_BY_ID);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                catatan = mapResultSetToCatatan(rs);
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatan;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> ambilCatatanByUserId(int userId) {
        List<CatatanUang> catatanList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(SELECT_BY_USER);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> ambilCatatanByDompetId(int dompetId) {
        List<CatatanUang> catatanList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(SELECT_BY_DOMPET);
            st.setInt(1, dompetId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> getCatatanByUserAndJenis(int userId, String jenisCatatan) {
        List<CatatanUang> catatanList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(SELECT_BY_USER_AND_JENIS);
            st.setInt(1, userId);
            st.setString(2, jenisCatatan);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    public List<CatatanUang> getCatatanIncomeByUser(int userId) {
        return getCatatanByUserAndJenis(userId, "INCOME");
    }

    public List<CatatanUang> getCatatanExpenseByUser(int userId) {
        return getCatatanByUserAndJenis(userId, "EXPENSE");
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> ambilCatatanSorted(int userId, String sortBy, boolean ascending) {
        List<CatatanUang> catatanList = new ArrayList<>();

        String orderColumn;
        switch (sortBy) {
            case "tanggal":
                orderColumn = "c.tanggalCatat";
                break;
            case "kategori":
                orderColumn = "k.namaKategori";
                break;
            case "dompet":
                orderColumn = "d.namaDompet";
                break;
            case "nominal":
                orderColumn = "c.nominal";
                break;
            default:
                orderColumn = "c.tanggalCatat";
        }

        String direction = ascending ? "ASC" : "DESC";

        String query = "SELECT c.idTransaksi AS id, c.idDompet AS dompetId, c.idUser AS userId, c.jenisCatatan, c.idKategori AS kategoriId, c.nominal, "
                + "c.catatan, c.tanggalCatat, k.namaKategori, d.namaDompet "
                + "FROM catatanuang c "
                + "LEFT JOIN kategori k ON c.idKategori = k.idKategori "
                + "LEFT JOIN dompet d ON c.idDompet = d.idDompet "
                + "WHERE c.idUser = ? ORDER BY " + orderColumn + " " + direction;

        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(query);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> getCatatanByUserAndKategori(int userId, int kategoriId) {
        List<CatatanUang> catatanList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(SELECT_BY_USER_AND_KATEGORI);
            st.setInt(1, userId);
            st.setInt(2, kategoriId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<CatatanUang> ambilCatatanByTanggal(int userId, LocalDate startDate, LocalDate endDate) {
        List<CatatanUang> catatanList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return catatanList;
            }

            st = con.prepareStatement(SELECT_BY_USER_AND_DATE_RANGE);
            st.setInt(1, userId);
            st.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            st.setTimestamp(3, Timestamp.valueOf(endDate.atTime(23, 59, 59)));
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CatatanUang catatan = mapResultSetToCatatan(rs);
                catatanList.add(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return catatanList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tambahCatatan(CatatanUang catatan) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(INSERT_CATATAN, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, catatan.getDompetId());
            st.setInt(2, catatan.getUserId());
            st.setString(3, catatan.getJenisCatatan());

            if (catatan.getKategoriId() > 0) {
                st.setInt(4, catatan.getKategoriId());
            } else {
                st.setNull(4, Types.INTEGER);
            }

            st.setDouble(5, catatan.getNominal());
            st.setString(6, catatan.getCatatan());
            st.setTimestamp(7, Timestamp.valueOf(catatan.getTanggalCatat()));

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    catatan.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean ubahCatatan(CatatanUang catatan) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(UPDATE_CATATAN);
            st.setInt(1, catatan.getDompetId());
            st.setString(2, catatan.getJenisCatatan());

            if (catatan.getKategoriId() > 0) {
                st.setInt(3, catatan.getKategoriId());
            } else {
                st.setNull(3, Types.INTEGER);
            }

            st.setDouble(4, catatan.getNominal());
            st.setString(5, catatan.getCatatan());
            st.setTimestamp(6, Timestamp.valueOf(catatan.getTanggalCatat()));
            st.setInt(7, catatan.getId());

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
    public boolean hapusCatatan(int id) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(DELETE_CATATAN);
            st.setInt(1, id);

            boolean success = st.executeUpdate() > 0;
            if (success) {
            } else {
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    public double hitungTotalPemasukan(int userId) {
        return getTotalByUserAndJenis(userId, "INCOME");
    }

    public double hitungTotalPengeluaran(int userId) {
        return getTotalByUserAndJenis(userId, "EXPENSE");
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private double getTotalByUserAndJenis(int userId, String jenisCatatan) {
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(SUM_BY_USER_AND_JENIS);
            st.setInt(1, userId);
            st.setString(2, jenisCatatan);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }

    public double getTotalIncomeByDompet(int dompetId) {
        return getTotalByDompetAndJenis(dompetId, "INCOME");
    }

    public double getTotalExpenseByDompet(int dompetId) {
        return getTotalByDompetAndJenis(dompetId, "EXPENSE");
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private double getTotalByDompetAndJenis(int dompetId, String jenisCatatan) {
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(SUM_BY_DOMPET_AND_JENIS);
            st.setInt(1, dompetId);
            st.setString(2, jenisCatatan);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }

    public double getMonthlyIncomeByDompet(int dompetId, int year, int month) {
        return getMonthlyTotalByDompetAndJenis(dompetId, "Pemasukan", year, month);
    }

    public double getMonthlyExpenseByDompet(int dompetId, int year, int month) {
        return getMonthlyTotalByDompetAndJenis(dompetId, "Pengeluaran", year, month);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private double getMonthlyTotalByDompetAndJenis(int dompetId, String jenisCatatan, int year, int month) {
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(SUM_BY_DOMPET_AND_JENIS_MONTHLY);
            st.setInt(1, dompetId);
            st.setString(2, jenisCatatan);
            st.setInt(3, year);
            st.setInt(4, month);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public double getTotalByKategori(int userId, int kategoriId) {
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(SUM_BY_USER_KATEGORI);
            st.setInt(1, userId);
            st.setInt(2, kategoriId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return 0;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public int getCountCatatanByUser(int userId) {
        try {
            con = getCon();
            if (con == null) {
                return 0;
            }

            st = con.prepareStatement(COUNT_BY_USER);
            st.setInt(1, userId);
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

    private CatatanUang mapResultSetToCatatan(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int dompetId = rs.getInt("dompetId");
        int userId = rs.getInt("userId");
        String jenisCatatan = rs.getString("jenisCatatan");
        int kategoriId = rs.getInt("kategoriId");
        double nominal = rs.getDouble("nominal");
        String catatan = rs.getString("catatan");

        Timestamp tanggalTs = rs.getTimestamp("tanggalCatat");
        LocalDateTime tanggalCatat = (tanggalTs != null) ? tanggalTs.toLocalDateTime() : LocalDateTime.now();

        CatatanUang catatanUang = new CatatanUang(id, dompetId, userId, jenisCatatan,
                kategoriId, nominal, catatan, tanggalCatat);

        catatanUang.setKategoriNama(rs.getString("namaKategori"));
        catatanUang.setDompetNama(rs.getString("namaDompet"));

        return catatanUang;
    }
}


