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
private TextField jamInput;
@FXML
private TableView<JadwalSecurity> tabel;
@FXML
private TableColumn<JadwalSecurity, String> colNama;
@FXML
private TableColumn<JadwalSecurity, LocalDate> colTgl;
@FXML
private TableColumn<JadwalSecurity, String> colJam;

private ObservableList<JadwalSecurity> listData = FXCollections.observableArrayList();

@Override
public void initialize(java.net.URL url, ResourceBundle rb) {
    colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
    colTgl.setCellValueFactory(new PropertyValueFactory<>("tgl"));
    colJam.setCellValueFactory(new PropertyValueFactory<>("jamJaga"));
    tabel.setItems(listData);
    tabel.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            namaInput.setText(newVal.getNama());
            tglInput.setValue(newVal.getTgl());
            jamInput.setText(newVal.getJamJaga());
        }
    });
}

@FXML
private void btnTambahAction(ActionEvent event) {
    if (cekInput()) {
        JadwalSecurity item = new JadwalSecurity(
                namaInput.getText(),
                tglInput.getValue(),
                jamInput.getText()
        );
        listData.add(item);
        resetForm();
        notif("Berhasil", "Data udah ditambah!", Alert.AlertType.INFORMATION);
    } else {
        notif("Salah", "Nama, tanggal dan jam harus diisi!", Alert.AlertType.ERROR);
    }
}

@FXML
private void btnEditAction(ActionEvent event) {
    JadwalSecurity selected = tabel.getSelectionModel().getSelectedItem();
    if (selected != null) {
        if (cekInput()) {
            selected.setNama(namaInput.getText());
            selected.setTgl(tglInput.getValue());
            selected.setJamJaga(jamInput.getText());
            tabel.refresh();
            resetForm();
            notif("Berhasil", "Data udah diupdate!", Alert.AlertType.INFORMATION);
        } else {
            notif("Salah", "Nama, tanggal dan jam harus diisi!", Alert.AlertType.ERROR);
        }
    } else {
        notif("Salah", "Pilih dulu data yang mau dirubah", Alert.AlertType.ERROR);
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
        notif("Salah", "Pilih dulu data yang mau dihapus!", Alert.AlertType.ERROR);
    }
}

private boolean cekInput() {
    return namaInput.getText() != null && !namaInput.getText().trim().isEmpty()
            && tglInput.getValue() != null
            && jamInput.getText() != null && !jamInput.getText().trim().isEmpty();
}

private void resetForm() {
    namaInput.clear();
    tglInput.setValue(null);
    jamInput.clear();
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
