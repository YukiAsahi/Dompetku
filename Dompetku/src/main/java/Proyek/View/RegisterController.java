package Proyek.View;

import Proyek.Dompetku;
import Proyek.DAO.DaftarAkunBaruDAO;
import Proyek.Model.DaftarAkunBaru;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    @FXML
    private TextField namaLengkapInput;

    @FXML
    private TextField namaAkunInput;

    @FXML
    private TextField emailInput;

    @FXML
    private PasswordField sandiInput;

    @FXML
    private PasswordField confirmSandiInput;

    @FXML
    private ComboBox<String> tipeAkunCombo;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tipeAkunCombo.setItems(FXCollections.observableArrayList(
            "Mahasiswa",
            "OrangTua"
        ));
    }


    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String namaLengkap = namaLengkapInput.getText();
        String namaAkun = namaAkunInput.getText();
        String email = emailInput.getText();
        String sandi = sandiInput.getText();
        String confirmSandi = confirmSandiInput.getText();
        String tipeAkun = tipeAkunCombo.getValue();

        System.out.println("=== REGISTER ACTION DIMULAI ===");
        System.out.println("Nama Lengkap: " + namaLengkap);
        System.out.println("Nama Akun: " + namaAkun);
        System.out.println("Email: " + email);
        System.out.println("Tipe Akun: " + tipeAkun);

        if (!validateInput(namaLengkap, namaAkun, email, sandi, confirmSandi, tipeAkun)) {
            System.out.println("✗ Validasi gagal!");
            return;
        }

        System.out.println("✓ Validasi berhasil!");

        DaftarAkunBaru daftarAkun = new DaftarAkunBaru(namaAkun, sandi, namaLengkap, email, tipeAkun);

        DaftarAkunBaruDAO dao = new DaftarAkunBaruDAO();
        if (dao.insertDaftarAkunBaru(daftarAkun)) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil",
                      "Akun Anda telah terdaftar dengan status PENDING.\n" +
                      "Menunggu persetujuan dari admin.");
            System.out.println("✓ Registrasi berhasil!");
            clearForm();
            try {
                Dompetku.showLoginScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal",
                      "Gagal menyimpan data. Kemungkinan:\n" +
                      "- Nama akun atau email sudah terdaftar\n" +
                      "- Koneksi database error");
            System.out.println("✗ Registrasi gagal!");
        }
    }


    @FXML
    private void handleCancelAction(ActionEvent event) {
        try {
            Dompetku.showLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke login.");
        }
    }


    private boolean validateInput(String namaLengkap, String namaAkun, String email,
                                   String sandi, String confirmSandi, String tipeAkun) {
        
        if (namaLengkap == null || namaLengkap.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Nama lengkap tidak boleh kosong.");
            return false;
        }
        
        if (namaAkun == null || namaAkun.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Nama akun tidak boleh kosong.");
            return false;
        }
        
        if (email == null || email.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Email tidak boleh kosong.");
            return false;
        }
        
        if (sandi == null || sandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Sandi tidak boleh kosong.");
            return false;
        }
        
        if (confirmSandi == null || confirmSandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Konfirmasi sandi tidak boleh kosong.");
            return false;
        }
        
        if (tipeAkun == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Tipe akun harus dipilih.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Format email tidak valid.\nContoh: nama@domain.com");
            return false;
        }

        if (!sandi.equals(confirmSandi)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Sandi dan konfirmasi sandi tidak sama.");
            return false;
        }

        if (sandi.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", 
                      "Sandi minimal 6 karakter.");
            return false;
        }

        return true;
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }


    private void clearForm() {
        namaLengkapInput.clear();
        namaAkunInput.clear();
        emailInput.clear();
        sandiInput.clear();
        confirmSandiInput.clear();
        tipeAkunCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
