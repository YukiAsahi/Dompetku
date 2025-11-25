package Proyek.View;

import Proyek.Dompetku;
import Proyek.DAO.PenggunaDAO;
import Proyek.Model.Pengguna;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField namaAkunInput;

    @FXML
    private PasswordField sandiInput;

    /**
     * Handle Login Button Action
     */
    @FXML
    private void handleLoginAction(ActionEvent event) {
        String namaAkun = namaAkunInput.getText();
        String sandi = sandiInput.getText();

        // Validasi input tidak kosong
        if (namaAkun == null || namaAkun.isBlank() || sandi == null || sandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Form Error", "Mohon isi nama akun dan sandi.");
            return;
        }

        // Query ke database
        PenggunaDAO dao = new PenggunaDAO();
        Pengguna pengguna = dao.getPenggunaByNamaAkunSandi(namaAkun, sandi);

        if (pengguna != null) {
            if (pengguna.isAktif()) {
                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                          "Selamat datang, " + pengguna.getNamaLengkap() + 
                          " (" + pengguna.getTipeAkun() + ")");
                System.out.println("✓ User login: " + pengguna.getNamaLengkap());
                // TODO: Lanjutkan ke tampilan utama aplikasi
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Akun Anda tidak aktif.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama akun atau sandi salah.");
        }
    }

    /**
     * Handle Reset Button Action
     */
    @FXML
    private void handleResetAction(ActionEvent event) {
        namaAkunInput.clear();
        sandiInput.clear();
    }

    /**
     * Handle Register Link Action - Berpindah ke Register Scene
     */
    @FXML
    private void handleRegisterAction(ActionEvent event) {
        try {
            Dompetku.showRegisterScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman register.");
        }
    }

    /**
     * Helper Method - Tampilkan Alert Dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
