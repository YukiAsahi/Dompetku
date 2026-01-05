package Proyek.Controller;

import Proyek.Dompetku;
import Proyek.Model.Pengguna;
import Proyek.DAO.PenggunaDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane; // Tambahan Import
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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
    private TextField sandiInputVisible;

    @FXML
    private TextField confirmSandiInputVisible;

    @FXML
    private Button toggleSandiButton;

    @FXML
    private Button toggleConfirmSandiButton;

    @FXML
    private ComboBox<String> tipeAkunCombo;

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    private boolean sandiVisible = false;
    private boolean confirmSandiVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tipeAkunCombo.setItems(FXCollections.observableArrayList(
                "Mahasiswa",
                "OrangTua"));
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }
        if (sandiInputVisible != null && sandiInput != null) {
            sandiInputVisible.managedProperty().bind(sandiInputVisible.visibleProperty());
            sandiInput.managedProperty().bind(sandiInput.visibleProperty());
            sandiInputVisible.textProperty().bindBidirectional(sandiInput.textProperty());
            sandiInputVisible.setVisible(false);
            sandiInput.setVisible(true);
        }

        if (confirmSandiInputVisible != null && confirmSandiInput != null) {
            confirmSandiInputVisible.managedProperty().bind(confirmSandiInputVisible.visibleProperty());
            confirmSandiInput.managedProperty().bind(confirmSandiInput.visibleProperty());
            confirmSandiInputVisible.textProperty().bindBidirectional(confirmSandiInput.textProperty());
            confirmSandiInputVisible.setVisible(false);
            confirmSandiInput.setVisible(true);
        }
    }

    @FXML
    private void handleHomeNav(ActionEvent event) {
        try {
            Dompetku.showHomeScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Home.");
        }
    }

    @FXML
    private void handleAboutNav(ActionEvent event) {
        try {
            Dompetku.showAboutScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman About Us.");
        }
    }

    @FXML
    private void handleContactNav(ActionEvent event) {
        try {
            Dompetku.showContactScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Contact.");
        }
    }

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String namaLengkap = namaLengkapInput.getText();
        String namaAkun = namaAkunInput.getText();
        String email = emailInput.getText();
        String sandi = sandiInput.getText();
        String confirmSandi = confirmSandiInput.getText();
        String tipeAkun = tipeAkunCombo.getValue();
        if (!validateInput(namaLengkap, namaAkun, email, sandi, confirmSandi, tipeAkun)) {
            return;
        }

        PenggunaDAO dao = new PenggunaDAO();

        if (dao.apakahNamaAkunAda(namaAkun)) {
            showAlert(Alert.AlertType.WARNING, "Username Sudah Digunakan",
                    "Nama akun '" + namaAkun + "' sudah terdaftar.\nSilakan gunakan nama akun lain.");
            return;
        }

        if (dao.apakahEmailAda(email)) {
            showAlert(Alert.AlertType.WARNING, "Email Sudah Digunakan",
                    "Email '" + email + "' sudah terdaftar.\nSilakan gunakan email lain.");
            return;
        }

        Pengguna penggunaBaru = new Pengguna();
        penggunaBaru.setNamaAkun(namaAkun);
        penggunaBaru.setSandi(sandi);
        penggunaBaru.setNamaLengkap(namaLengkap);
        penggunaBaru.setEmail(email);
        penggunaBaru.setTipeAkun(tipeAkun);
        penggunaBaru.setAktif("belum aktif");
        penggunaBaru.setTanggalDaftar(java.time.LocalDateTime.now());

        if (dao.tambahPengguna(penggunaBaru)) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil",
                    "Akun Anda telah terdaftar dengan status PENDING.\n" +
                            "Menunggu persetujuan dari admin.");
            clearForm();
            try {
                Dompetku.showLoginScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal",
                    "Gagal menyimpan data. Silakan coba lagi.");
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

    @FXML
    private void handleToggleSandiVisibility(ActionEvent event) {
        sandiVisible = !sandiVisible;
        sandiInputVisible.setVisible(sandiVisible);
        sandiInput.setVisible(!sandiVisible);

        if (toggleSandiButton != null) {
            toggleSandiButton.setText(sandiVisible ? "🙈" : "👁");
        }
    }

    @FXML
    private void handleToggleConfirmSandiVisibility(ActionEvent event) {
        confirmSandiVisible = !confirmSandiVisible;
        confirmSandiInputVisible.setVisible(confirmSandiVisible);
        confirmSandiInput.setVisible(!confirmSandiVisible);

        if (toggleConfirmSandiButton != null) {
            toggleConfirmSandiButton.setText(confirmSandiVisible ? "🙈" : "👁");
        }
    }

    private boolean validateInput(String namaLengkap, String namaAkun, String email,
            String sandi, String confirmSandi, String tipeAkun) {

        if (namaLengkap == null || namaLengkap.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Nama lengkap tidak boleh kosong.");
            return false;
        }
        if (namaAkun == null || namaAkun.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Nama akun tidak boleh kosong.");
            return false;
        }
        if (email == null || email.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Email tidak boleh kosong.");
            return false;
        }
        if (sandi == null || sandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Sandi tidak boleh kosong.");
            return false;
        }
        if (confirmSandi == null || confirmSandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Konfirmasi sandi tidak boleh kosong.");
            return false;
        }
        if (tipeAkun == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Tipe akun harus dipilih.");
            return false;
        }
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Format email tidak valid.\nContoh: nama@domain.com");
            return false;
        }
        if (!sandi.equals(confirmSandi)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Sandi dan konfirmasi sandi tidak sama.");
            return false;
        }
        if (sandi.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validasi Error", "Sandi minimal 6 karakter.");
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
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.setGraphic(null);
        styleDialog(alert.getDialogPane());

        alert.showAndWait();
    }

    private void styleDialog(DialogPane dialogPane) {
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/Proyek/css/Register.css").toExternalForm());
            dialogPane.getStyleClass().add("my-alert");
        } catch (Exception e) {
        }
    }
}
