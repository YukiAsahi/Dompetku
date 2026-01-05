package Proyek;

import Proyek.Model.Pengguna;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
public class Session {

    private static Pengguna pengguna;
    private static Image cachedProfileImage;
    private static int connectedChildId = -1;
    private static String connectedChildName = null;

    public static void setPengguna(Pengguna p) {
        pengguna = p;
        cachedProfileImage = null;
        connectedChildId = -1;
        connectedChildName = null;
    }

    public static Pengguna ambilPengguna() {
        return pengguna;
    }
    public static void setConnectedChild(int childId, String childName) {
        connectedChildId = childId;
        connectedChildName = childName;
    }

    public static int getConnectedChildId() {
        return connectedChildId;
    }

    public static String getConnectedChildName() {
        return connectedChildName;
    }

    public static boolean hasConnectedChild() {
        return connectedChildId > 0;
    }

    public static void clearConnectedChild() {
        connectedChildId = -1;
        connectedChildName = null;
    }
    public static Image getProfileImage() {
        if (cachedProfileImage == null && pengguna != null) {
            byte[] fotoBytes = pengguna.getFoto();
            if (fotoBytes != null && fotoBytes.length > 0) {
                cachedProfileImage = new Image(new ByteArrayInputStream(fotoBytes));
            }
        }
        return cachedProfileImage;
    }
    public static void updateProfileImage(byte[] fotoBytes) {
        if (fotoBytes != null && fotoBytes.length > 0) {
            cachedProfileImage = new Image(new ByteArrayInputStream(fotoBytes));
        } else {
            cachedProfileImage = null;
        }
    }

    public static void clear() {
        pengguna = null;
        cachedProfileImage = null;
        connectedChildId = -1;
        connectedChildName = null;
    }
}


