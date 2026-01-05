package Proyek.Controller;

import Proyek.Dompetku;
import Proyek.DAO.PenggunaDAO;
import Proyek.Model.Pengguna;
import Proyek.Session;
import Proyek.Utils.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
public class LoginController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private TextField namaAkunInput;

    @FXML
    private PasswordField sandiInput;

    @FXML
    private TextField sandiInputVisible;

    @FXML
    private Button toggleSandiButton;

    private boolean sandiVisible = false;

    @FXML
    public void initialize() {
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
    private void handleToggleSandiVisibility(ActionEvent event) {
        sandiVisible = !sandiVisible;
        sandiInputVisible.setVisible(sandiVisible);
        sandiInput.setVisible(!sandiVisible);
        if (toggleSandiButton != null) {
            toggleSandiButton.setText(sandiVisible ? "🙈" : "👁");
        }
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        String namaAkun = namaAkunInput.getText();
        String sandi = sandiInput.getText(); 

        if (namaAkun == null || namaAkun.isBlank() || sandi == null || sandi.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Form Error", "Mohon isi nama akun dan sandi.");
            return;
        }

        PenggunaDAO dao = new PenggunaDAO();
        Pengguna pengguna = dao.cariByNamaAkun(namaAkun);

        if (pengguna != null) {
            if (pengguna.getSandi().equals(sandi)) {
                if (pengguna.isAktif()) {
                    Session.setPengguna(pengguna);

                    PenggunaDAO daoUpdate = new PenggunaDAO();
                    LocalDateTime now = LocalDateTime.now();
                    daoUpdate.updateLoginTerakhir(pengguna.getIdUser(), now);
                    pengguna.setLoginTerakhir(now);

                    showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                            "Selamat datang, " + pengguna.getNamaAkun());

                    try {
                        if (pengguna.isMahasiswa()) {
                            Dompetku.showMahasiswaDashboardScene();
                        } else if (pengguna.isOrangTua()) {
                            Dompetku.showOrangTuaDashboardScene();
                        } else if (pengguna.isAdmin()) {
                            Dompetku.showAdminDashboardScene();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Sistem Error", "Gagal membuka dashboard.");
                    }

                } else {
                    String status = pengguna.getAktif();
                    if ("belum aktif".equals(status)) {
                        showAlert(Alert.AlertType.WARNING, "Login Gagal",
                                "Akun Anda belum aktif.\nMenunggu persetujuan dari admin.");
                    } else if ("deaktif".equals(status)) {
                        showAlert(Alert.AlertType.ERROR, "Login Gagal",
                                "Akun Anda telah dinonaktifkan oleh admin.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Login Gagal", "Akun Anda tidak aktif.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama akun atau sandi salah.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama akun atau sandi salah.");
        }
    }

    @FXML
    private void handleLupaSandiAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Lupa Password");
        dialog.setHeaderText("Pemulihan Akun");
        dialog.setContentText("Masukkan email terdaftar Anda:");
        dialog.setGraphic(null); // Hapus icon ?
        dialog.initStyle(StageStyle.UTILITY); // Hapus bar aplikasi
        styleDialog(dialog.getDialogPane()); // Suntik CSS

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String emailInput = result.get();
            if (emailInput.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Email tidak boleh kosong.");
                return;
            }

            PenggunaDAO dao = new PenggunaDAO();
            Pengguna user = dao.cariByEmail(emailInput);

            if (user != null) {
                new Thread(() -> {
                    try {
                        Email.sendPasswordEmail(user.getEmail(), user.getSandi());
                        javafx.application.Platform.runLater(() -> 
                            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                                "Password telah dikirim ke email: " + emailInput)
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        javafx.application.Platform.runLater(() -> 
                            showAlert(Alert.AlertType.ERROR, "Gagal",
                                "Gagal mengirim email. Pastikan koneksi internet lancar.")
                        );
                    }
                }).start();

            } else {
                showAlert(Alert.AlertType.ERROR, "Tidak Ditemukan", "Email tersebut tidak terdaftar.");
            }
        }
    }

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        try {
            Dompetku.showRegisterScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman register.");
        }
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
            dialogPane.getStylesheets().add(getClass().getResource("/Proyek/css/Login.css").toExternalForm());
            dialogPane.getStyleClass().add("my-alert"); // Class CSS khusus
        } catch (Exception e) {
        }
    }
}


