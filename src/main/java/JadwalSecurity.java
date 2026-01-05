import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JadwalSecurity {
    private final StringProperty nama;
    private final ObjectProperty<LocalDate> tgl;

    public JadwalSecurity(String nama, LocalDate tgl) {
        this.nama = new SimpleStringProperty(nama);
        this.tgl = new SimpleObjectProperty<>(tgl);
    }

    public String getNama() {
        return nama.get();
    }

    public void setNama(String nama) {
        this.nama.set(nama);
    }

    public StringProperty namaProp() {
        return nama;
    }

    public LocalDate getTgl() {
        return tgl.get();
    }

    public void setTgl(LocalDate tgl) {
        this.tgl.set(tgl);
    }

    public ObjectProperty<LocalDate> tglProp() {
        return tgl;
    }
}
