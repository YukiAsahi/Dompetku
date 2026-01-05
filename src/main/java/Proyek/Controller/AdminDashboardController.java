package Proyek.Controller;

import Proyek.DAO.AdminDashboardDAO;
import Proyek.Dompetku;
import Proyek.Model.Pengguna;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.List;
public class AdminDashboardController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private VBox applicantListContainer;

    private final AdminDashboardDAO adminDashboardDAO = new AdminDashboardDAO();

    @FXML
    public void initialize() {
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }
        loadApplicants();
    }

    private void loadApplicants() {
        applicantListContainer.getChildren().clear();

        List<Pengguna> pending = adminDashboardDAO.getPendingApplicants();

        if (pending.isEmpty()) {
            Label kosong = new Label("Belum ada applicant baru.");
            kosong.getStyleClass().add("emptyText");
            applicantListContainer.getChildren().add(kosong);
            return;
        }

        for (Pengguna p : pending) {
            HBox card = createApplicantCard(p);
            applicantListContainer.getChildren().add(card);
        }
    }

    private HBox createApplicantCard(Pengguna pengguna) {
        HBox card = new HBox(20);
        card.getStyleClass().add("applicantCard");

        Label nameLabel = new Label(pengguna.getNamaAkun() +
                "  (" + pengguna.getEmail() + ")");
        nameLabel.getStyleClass().add("applicantName");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button approveBtn = new Button("✔");
        approveBtn.getStyleClass().add("approveButton");
        approveBtn.setOnAction(e -> approve(pengguna));

        Button rejectBtn = new Button("✖");
        rejectBtn.getStyleClass().add("rejectButton");
        rejectBtn.setOnAction(e -> reject(pengguna));

        HBox buttons = new HBox(12, approveBtn, rejectBtn);

        card.getChildren().addAll(nameLabel, spacer, buttons);
        return card;
    }

    private void approve(Pengguna p) {
        boolean ok = adminDashboardDAO.approveApplicant(p.getIdUser());
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Berhasil",
                    "Akun " + p.getNamaAkun() + " telah diaktifkan.");
            loadApplicants();
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Gagal mengaktifkan akun.");
        }
    }

    private void reject(Pengguna p) {
        boolean ok = adminDashboardDAO.rejectApplicant(p.getIdUser());
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Berhasil",
                    "Akun " + p.getNamaAkun() + " telah dinonaktifkan.");
            loadApplicants();
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Gagal menonaktifkan akun.");
        }
    }

    @FXML
    private void handleApplicantNav(ActionEvent event) {
        loadApplicants();
    }

    @FXML
    private void handleUsersNav(ActionEvent event) {
        try {
            Dompetku.showAdminUsersScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal membuka halaman Users.");
        }
    }

    @FXML
    private void handleTrafficNav(ActionEvent event) {
        try {
            Dompetku.showAdminTrafficScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal membuka halaman Traffic Application.");
        }
    }

    @FXML
    private void handleHistoryNav(ActionEvent event) {
        try {
            Dompetku.showAdminHistoryScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal membuka halaman History.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Dompetku.showLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal logout.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


