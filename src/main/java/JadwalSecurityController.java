import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class JadwalSecurityController implements Initializable {

    @FXML
    private TextField namaInput;
    @FXML
    private DatePicker tglInput;
    @FXML
    private TableView<JadwalSecurity> tabel;
    @FXML
    private TableColumn<JadwalSecurity, String> colNama;
    @FXML
    private TableColumn<JadwalSecurity, LocalDate> colTgl;

    private ObservableList<JadwalSecurity> listData = FXCollections.observableArrayList();

    @Override
    public void initialize(java.net.URL url, ResourceBundle rb) {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTgl.setCellValueFactory(new PropertyValueFactory<>("tgl"));
        tabel.setItems(listData);
        tabel.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                namaInput.setText(newVal.getNama());
                tglInput.setValue(newVal.getTgl());
            }
        });
    }

    @FXML
    private void btnTambahAction(ActionEvent event) {
        if (cekInput()) {
            JadwalSecurity item = new JadwalSecurity(
                    namaInput.getText(),
                    tglInput.getValue()
            );
            listData.add(item);
            resetForm();
            notif("Berhasil", "Data udah ditambah!", Alert.AlertType.INFORMATION);
        } else {
            notif("Salah", "Nama dan tanggal harus diisi!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnEditAction(ActionEvent event) {
        JadwalSecurity selected = tabel.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (cekInput()) {
                selected.setNama(namaInput.getText());
                selected.setTgl(tglInput.getValue());
                tabel.refresh();
                resetForm();
                notif("Berhasil", "Data udah diupdate!", Alert.AlertType.INFORMATION);
            } else {
                notif("Salah", "Nama dan tanggal harus diisi!", Alert.AlertType.ERROR);
            }
        } else {
            notif("Salah", "Pilih data yang mau dirubah", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnHapusAction(ActionEvent event) {
        JadwalSecurity selected = tabel.getSelectionModel().getSelectedItem();
        if (selected != null) {
            listData.remove(selected);
            resetForm();
            notif("Berhasil", "Data udah dihapus!", Alert.AlertType.INFORMATION);
        } else {
            notif("Salah", "Pilih data yang mau dihapus!", Alert.AlertType.ERROR);
        }
    }

    private boolean cekInput() {
        return namaInput.getText() != null && !namaInput.getText().trim().isEmpty()
                && tglInput.getValue() != null;
    }

    private void resetForm() {
        namaInput.clear();
        tglInput.setValue(null);
        tabel.getSelectionModel().clearSelection();
    }

    private void notif(String judul, String pesan, Alert.AlertType tipe) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
