package com.mycompany.dompetku.View;

import com.mycompany.dompetku.DAO.PenggunaDAO;
import com.mycompany.dompetku.Model.Pengguna;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private void handleLoginAction(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Form Error", "Mohon isi username dan password.");
            return;
        }

        PenggunaDAO dao = new PenggunaDAO();
        Pengguna pengguna = dao.getPenggunaByUsernamePassword(username, password);

        if (pengguna != null) {
            if (pengguna.isAktif()) {
                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                          "Selamat datang, " + pengguna.getNamaLengkap() + " (" + pengguna.getTipeUser() + ")");
                // TODO: lanjutkan ke tampilan utama aplikasi
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Akun Anda tidak aktif.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
        }
    }

    @FXML
    private void handleOkAction(ActionEvent event) {
        // Contoh aksi reset form saat tombol OK ditekan
        usernameInput.clear();
        passwordInput.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
