package Proyek.Controller;

import Proyek.DAO.AdminDashboardDAO;
import Proyek.Dompetku;
import Proyek.Model.Pengguna;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class AdminHistoryController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private VBox historyListContainer;

    private final AdminDashboardDAO adminDAO = new AdminDashboardDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }
        loadHistoryData();
    }

    private void loadHistoryData() {
        historyListContainer.getChildren().clear();

        List<Pengguna> users = adminDAO.getAllUsersForHistory();
        List<HistoryItem> timeline = new ArrayList<>();

        for (Pengguna p : users) {
            if (p.getTanggalDaftar() != null) {
                timeline.add(new HistoryItem(
                        p.getNamaAkun(),
                        "Registrasi Akun Baru",
                        p.getTanggalDaftar()));
            }

            if (p.getLoginTerakhir() != null) {
                timeline.add(new HistoryItem(
                        p.getNamaAkun(),
                        "Login ke Aplikasi",
                        p.getLoginTerakhir()));
            }
        }

        timeline.sort(Comparator.comparing(HistoryItem::getWaktu).reversed());

        if (timeline.isEmpty()) {
            Label kosong = new Label("Belum ada aktivitas.");
            kosong.getStyleClass().add("emptyText");
            historyListContainer.getChildren().add(kosong);
            return;
        }

        for (HistoryItem item : timeline) {
            HBox card = createHistoryCard(item);
            historyListContainer.getChildren().add(card);
        }
    }

    private HBox createHistoryCard(HistoryItem item) {
        HBox card = new HBox();
        card.setSpacing(20);
        card.getStyleClass().add("historyCard");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(4);

        HBox detailAksi = new HBox(5);
        Label userLabel = new Label(item.namaUser);
        userLabel.getStyleClass().add("historyUser");

        Label actionLabel = new Label("- " + item.aksi);
        actionLabel.getStyleClass().add("historyAction");

        detailAksi.getChildren().addAll(userLabel, actionLabel);

        String waktuStr = item.waktu.format(formatter);
        Label timeLabel = new Label(waktuStr);
        timeLabel.getStyleClass().add("historyTime");

        infoBox.getChildren().addAll(detailAksi, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(infoBox, spacer);
        return card;
    }

    private static class HistoryItem {
        String namaUser;
        String aksi;
        LocalDateTime waktu;

        public HistoryItem(String namaUser, String aksi, LocalDateTime waktu) {
            this.namaUser = namaUser;
            this.aksi = aksi;
            this.waktu = waktu;
        }

        public LocalDateTime getWaktu() {
            return waktu;
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
        try {
            Dompetku.showAdminUsersScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTrafficNav(ActionEvent event) {
        try {
            Dompetku.showAdminTrafficScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHistoryNav(ActionEvent event) {
        loadHistoryData();
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


