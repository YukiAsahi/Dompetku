package Proyek.Controller;

import Proyek.DAO.AdminDashboardDAO;
import Proyek.Dompetku;
import Proyek.Model.Pengguna;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private VBox usersListContainer;

    @FXML
    private TextField txtSearch;

    private final AdminDashboardDAO adminDAO = new AdminDashboardDAO();
    private List<Pengguna> allUsers = new ArrayList<>();

    @FXML
    public void initialize() {
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }
        muatSemuaPengguna();
    }

    private void muatSemuaPengguna() {
        usersListContainer.getChildren().clear();
        allUsers = adminDAO.getAllUsers();
        tampilkanPengguna(allUsers);
    }

    private void tampilkanPengguna(List<Pengguna> users) {
        usersListContainer.getChildren().clear();

        if (users.isEmpty()) {
            Label kosong = new Label("Tidak ada data pengguna.");
            kosong.getStyleClass().add("emptyText");
            usersListContainer.getChildren().add(kosong);
            return;
        }

        for (Pengguna p : users) {
            if (!"admin".equalsIgnoreCase(p.getTipeAkun())) {
                HBox card = createUserCard(p);
                usersListContainer.getChildren().add(card);
            }
        }
    }

    @FXML
    private void tanganiPencarian() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            tampilkanPengguna(allUsers);
        } else {
            List<Pengguna> filtered = new ArrayList<>();
            for (Pengguna p : allUsers) {
                if (p.getNamaAkun().toLowerCase().contains(keyword) ||
                        p.getEmail().toLowerCase().contains(keyword)) {
                    filtered.add(p);
                }
            }
            tampilkanPengguna(filtered);
        }
    }

    private HBox createUserCard(Pengguna pengguna) {
        HBox card = new HBox();
        card.setSpacing(20);
        card.getStyleClass().add("userCard");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(5);

        Label nameLabel = new Label(pengguna.getNamaAkun());
        nameLabel.getStyleClass().add("userName");

        Label emailLabel = new Label(pengguna.getEmail());
        emailLabel.getStyleClass().add("userEmail");

        boolean isActive = "aktif".equalsIgnoreCase(pengguna.getAktif());

        Label statusLabel = new Label();
        if (isActive) {
            statusLabel.setText("Status: Aktif");
            statusLabel.getStyleClass().add("statusLabelActive");
        } else {
            statusLabel.setText("Status: Nonaktif");
            statusLabel.getStyleClass().add("statusLabelInactive");
        }

        infoBox.getChildren().addAll(nameLabel, emailLabel, statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button();
        actionBtn.getStyleClass().add("actionButton");

        if (isActive) {
            actionBtn.setText("Nonaktifkan");
            actionBtn.getStyleClass().add("btnDeactivate");
            actionBtn.setOnAction(e -> toggleStatus(pengguna, "deaktif"));
        } else {
            actionBtn.setText("Aktifkan");
            actionBtn.getStyleClass().add("btnActivate");
            actionBtn.setOnAction(e -> toggleStatus(pengguna, "aktif"));
        }

        card.getChildren().addAll(infoBox, spacer, actionBtn);
        return card;
    }

    private void toggleStatus(Pengguna p, String newStatus) {
        boolean ok = adminDAO.updateUserStatus(p.getIdUser(), newStatus);

        if (ok) {
            muatSemuaPengguna();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah status user.");
        }
    }

    @FXML
    private void handleApplicantNav(ActionEvent event) {
        try {
            Dompetku.showAdminDashboardScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsersNav(ActionEvent event) {
        muatSemuaPengguna();
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
