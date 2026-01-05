package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.Dompet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class DompetDAO {

    private static PreparedStatement st;
    private static Connection con;

    private static final String SELECT_BY_ID = "SELECT idDompet, idUser, namaDompet, jenisDompet, saldoSekarang, " +
            "targetSaldo, deadlineTabung, dibikinKapan, icon, utamaEnggak " +
            "FROM Dompet WHERE idDompet = ?";

    private static final String SELECT_BY_USER_ID = "SELECT idDompet, idUser, namaDompet, jenisDompet, saldoSekarang, "
            +
            "targetSaldo, deadlineTabung, dibikinKapan, icon, utamaEnggak " +
            "FROM Dompet WHERE idUser = ?";

    private static final String SELECT_ALL = "SELECT idDompet, idUser, namaDompet, jenisDompet, saldoSekarang, " +
            "targetSaldo, deadlineTabung, dibikinKapan, icon, utamaEnggak " +
            "FROM Dompet";

    private static final String SELECT_MAIN_BY_USER = "SELECT idDompet, idUser, namaDompet, jenisDompet, saldoSekarang, "
            +
            "targetSaldo, deadlineTabung, dibikinKapan, icon, utamaEnggak " +
            "FROM Dompet WHERE idUser = ? AND utamaEnggak = 1";

    private static final String SELECT_SAVINGS_BY_USER = "SELECT idDompet, idUser, namaDompet, jenisDompet, saldoSekarang, "
            +
            "targetSaldo, deadlineTabung, dibikinKapan, icon, utamaEnggak " +
            "FROM Dompet WHERE idUser = ? AND utamaEnggak = 0";

    private static final String INSERT_DOMPET = "INSERT INTO Dompet (idUser, namaDompet, jenisDompet, saldoSekarang, icon, utamaEnggak) "
            +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_DOMPET = "UPDATE Dompet SET namaDompet = ?, jenisDompet = ?, saldoSekarang = ?, "
            +
            "targetSaldo = ?, deadlineTabung = ?, icon = ?, utamaEnggak = ? " +
            "WHERE idDompet = ?";

    private static final String UPDATE_SALDO = "UPDATE Dompet SET saldoSekarang = ? WHERE idDompet = ?";

    private static final String DELETE_DOMPET = "DELETE FROM Dompet WHERE idDompet = ?";

    private static final String RESET_MAIN_DOMPET = "UPDATE Dompet SET utamaEnggak = 0 WHERE idUser = ? AND utamaEnggak = 1";

    private static final String SET_AS_MAIN = "UPDATE Dompet SET utamaEnggak = 1 WHERE idDompet = ?";

    private static final String REMOVE_AS_MAIN = "UPDATE Dompet SET utamaEnggak = 0 WHERE idDompet = ?";
    public Dompet ambilDompetById(int id) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return null;
        }

        Dompet dompet = null;

        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                dompet = mapResultSetToDompet(rs);
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }

        return dompet;
    }
    public List<Dompet> ambilDompetByUserId(int userId) {
        Connection connection = BaseDAO.getCon();
        List<Dompet> dompetList = new ArrayList<>();

        if (connection == null) {
            return dompetList;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_USER_ID)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dompet dompet = mapResultSetToDompet(rs);
                dompetList.add(dompet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }

        return dompetList;
    }
    public List<Dompet> getAllDompet() {
        Connection connection = BaseDAO.getCon();
        List<Dompet> dompetList = new ArrayList<>();

        if (connection == null) {
            return dompetList;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dompet dompet = mapResultSetToDompet(rs);
                dompetList.add(dompet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }

        return dompetList;
    }
    public Dompet getMainDompetByUser(int userId) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return null;
        }

        Dompet dompet = null;

        try (PreparedStatement ps = connection.prepareStatement(SELECT_MAIN_BY_USER)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                dompet = mapResultSetToDompet(rs);
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }

        return dompet;
    }
    public List<Dompet> getSavingsDompetByUser(int userId) {
        Connection connection = BaseDAO.getCon();
        List<Dompet> dompetList = new ArrayList<>();

        if (connection == null) {
            return dompetList;
        }

        try (PreparedStatement ps = connection.prepareStatement(SELECT_SAVINGS_BY_USER)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dompet dompet = mapResultSetToDompet(rs);
                dompetList.add(dompet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }

        return dompetList;
    }
    public boolean tambahDompet(Dompet dompet) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(INSERT_DOMPET,
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dompet.getUserId());
            ps.setString(2, dompet.getNamaDompet());
            ps.setString(3, dompet.getJenisDompet());
            ps.setDouble(4, dompet.getSaldoSekarang());
            if (dompet.getIcon() != null) {
                ps.setBytes(5, dompet.getIcon());
            } else {
                ps.setNull(5, Types.BLOB);
            }
            ps.setInt(6, dompet.isUtamaEnggak() ? 1 : 0);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    dompet.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err
                    .println("  5. icon: " + (dompet.getIcon() != null ? dompet.getIcon().length + " bytes" : "null"));
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
    public boolean ubahDompet(Dompet dompet) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_DOMPET)) {

            ps.setString(1, dompet.getNamaDompet());
            ps.setString(2, dompet.getJenisDompet());
            ps.setDouble(3, dompet.getSaldoSekarang());
            if (dompet.getTargetSaldo() != null) {
                ps.setDouble(4, dompet.getTargetSaldo());
            } else {
                ps.setNull(4, Types.DOUBLE);
            }
            if (dompet.getDeadlineTabung() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(dompet.getDeadlineTabung()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            if (dompet.getIcon() != null) {
                ps.setBytes(6, dompet.getIcon());
            } else {
                ps.setNull(6, Types.BLOB);
            }
            ps.setInt(7, dompet.isUtamaEnggak() ? 1 : 0);

            ps.setInt(8, dompet.getId());

            boolean success = ps.executeUpdate() > 0;
            if (success) {
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
    public boolean ubahSaldo(int dompetId, double saldoBaru) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(UPDATE_SALDO)) {

            ps.setDouble(1, saldoBaru);
            ps.setInt(2, dompetId);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                System.out
                        .println("✓ Saldo dompet berhasil diupdate. ID: " + dompetId + ", Saldo baru: Rp " + saldoBaru);
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
    public boolean hapusDompet(int id) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(DELETE_DOMPET)) {

            ps.setInt(1, id);

            boolean success = ps.executeUpdate() > 0;
            if (success) {
            } else {
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
    private Dompet mapResultSetToDompet(ResultSet rs) throws SQLException {
        int id = rs.getInt("idDompet");
        int userId = rs.getInt("idUser");
        String namaDompet = rs.getString("namaDompet");
        String jenisDompet = rs.getString("jenisDompet");
        double saldoSekarang = rs.getDouble("saldoSekarang");
        Double targetSaldo = rs.getDouble("targetSaldo");
        if (rs.wasNull()) {
            targetSaldo = null;
        }
        Date deadlineDate = rs.getDate("deadlineTabung");
        LocalDateTime deadlineTabung = (deadlineDate != null) ? deadlineDate.toLocalDate().atStartOfDay() : null;
        Timestamp dibikinTs = rs.getTimestamp("dibikinKapan");
        LocalDateTime dibikinKapan = (dibikinTs != null) ? dibikinTs.toLocalDateTime() : LocalDateTime.now();
        byte[] icon = rs.getBytes("icon");
        boolean utamaEnggak = rs.getInt("utamaEnggak") == 1;

        Dompet dompet = new Dompet(id, userId, namaDompet, jenisDompet, saldoSekarang,
                targetSaldo, deadlineTabung, dibikinKapan);
        dompet.setIcon(icon);
        dompet.setUtamaEnggak(utamaEnggak);
        return dompet;
    }
    public boolean setAsMainDompet(int dompetId, int userId) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement psReset = connection.prepareStatement(RESET_MAIN_DOMPET)) {
                psReset.setInt(1, userId);
                psReset.executeUpdate();
            }
            try (PreparedStatement psSet = connection.prepareStatement(SET_AS_MAIN)) {
                psSet.setInt(1, dompetId);
                int affected = psSet.executeUpdate();

                if (affected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BaseDAO.closeCon(connection);
        }
    }
    public boolean removeAsMainDompet(int dompetId) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(REMOVE_AS_MAIN)) {
            ps.setInt(1, dompetId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
            }
            return success;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
    public void recalculateSaldoFromTransactions(int userId) {
        Connection connection = BaseDAO.getCon();
        if (connection == null) {
            return;
        }

        try {
            List<Dompet> wallets = ambilDompetByUserId(userId);

            for (Dompet wallet : wallets) {
                int dompetId = wallet.getId();
                String sqlIncome = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                        "WHERE idDompet = ? AND jenisCatatan = 'Pemasukan'";
                String sqlExpense = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                        "WHERE idDompet = ? AND jenisCatatan = 'Pengeluaran'";
                String sqlTransferOut = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                        "WHERE idDompet = ? AND jenisCatatan = 'Transfer'";
                String sqlTransferIn = "SELECT COALESCE(SUM(nominal), 0) AS total FROM catatanuang " +
                        "WHERE idDompetTujuan = ? AND jenisCatatan = 'Transfer'";

                double income = 0, expense = 0, transferOut = 0, transferIn = 0;

                try (PreparedStatement psIncome = connection.prepareStatement(sqlIncome)) {
                    psIncome.setInt(1, dompetId);
                    ResultSet rs = psIncome.executeQuery();
                    if (rs.next()) {
                        income = rs.getDouble("total");
                    }
                }

                try (PreparedStatement psExpense = connection.prepareStatement(sqlExpense)) {
                    psExpense.setInt(1, dompetId);
                    ResultSet rs = psExpense.executeQuery();
                    if (rs.next()) {
                        expense = rs.getDouble("total");
                    }
                }

                try (PreparedStatement psTransferOut = connection.prepareStatement(sqlTransferOut)) {
                    psTransferOut.setInt(1, dompetId);
                    ResultSet rs = psTransferOut.executeQuery();
                    if (rs.next()) {
                        transferOut = rs.getDouble("total");
                    }
                }

                try (PreparedStatement psTransferIn = connection.prepareStatement(sqlTransferIn)) {
                    psTransferIn.setInt(1, dompetId);
                    ResultSet rs = psTransferIn.executeQuery();
                    if (rs.next()) {
                        transferIn = rs.getDouble("total");
                    }
                }
                double newSaldo = income - expense - transferOut + transferIn;
                ubahSaldo(dompetId, newSaldo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDAO.closeCon(connection);
        }
    }
}


