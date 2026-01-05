package Proyek.Controller;

import Proyek.DAO.AdminDashboardDAO;
import Proyek.Dompetku;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.IOException;
public class AdminJumlahPengunjungController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private VBox trafficListContainer;

    private final AdminDashboardDAO adminDAO = new AdminDashboardDAO();

    @FXML
    public void initialize() {
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }

        loadStatistics();
    }

    private void loadStatistics() {
        trafficListContainer.getChildren().clear();
        trafficListContainer.setSpacing(16);
        int loginsToday = adminDAO.getLoginsToday();
        int loginsMonth = adminDAO.getLoginsThisMonth();
        int totalUsers = adminDAO.getTotalUsers();
        int activeUsers = adminDAO.getTotalActiveUsers();
        int inactiveUsers = adminDAO.getTotalInactiveUsers();
        int pendingUsers = adminDAO.getTotalPendingUsers();
        int totalMahasiswa = adminDAO.getTotalMahasiswa();
        int totalOrangTua = adminDAO.getTotalOrangTua();
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(16);
        statsGrid.setVgap(16);
        statsGrid.setPadding(new Insets(8));
        statsGrid.add(createStatCard("👤", "Login Hari Ini", String.valueOf(loginsToday), "#14b8a6"), 0, 0);
        statsGrid.add(createStatCard("📅", "Login Bulan Ini", String.valueOf(loginsMonth), "#0ea5e9"), 1, 0);
        statsGrid.add(createStatCard("👥", "Total User", String.valueOf(totalUsers), "#8b5cf6"), 0, 1);
        statsGrid.add(createStatCard("✅", "User Aktif", String.valueOf(activeUsers), "#22c55e"), 1, 1);
        statsGrid.add(createStatCard("❌", "User Nonaktif", String.valueOf(inactiveUsers), "#ef4444"), 0, 2);
        statsGrid.add(createStatCard("⏳", "Pending Approval", String.valueOf(pendingUsers), "#f59e0b"), 1, 2);
        statsGrid.add(createStatCard("🎓", "Total Mahasiswa", String.valueOf(totalMahasiswa), "#06b6d4"), 0, 3);
        statsGrid.add(createStatCard("👨‍👩‍👧", "Total Orang Tua", String.valueOf(totalOrangTua), "#ec4899"), 1, 3);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(50);
        statsGrid.getColumnConstraints().addAll(col1, col2);

        trafficListContainer.getChildren().add(statsGrid);
    }

    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 4); " +
                "-fx-border-color: " + color + "; -fx-border-width: 0 0 4 0; -fx-border-radius: 16;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
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
        try {
            Dompetku.showAdminUsersScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTrafficNav(ActionEvent event) {
        loadStatistics();
    }

    @FXML
    private void handleHistoryNav(ActionEvent event) {
        try {
            Dompetku.showAdminHistoryScene();
        } catch (IOException e) {
            e.printStackTrace();
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
}


