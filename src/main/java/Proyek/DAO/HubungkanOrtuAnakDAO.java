package Proyek.DAO;

import static Proyek.DAO.BaseDAO.closeCon;
import static Proyek.DAO.BaseDAO.getCon;
import Proyek.Model.HubungkanOrtuAnak;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class HubungkanOrtuAnakDAO {
    private static final String SQL_GET_CODE = "SELECT kodeAkses FROM ortuanaklink WHERE idAnak = ? AND (idOrtu IS NULL OR idOrtu = 0) ORDER BY idLink DESC LIMIT 1";

    private static final String SQL_INSERT_CODE = "INSERT INTO ortuanaklink (idAnak, kodeAkses, aktif) VALUES (?, ?, 0)";

    private static final String SQL_DELETE_OLD = "DELETE FROM ortuanaklink WHERE idAnak = ? AND (idOrtu IS NULL OR idOrtu = 0)";

    private static final String SQL_FIND_BY_CODE = "SELECT oal.idAnak, p.namaLengkap FROM ortuanaklink oal " +
            "JOIN pengguna p ON oal.idAnak = p.idUser " +
            "WHERE oal.kodeAkses = ? AND (oal.idOrtu IS NULL OR oal.idOrtu = 0) LIMIT 1";

    private static final String SQL_CONNECT = "UPDATE ortuanaklink SET idOrtu = ?, aktif = 1 WHERE kodeAkses = ?";

    private static final String SQL_CHECK_LINK = "SELECT idLink FROM ortuanaklink WHERE idOrtu = ? AND idAnak = ? AND aktif = 1";
    private static final String SQL_GET_CONNECTED_CHILDREN = "SELECT oal.idLink, oal.idAnak, p.namaLengkap, p.foto " +
            "FROM ortuanaklink oal " +
            "JOIN pengguna p ON oal.idAnak = p.idUser " +
            "WHERE oal.idOrtu = ? AND oal.aktif = 1 " +
            "ORDER BY p.namaLengkap";

    private static final String SQL_DISCONNECT = "DELETE FROM ortuanaklink WHERE idOrtu = ? AND idAnak = ?";
    public String getChildCode(int childId) {
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_GET_CODE);
            ps.setInt(1, childId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("kodeAkses");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return null;
    }
    public String generateCode(int childId) {
        Connection conn = null;
        String newCode = createRandomCode();

        try {
            conn = getCon();
            if (conn == null) {
                return null;
            }
            PreparedStatement delPs = conn.prepareStatement(SQL_DELETE_OLD);
            delPs.setInt(1, childId);
            delPs.executeUpdate();
            delPs.close();
            PreparedStatement insPs = conn.prepareStatement(SQL_INSERT_CODE);
            insPs.setInt(1, childId);
            insPs.setString(2, newCode);
            int rows = insPs.executeUpdate();
            insPs.close();

            if (rows > 0) {
                return newCode;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(conn);
        }
        return null;
    }
    public int findChildByCode(String code) {
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idAnak");
            }
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return -1;
    }
    public boolean connectParent(int parentId, String code) {
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_CONNECT);
            ps.setInt(1, parentId);
            ps.setString(2, code);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return false;
    }
    public boolean isAlreadyConnected(int parentId, int childId) {
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_CHECK_LINK);
            ps.setInt(1, parentId);
            ps.setInt(2, childId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return false;
    }
    public List<ChildInfo> getConnectedChildren(int parentId) {
        List<ChildInfo> children = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_GET_CONNECTED_CHILDREN);
            ps.setInt(1, parentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChildInfo child = new ChildInfo();
                child.idAnak = rs.getInt("idAnak");
                child.namaLengkap = rs.getString("namaLengkap");
                child.foto = rs.getString("foto");
                children.add(child);
            }
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return children;
    }
    public boolean disconnectChild(int parentId, int childId) {
        Connection conn = null;
        try {
            conn = getCon();
            PreparedStatement ps = conn.prepareStatement(SQL_DISCONNECT);
            ps.setInt(1, parentId);
            ps.setInt(2, childId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
        } finally {
            closeCon(conn);
        }
        return false;
    }

    private String createRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Avoid confusing chars: 0,O,1,I
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
    public static class ChildInfo {
        public int idAnak;
        public String namaLengkap;
        public String foto;
    }
}


