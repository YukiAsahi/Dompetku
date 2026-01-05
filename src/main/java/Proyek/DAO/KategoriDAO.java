package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.Kategori;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    private static PreparedStatement st;
    private static Connection con;

    // Query dengan filter idPengguna - ambil kategori user + kategori default
    // (idPengguna = 0)
    private static final String SELECT_BY_ID = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE idKategori = ?";

    private static final String SELECT_ALL_BY_USER = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE (idPengguna = ? OR idPengguna = 0) ORDER BY namaKategori";

    private static final String SELECT_AKTIF_BY_USER = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE aktif = TRUE AND (idPengguna = ? OR idPengguna = 0) ORDER BY namaKategori";

    private static final String SELECT_BY_JENIS_AND_USER = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE tipeKategori = ? AND aktif = TRUE AND (idPengguna = ? OR idPengguna = 0) ORDER BY namaKategori";

    private static final String SELECT_INCOME_BY_USER = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE tipeKategori = 'Pemasukan' AND aktif = TRUE AND (idPengguna = ? OR idPengguna = 0) ORDER BY namaKategori";

    private static final String SELECT_EXPENSE_BY_USER = "SELECT idKategori, idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah "
            + "FROM kategori WHERE tipeKategori = 'Pengeluaran' AND aktif = TRUE AND (idPengguna = ? OR idPengguna = 0) ORDER BY namaKategori";

    private static final String INSERT_KATEGORI = "INSERT INTO kategori (idPengguna, namaKategori, tipeKategori, ikon, aktif, tanggalTambah) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_KATEGORI = "UPDATE kategori SET namaKategori = ?, tipeKategori = ?, ikon = ? "
            + "WHERE idKategori = ? AND idPengguna = ?";

    private static final String UPDATE_STATUS = "UPDATE kategori SET aktif = ? WHERE idKategori = ?";

    private static final String DELETE_KATEGORI = "DELETE FROM kategori WHERE idKategori = ? AND idPengguna = ?";

    private static final String CHECK_NAMA_EXISTS_BY_USER = "SELECT COUNT(*) FROM kategori WHERE namaKategori = ? AND tipeKategori = ? AND (idPengguna = ? OR idPengguna = 0)";

    @SuppressWarnings("CallToPrintStackTrace")
    public Kategori ambilKategoriById(int id) {
        Kategori kategori = null;
        try {
            con = getCon();
            if (con == null) {
                return null;
            }

            st = con.prepareStatement(SELECT_BY_ID);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                kategori = mapResultSetToKategori(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategori;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Kategori> ambilSemuaKategori(int idPengguna) {
        List<Kategori> kategoriList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return kategoriList;
            }

            st = con.prepareStatement(SELECT_ALL_BY_USER);
            st.setInt(1, idPengguna);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Kategori kategori = mapResultSetToKategori(rs);
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategoriList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Kategori> ambilKategoriAktif(int idPengguna) {
        List<Kategori> kategoriList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return kategoriList;
            }

            st = con.prepareStatement(SELECT_AKTIF_BY_USER);
            st.setInt(1, idPengguna);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Kategori kategori = mapResultSetToKategori(rs);
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategoriList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Kategori> ambilKategoriByJenis(String jenisKategori, int idPengguna) {
        List<Kategori> kategoriList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return kategoriList;
            }

            st = con.prepareStatement(SELECT_BY_JENIS_AND_USER);
            String tipe = convertJenisToTipe(jenisKategori);
            st.setString(1, tipe);
            st.setInt(2, idPengguna);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Kategori kategori = mapResultSetToKategori(rs);
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategoriList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Kategori> ambilKategoriPemasukan(int idPengguna) {
        List<Kategori> kategoriList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return kategoriList;
            }

            st = con.prepareStatement(SELECT_INCOME_BY_USER);
            st.setInt(1, idPengguna);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Kategori kategori = mapResultSetToKategori(rs);
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategoriList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Kategori> ambilKategoriPengeluaran(int idPengguna) {
        List<Kategori> kategoriList = new ArrayList<>();
        try {
            con = getCon();
            if (con == null) {
                return kategoriList;
            }

            st = con.prepareStatement(SELECT_EXPENSE_BY_USER);
            st.setInt(1, idPengguna);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Kategori kategori = mapResultSetToKategori(rs);
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return kategoriList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tambahKategori(Kategori kategori) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(INSERT_KATEGORI, PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, kategori.getIdPengguna());
            st.setString(2, kategori.getNamaKategori());
            String tipe = convertJenisToTipe(kategori.getJenisKategori());
            st.setString(3, tipe);

            if (kategori.getIkon() != null && kategori.getIkon().length > 0) {
                st.setBytes(4, kategori.getIkon());
            } else {
                st.setNull(4, java.sql.Types.BLOB);
            }

            st.setBoolean(5, kategori.isAktif());
            st.setTimestamp(6, Timestamp.valueOf(kategori.getTanggalTambah()));

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    kategori.setId(generatedKeys.getInt(1));
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
    public boolean ubahKategori(Kategori kategori) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(UPDATE_KATEGORI);
            st.setString(1, kategori.getNamaKategori());
            String tipe = convertJenisToTipe(kategori.getJenisKategori());
            st.setString(2, tipe);

            if (kategori.getIkon() != null && kategori.getIkon().length > 0) {
                st.setBytes(3, kategori.getIkon());
            } else {
                st.setNull(3, java.sql.Types.BLOB);
            }

            st.setInt(4, kategori.getId());
            st.setInt(5, kategori.getIdPengguna());

            boolean success = st.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateStatusKategori(int id, boolean aktif) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(UPDATE_STATUS);
            st.setBoolean(1, aktif);
            st.setInt(2, id);

            boolean success = st.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    public boolean nonaktifkanKategori(int id) {
        return updateStatusKategori(id, false);
    }

    public boolean aktifkanKategori(int id) {
        return updateStatusKategori(id, true);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean hapusKategori(int id, int idPengguna) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(DELETE_KATEGORI);
            st.setInt(1, id);
            st.setInt(2, idPengguna);

            boolean success = st.executeUpdate() > 0;
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeCon(con);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean isNamaKategoriExists(String namaKategori, String jenisKategori, int idPengguna) {
        try {
            con = getCon();
            if (con == null) {
                return false;
            }

            st = con.prepareStatement(CHECK_NAMA_EXISTS_BY_USER);
            st.setString(1, namaKategori);
            String tipe = convertJenisToTipe(jenisKategori);
            st.setString(2, tipe);
            st.setInt(3, idPengguna);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(con);
        }
        return false;
    }

    private String convertJenisToTipe(String jenis) {
        if (jenis == null)
            return "Pengeluaran";
        if (jenis.equalsIgnoreCase("INCOME") || jenis.equalsIgnoreCase("Pemasukan")
                || jenis.equalsIgnoreCase("Pemasukkan")) {
            return "Pemasukan";
        }
        return "Pengeluaran";
    }

    private String convertTipeToJenis(String tipe) {
        if (tipe == null)
            return "Pengeluaran";
        if (tipe.equalsIgnoreCase("Pemasukan") || tipe.equalsIgnoreCase("Pemasukkan")) {
            return "Pemasukan";
        }
        return "Pengeluaran";
    }

    private Kategori mapResultSetToKategori(ResultSet rs) throws SQLException {
        int id = rs.getInt("idKategori");
        int idPengguna = rs.getInt("idPengguna");
        String namaKategori = rs.getString("namaKategori");
        String tipeKategori = rs.getString("tipeKategori");
        String jenisKategori = convertTipeToJenis(tipeKategori);
        byte[] ikon = rs.getBytes("ikon");
        boolean aktif = rs.getBoolean("aktif");

        Timestamp tanggalTs = rs.getTimestamp("tanggalTambah");
        LocalDateTime tanggalTambah = (tanggalTs != null) ? tanggalTs.toLocalDateTime() : LocalDateTime.now();

        return new Kategori(id, idPengguna, namaKategori, jenisKategori, ikon, aktif, tanggalTambah);
    }
}
