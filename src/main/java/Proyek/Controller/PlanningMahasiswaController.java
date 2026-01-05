package Proyek.Controller;

import Proyek.DAO.BatasUangDAO;
import Proyek.DAO.CatatanUangDAO;
import Proyek.DAO.DompetDAO;
import Proyek.DAO.KategoriDAO;
import Proyek.DAO.PenggunaDAO;
import Proyek.Dompetku;
import Proyek.Model.BatasUang;
import Proyek.Model.CatatanUang;
import Proyek.Model.Dompet;
import Proyek.Model.Kategori;
import Proyek.Model.Pengguna;
import Proyek.Session;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PlanningMahasiswaController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Circle imgProfileCircle;
    @FXML
    private Label lblUsername;
    @FXML
    private Button btnToggleSidebar;
    @FXML
    private Button btnDashboard, btnTransaction, btnWallet, btnPlanning, btnHistory, btnSettings;
    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnNotification;
    @FXML
    private Label lblNotifBadge;
    @FXML
    private VBox notifPanel;
    @FXML
    private VBox notifContent;
    @FXML
    private Button btnCloseNotif;

    @FXML
    private Button btnTabBudgeting, btnTabGoals;
    @FXML
    private VBox budgetingPane, goalsPane;
    @FXML
    private VBox vboxBudgetCards, vboxGoalsCards;
    @FXML
    private Button btnAddBudget, btnAddGoals;

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private final BatasUangDAO batasUangDAO = new BatasUangDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();
    private final DompetDAO dompetDAO = new DompetDAO();
    private final CatatanUangDAO catatanUangDAO = new CatatanUangDAO();

    private Pengguna pengguna;
    private boolean sidebarVisible = true;
    private double sidebarWidth = 260.0;
    private boolean isBudgetingTab = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pengguna = Session.ambilPengguna();
        if (pengguna == null)
            return;

        lblUsername.setText("Halo " + pengguna.getNamaAkun());

        imgProfileCircle.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::loadFotoProfil);
            }
        });

        imgProfileCircle.setPickOnBounds(true);
        imgProfileCircle.setOnMouseClicked(e -> {
            try {
                pilihDanGantiFoto();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengganti foto profil.");
            }
        });

        setupSidebarActions();
        setupSidebarToggle();
        updateSidebarActiveButton();
        setupTabToggle();
        setupNotification();
        setupAddButtons();

        loadBudgetingList();
        loadNotifications();
    }

    private void loadFotoProfil() {
        try {
            Image cachedImage = Session.getProfileImage();
            if (cachedImage != null) {
                ImagePattern pattern = new ImagePattern(cachedImage, 0, 0, 1, 1, true);
                imgProfileCircle.setFill(pattern);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pilihDanGantiFoto() throws IOException {
        Window window = imgProfileCircle.getScene() != null
                ? imgProfileCircle.getScene().getWindow()
                : null;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Profil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file = chooser.showOpenDialog(window);

        if (file != null) {
            byte[] fotoBytes = Files.readAllBytes(file.toPath());
            boolean updated = penggunaDAO.gantiFoto(pengguna.getIdUser(), fotoBytes);

            if (updated) {
                pengguna.setFoto(fotoBytes);
                Session.updateProfileImage(fotoBytes);

                Image img = Session.getProfileImage();
                if (img != null) {
                    imgProfileCircle.setFill(new ImagePattern(img));
                }
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Foto profil berhasil diperbarui.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Tidak dapat menyimpan foto profil ke database.");
            }
        }
    }

    private void setupTabToggle() {
        btnTabBudgeting.getStyleClass().add("tabButtonAktif");

        btnTabBudgeting.setOnAction(e -> {
            if (!isBudgetingTab) {
                isBudgetingTab = true;
                btnTabBudgeting.getStyleClass().add("tabButtonAktif");
                btnTabGoals.getStyleClass().remove("tabButtonAktif");
                budgetingPane.setVisible(true);
                goalsPane.setVisible(false);
                loadBudgetingList();
            }
        });

        btnTabGoals.setOnAction(e -> {
            if (isBudgetingTab) {
                isBudgetingTab = false;
                btnTabGoals.getStyleClass().add("tabButtonAktif");
                btnTabBudgeting.getStyleClass().remove("tabButtonAktif");
                goalsPane.setVisible(true);
                budgetingPane.setVisible(false);
                loadGoalsList();
            }
        });
    }

    private void setupNotification() {
        btnNotification.setOnAction(e -> showNotificationPopup());
        if (btnCloseNotif != null) {
            btnCloseNotif.setOnAction(e -> notifPanel.setVisible(false));
        }
    }

    private void showNotificationPopup() {
        List<BatasUang> exceededBudgets = batasUangDAO.ambilBudgetMelebihi(pengguna.getIdUser());
        List<BatasUang> completedGoals = batasUangDAO.ambilGoalsTercapai(pengguna.getIdUser());

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setTitle("Notifikasi");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(380);
        content.setMaxHeight(450);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("\uD83D\uDD14 Notifikasi");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnClose = new Button("\u2715");
        btnClose.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-cursor: hand;");
        btnClose.setOnAction(ev -> dialogStage.close());
        header.getChildren().addAll(titleLabel, spacer, btnClose);

        VBox notifList = new VBox(10);
        notifList.setAlignment(Pos.TOP_CENTER);

        if (exceededBudgets.isEmpty() && completedGoals.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada notifikasi");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            notifList.getChildren().add(emptyLabel);
        } else {
            for (BatasUang bu : exceededBudgets) {
                VBox item = new VBox(4);
                item.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-border-color: #ef4444; " +
                        "-fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
                Label itemTitle = new Label("\u26A0\uFE0F Budget Terlampaui!");
                itemTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
                Label itemMsg = new Label(String.format("Kategori %s: Rp %,.0f dari Rp %,.0f",
                        bu.getKategoriNama(), bu.getCurrentSpending(), bu.getMaxUang()));
                itemMsg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                itemMsg.setWrapText(true);
                item.getChildren().addAll(itemTitle, itemMsg);
                notifList.getChildren().add(item);
            }

            for (BatasUang g : completedGoals) {
                VBox item = new VBox(4);
                item.setStyle("-fx-background-color: rgba(34, 197, 94, 0.2); -fx-border-color: #22c55e; " +
                        "-fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
                Label itemTitle = new Label("\uD83C\uDF89 Goals Tercapai!");
                itemTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
                Label itemMsg = new Label(String.format("Target %s Rp %,.0f tercapai!",
                        g.getKategoriNama(), g.getMaxUang()));
                itemMsg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                itemMsg.setWrapText(true);
                item.getChildren().addAll(itemTitle, itemMsg);
                notifList.getChildren().add(item);
            }
        }

        ScrollPane scrollPane = new ScrollPane(notifList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(header, scrollPane);

        Scene scene = new Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void toggleNotificationPanel() {
        notifPanel.setVisible(!notifPanel.isVisible());
        if (notifPanel.isVisible()) {
            loadNotifications();
        }
    }

    private void loadNotifications() {
        notifContent.getChildren().clear();

        List<BatasUang> exceededBudgets = batasUangDAO.ambilBudgetMelebihi(pengguna.getIdUser());
        List<BatasUang> completedGoals = batasUangDAO.ambilGoalsTercapai(pengguna.getIdUser());

        int notifCount = exceededBudgets.size() + completedGoals.size();

        if (notifCount > 0) {
            lblNotifBadge.setText(String.valueOf(notifCount));
            lblNotifBadge.setVisible(true);

            for (BatasUang bu : exceededBudgets) {
                VBox notifItem = createNotificationItem(
                        "⚠️ Budget Terlampaui!",
                        String.format("Kategori %s sudah melebihi batas. Terpakai: Rp %,.0f dari Rp %,.0f",
                                bu.getKategoriNama(), bu.getCurrentSpending(), bu.getMaxUang()),
                        true);
                notifContent.getChildren().add(notifItem);
            }

            for (BatasUang g : completedGoals) {
                VBox notifItem = createNotificationItem(
                        "🎉 Goals Tercapai!",
                        String.format("Selamat! Target %s sebesar Rp %,.0f telah tercapai!",
                                g.getKategoriNama(), g.getMaxUang()),
                        false);
                notifContent.getChildren().add(notifItem);
            }
        } else {
            lblNotifBadge.setVisible(false);
            Label emptyLabel = new Label("Tidak ada notifikasi");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            notifContent.getChildren().add(emptyLabel);
        }
    }

    private VBox createNotificationItem(String title, String message, boolean isWarning) {
        VBox item = new VBox(4);
        item.getStyleClass().add(isWarning ? "notifItemWarning" : "notifItemSuccess");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        msgLabel.setWrapText(true);

        item.getChildren().addAll(titleLabel, msgLabel);
        return item;
    }

    private void setupAddButtons() {
        btnAddBudget.setOnAction(e -> showAddBudgetDialog());
        btnAddGoals.setOnAction(e -> showAddGoalsDialog());
    }

    private void loadBudgetingList() {
        vboxBudgetCards.getChildren().clear();

        List<BatasUang> budgets = batasUangDAO.ambilBudgetingAktif(pengguna.getIdUser());

        if (budgets.isEmpty()) {
            VBox emptyState = createEmptyState("💰", "Belum ada budget",
                    "Tambahkan batasan anggaran untuk kategori tertentu");
            vboxBudgetCards.getChildren().add(emptyState);
        } else {
            for (BatasUang bu : budgets) {
                VBox card = createBudgetCard(bu);
                vboxBudgetCards.getChildren().add(card);
            }
        }
    }

    private void loadGoalsList() {
        vboxGoalsCards.getChildren().clear();

        List<BatasUang> goals = batasUangDAO.ambilGoalsByUserId(pengguna.getIdUser());
        for (BatasUang g : goals) {
            batasUangDAO.syncGoalsProgressWithBalance(g);
        }
        goals = batasUangDAO.ambilGoalsByUserId(pengguna.getIdUser());

        if (goals.isEmpty()) {
            VBox emptyState = createEmptyState("🎯", "Belum ada goals",
                    "Tambahkan target tabungan untuk kategori tertentu");
            vboxGoalsCards.getChildren().add(emptyState);
        } else {
            for (BatasUang g : goals) {
                VBox card = createGoalsCard(g);
                vboxGoalsCards.getChildren().add(card);
            }
        }
    }

    private VBox createEmptyState(String icon, String title, String subtitle) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("emptyStateCard");
        box.setPrefWidth(400);
        box.setPrefHeight(180);
        box.setPadding(new Insets(24));

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("emptyStateIcon");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("emptyStateTitle");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("emptyStateSubtitle");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setAlignment(Pos.CENTER);

        box.getChildren().addAll(iconLabel, titleLabel, subtitleLabel);
        return box;
    }

    private VBox createBudgetCard(BatasUang bu) {
        HBox cardContent = new HBox(16);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        cardContent.setPadding(new Insets(16, 20, 16, 20));
        cardContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardContent, Priority.ALWAYS);
        StackPane iconContainer = new StackPane();
        iconContainer.setMinSize(50, 50);
        iconContainer.setMaxSize(50, 50);
        Kategori kategori = kategoriDAO.ambilKategoriById(bu.getKategoriId());
        if (kategori != null && kategori.getIkon() != null && kategori.getIkon().length > 0) {
            try {
                javafx.scene.image.Image iconImage = new javafx.scene.image.Image(
                        new java.io.ByteArrayInputStream(kategori.getIkon()));
                javafx.scene.image.ImageView iconView = new javafx.scene.image.ImageView(iconImage);
                iconView.setFitWidth(48);
                iconView.setFitHeight(48);
                iconView.setPreserveRatio(true);
                Circle clip = new Circle(24, 24, 24);
                iconView.setClip(clip);
                iconContainer.getChildren().add(iconView);
            } catch (Exception e) {
                Label iconLabel = new Label("💰");
                iconLabel.setStyle("-fx-font-size: 28px;");
                iconContainer.getChildren().add(iconLabel);
            }
        } else {
            Label iconLabel = new Label("💰");
            iconLabel.setStyle("-fx-font-size: 28px;");
            iconContainer.getChildren().add(iconLabel);
        }
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label titleLabel = new Label(bu.getKategoriNama());
        titleLabel.getStyleClass().add("budgetCardTitle");

        String period = "";
        if (bu.getMulaiDari() != null && bu.getSelesaiSampai() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
            period = bu.getMulaiDari().format(fmt) + " - " + bu.getSelesaiSampai().format(fmt);
        }
        Label periodLabel = new Label(period);
        periodLabel.getStyleClass().add("budgetCardSubtitle");

        infoBox.getChildren().addAll(titleLabel, periodLabel);
        VBox amountBox = new VBox(4);
        amountBox.setAlignment(Pos.CENTER_RIGHT);

        Label spentLabel = new Label(String.format("Rp %,.0f", bu.getCurrentSpending()));
        spentLabel.getStyleClass().add(bu.isMelebihi() ? "budgetCardAmountExceeded" : "budgetCardAmount");

        Label limitLabel = new Label(String.format("dari Rp %,.0f", bu.getMaxUang()));
        limitLabel.getStyleClass().add("budgetCardLimit");

        Label percentLabel = new Label(String.format("%.1f%% terpakai", bu.hitungPersenTerpakai()));
        percentLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        amountBox.getChildren().addAll(spentLabel, limitLabel, percentLabel);

        cardContent.getChildren().addAll(iconContainer, infoBox, amountBox);
        double progress = Math.min(bu.getCurrentSpending() / bu.getMaxUang(), 1.0);
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(8);
        progressBar.getStyleClass().add("progressBarBudget");
        if (bu.isMelebihi()) {
            progressBar.getStyleClass().add("progressBarExceeded");
        }
        HBox progressContainer = new HBox();
        progressContainer.setPadding(new Insets(0, 20, 16, 20));
        progressContainer.getChildren().add(progressBar);
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        VBox card = new VBox();
        card.getStyleClass().add("budgetCard");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        if (bu.isMelebihi()) {
            card.getStyleClass().add("budgetCardExceeded");
        }

        card.getChildren().addAll(cardContent, progressContainer);
        card.setOnMouseClicked(e -> showBudgetDetailDialog(bu));

        return card;
    }

    private VBox createGoalsCard(BatasUang g) {
        VBox card = new VBox(10);
        card.getStyleClass().add("goalsCardNew");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);

        if (g.goalsTercapai()) {
            card.getStyleClass().add("goalsCardCompleted");
        }
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);

        Label titleLabel = new Label(g.getKategoriNama());
        titleLabel.getStyleClass().add("goalsCardNewTitle");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        if (g.goalsTercapai()) {
            Button selesaiBtn = new Button("✓ Selesai");
            selesaiBtn.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;");
            selesaiBtn.setOnAction(e -> {
                e.consume(); // Prevent card click
                handleCompleteGoals(g);
            });
            header.getChildren().add(selesaiBtn);
        }

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("goalsCardEditBtn");
        editBtn.setOnAction(e -> {
            e.consume(); // Prevent card click
            showGoalsDetailDialog(g);
        });

        header.getChildren().addAll(titleLabel, editBtn);
        double adjustedProgress = g.hitungProgressAktual();
        Label saldoLabel = new Label("Saldo");
        saldoLabel.getStyleClass().add("goalsCardSaldoLabel");

        Label amountLabel = new Label(String.format("Rp.%,.0f", adjustedProgress).replace(",", "."));
        amountLabel.getStyleClass().add("goalsCardSaldoAmount");
        double progressPercent = g.hitungPersenProgressAktual();
        ProgressBar progressBar = new ProgressBar(progressPercent / 100.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progressBarGoalsNew");
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);

        Label percentLabel = new Label(String.format("Tercapai %.0f%%", progressPercent));
        percentLabel.getStyleClass().add("goalsCardPercentLabel");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label targetLabel = new Label(String.format("dari RP. %,.0f", g.getMaxUang()).replace(",", "."));
        targetLabel.getStyleClass().add("goalsCardTargetLabel");

        footer.getChildren().addAll(percentLabel, spacer, targetLabel);

        card.getChildren().addAll(header, saldoLabel, amountLabel, progressBar, footer);
        card.setOnMouseClicked(e -> showGoalsDetailDialog(g));

        return card;
    }

    private void showAddBudgetDialog() {
        Stage dialog = createDialogStage("Tambah Budget");

        VBox content = new VBox(16);
        content.getStyleClass().add("popupDialog");
        content.setPrefWidth(400);

        Label title = new Label("💰 Tambah Budget Baru");
        title.getStyleClass().add("popupTitle");

        Label kategoriLabel = new Label("Pilih Kategori (Pengeluaran)");
        kategoriLabel.getStyleClass().add("popupLabel");

        ComboBox<Kategori> cbKategori = new ComboBox<>();
        cbKategori.getStyleClass().add("popupComboBox");
        cbKategori.setMaxWidth(Double.MAX_VALUE);
        List<Kategori> expenseCategories = kategoriDAO.ambilKategoriPengeluaran(pengguna.getIdUser());
        cbKategori.getItems().addAll(expenseCategories);

        Label limitLabel = new Label("Batas Anggaran (Rp)");
        limitLabel.getStyleClass().add("popupLabel");

        TextField tfLimit = new TextField();
        tfLimit.getStyleClass().add("popupTextField");
        tfLimit.setPromptText("Contoh: 500000");

        Label startLabel = new Label("Tanggal Mulai");
        startLabel.getStyleClass().add("popupLabel");

        DatePicker dpStart = new DatePicker(LocalDate.now());
        dpStart.getStyleClass().add("popupDatePicker");
        dpStart.setMaxWidth(Double.MAX_VALUE);

        Label endLabel = new Label("Tanggal Selesai");
        endLabel.getStyleClass().add("popupLabel");

        DatePicker dpEnd = new DatePicker(LocalDate.now().plusMonths(1));
        dpEnd.getStyleClass().add("popupDatePicker");
        dpEnd.setMaxWidth(Double.MAX_VALUE);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnSave = new Button("💾 Simpan");
        btnSave.getStyleClass().add("btnPopupPrimary");

        Button btnCancel = new Button("Batal");
        btnCancel.getStyleClass().add("btnPopupSecondary");
        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            if (cbKategori.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih kategori terlebih dahulu.");
                return;
            }

            String limitText = tfLimit.getText().replaceAll("[^0-9]", "");
            if (limitText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Masukkan batas anggaran.");
                return;
            }

            double limit = Double.parseDouble(limitText);
            if (limit <= 0) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Batas anggaran harus lebih dari 0.");
                return;
            }

            if (dpStart.getValue() == null || dpEnd.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih tanggal mulai dan selesai.");
                return;
            }

            if (dpEnd.getValue().isBefore(dpStart.getValue())) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Tanggal selesai harus setelah tanggal mulai.");
                return;
            }

            BatasUang newBudget = new BatasUang(pengguna.getIdUser(), cbKategori.getValue().getId(), limit);
            newBudget.setMulaiDari(dpStart.getValue());
            newBudget.setSelesaiSampai(dpEnd.getValue());

            if (batasUangDAO.tambah(newBudget)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Budget berhasil ditambahkan.");
                dialog.close();
                loadBudgetingList();
                loadNotifications();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan budget.");
            }
        });

        buttons.getChildren().addAll(btnCancel, btnSave);

        content.getChildren().addAll(title, kategoriLabel, cbKategori, limitLabel, tfLimit,
                startLabel, dpStart, endLabel, dpEnd, buttons);

        showDialog(dialog, content);
    }

    private void showBudgetDetailDialog(BatasUang bu) {
        Stage dialog = createDialogStage("Detail Budget");

        VBox content = new VBox(16);
        content.getStyleClass().add("popupDialog");
        content.setPrefWidth(420);

        Label title = new Label("💰 " + bu.getKategoriNama());
        title.getStyleClass().add("popupTitle");

        String periodText = "";
        if (bu.getMulaiDari() != null && bu.getSelesaiSampai() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
            periodText = "Periode: " + bu.getMulaiDari().format(fmt) + " - " + bu.getSelesaiSampai().format(fmt);
        }
        Label periodLabel = new Label(periodText);
        periodLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        VBox statsBox = new VBox(8);
        statsBox.setStyle("-fx-background-color: #334155; -fx-background-radius: 12; -fx-padding: 16;");

        Label spentLabel = new Label(String.format("Terpakai: Rp %,.0f", bu.getCurrentSpending()));
        spentLabel.setStyle("-fx-text-fill: " + (bu.isMelebihi() ? "#ef4444" : "#14b8a6")
                + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label limitLbl = new Label(String.format("Batas: Rp %,.0f", bu.getMaxUang()));
        limitLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label remainLabel = new Label(String.format("Sisa: Rp %,.0f", bu.sisaBudget()));
        remainLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        double progress = Math.min(bu.getCurrentSpending() / bu.getMaxUang(), 1.0);
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progressBarBudget");
        if (bu.isMelebihi()) {
            progressBar.getStyleClass().add("progressBarExceeded");
        }

        statsBox.getChildren().addAll(spentLabel, limitLbl, remainLabel, progressBar);

        Label editLimitLabel = new Label("Edit Batas Anggaran (Rp)");
        editLimitLabel.getStyleClass().add("popupLabel");

        TextField tfNewLimit = new TextField(String.format("%.0f", bu.getMaxUang()));
        tfNewLimit.getStyleClass().add("popupTextField");

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnDelete = new Button("🗑 Hapus");
        btnDelete.getStyleClass().add("btnPopupDanger");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus budget ini?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (batasUangDAO.hapus(bu.getId())) {
                        dialog.close();
                        loadBudgetingList();
                        loadNotifications();
                    }
                }
            });
        });

        Button btnSave = new Button("💾 Update");
        btnSave.getStyleClass().add("btnPopupPrimary");
        btnSave.setOnAction(e -> {
            String limitText = tfNewLimit.getText().replaceAll("[^0-9]", "");
            if (!limitText.isEmpty()) {
                double newLimit = Double.parseDouble(limitText);
                if (newLimit > 0) {
                    bu.setMaxUang(newLimit);
                    if (batasUangDAO.ubah(bu)) {
                        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Budget berhasil diupdate.");
                        dialog.close();
                        loadBudgetingList();
                        loadNotifications();
                    }
                }
            }
        });

        Button btnClose = new Button("Tutup");
        btnClose.getStyleClass().add("btnPopupSecondary");
        btnClose.setOnAction(e -> dialog.close());

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        buttons.getChildren().addAll(btnDelete, spacer, btnClose, btnSave);

        content.getChildren().addAll(title, periodLabel, statsBox, editLimitLabel, tfNewLimit, buttons);

        showDialog(dialog, content);
    }

    private void showAddGoalsDialog() {
        Stage dialog = createDialogStage("Tambah Goals");

        VBox content = new VBox(16);
        content.getStyleClass().add("popupDialog");
        content.setPrefWidth(400);

        Label title = new Label("🎯 Tambah Goals Baru");
        title.getStyleClass().add("popupTitle");

        Label kategoriLabel = new Label("Pilih Kategori (Pengeluaran)");
        kategoriLabel.getStyleClass().add("popupLabel");

        ComboBox<Kategori> cbKategori = new ComboBox<>();
        cbKategori.getStyleClass().add("popupComboBox");
        cbKategori.setMaxWidth(Double.MAX_VALUE);
        List<Kategori> expenseCategories = kategoriDAO.ambilKategoriPengeluaran(pengguna.getIdUser());
        cbKategori.getItems().addAll(expenseCategories);

        Label targetLabel = new Label("Target Tabungan (Rp)");
        targetLabel.getStyleClass().add("popupLabel");

        TextField tfTarget = new TextField();
        tfTarget.getStyleClass().add("popupTextField");
        tfTarget.setPromptText("Contoh: 10000000");

        Label walletLabel = new Label("Wallet yang Dikaitkan");
        walletLabel.getStyleClass().add("popupLabel");

        ComboBox<Dompet> cbWallet = new ComboBox<>();
        cbWallet.getStyleClass().add("popupComboBox");
        cbWallet.setMaxWidth(Double.MAX_VALUE);
        List<Dompet> wallets = dompetDAO.ambilDompetByUserId(pengguna.getIdUser());
        cbWallet.getItems().addAll(wallets);

        Label deadlineLabel = new Label("Deadline (Opsional)");
        deadlineLabel.getStyleClass().add("popupLabel");

        DatePicker dpDeadline = new DatePicker();
        dpDeadline.getStyleClass().add("popupDatePicker");
        dpDeadline.setMaxWidth(Double.MAX_VALUE);
        dpDeadline.setPromptText("Kosongkan jika tidak ada batas waktu");

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnSave = new Button("💾 Simpan");
        btnSave.getStyleClass().add("btnPopupPrimary");

        Button btnCancel = new Button("Batal");
        btnCancel.getStyleClass().add("btnPopupSecondary");
        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            if (cbKategori.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih kategori terlebih dahulu.");
                return;
            }

            String targetText = tfTarget.getText().replaceAll("[^0-9]", "");
            if (targetText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Masukkan target tabungan.");
                return;
            }

            double target = Double.parseDouble(targetText);
            if (target <= 0) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Target harus lebih dari 0.");
                return;
            }

            if (cbWallet.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih wallet yang dikaitkan.");
                return;
            }

            BatasUang newGoals = new BatasUang(
                    pengguna.getIdUser(),
                    cbKategori.getValue().getId(),
                    target,
                    cbWallet.getValue().getId());
            newGoals.setMulaiDari(LocalDate.now());
            if (dpDeadline.getValue() != null) {
                newGoals.setSelesaiSampai(dpDeadline.getValue());
            }

            if (batasUangDAO.tambah(newGoals)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Goals berhasil ditambahkan.");
                dialog.close();
                loadGoalsList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan goals.");
            }
        });

        buttons.getChildren().addAll(btnCancel, btnSave);

        content.getChildren().addAll(title, kategoriLabel, cbKategori, targetLabel, tfTarget,
                walletLabel, cbWallet, deadlineLabel, dpDeadline, buttons);

        showDialog(dialog, content);
    }

    private void showGoalsDetailDialog(BatasUang g) {
        Stage dialog = createDialogStage("Detail Goals");

        VBox content = new VBox(16);
        content.getStyleClass().add("popupDialog");
        content.setPrefWidth(420);
        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #064e3b, #022c22); -fx-background-radius: 16; -fx-padding: 28;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎯 " + g.getKategoriNama());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        HBox.setHgrow(title, Priority.ALWAYS);

        header.getChildren().add(title);

        Label saldoLabel = new Label("Saldo");
        saldoLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        double adjustedProgress = g.hitungProgressAktual();
        Label amountLabel = new Label(String.format("Rp %,.0f", adjustedProgress));
        amountLabel.setStyle("-fx-text-fill: #14b8a6; -fx-font-size: 28px; -fx-font-weight: bold;");

        double progressPercent = g.hitungPersenProgressAktual();
        ProgressBar progressBar = new ProgressBar(progressPercent / 100.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("progressBarGoals");
        progressBar.setPrefHeight(16);

        HBox progressInfo = new HBox();
        progressInfo.setAlignment(Pos.CENTER_LEFT);

        Label percentLabel = new Label(String.format("Tercapai %.0f%%", progressPercent));
        percentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label targetLabel = new Label(String.format("dari Rp %,.0f", g.getMaxUang()));
        targetLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        progressInfo.getChildren().addAll(percentLabel, spacer, targetLabel);

        VBox addProgressBox = new VBox(8);
        addProgressBox.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-background-radius: 12; -fx-padding: 16;");

        Label addLabel = new Label("Tambah Progress Tabungan (Rp)");
        addLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        TextField tfAddAmount = new TextField();
        tfAddAmount.getStyleClass().add("popupTextField");
        tfAddAmount.setPromptText("Contoh: 100000");

        Button btnAddProgress = new Button("➕ Tambah Progress");
        btnAddProgress.getStyleClass().add("btnPopupPrimary");
        btnAddProgress.setMaxWidth(Double.MAX_VALUE);
        btnAddProgress.setOnAction(e -> {
            String amountText = tfAddAmount.getText().replaceAll("[^0-9]", "");
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                if (amount > 0) {
                    double newProgress = g.getProgressTabungan() + amount;
                    if (batasUangDAO.updateProgress(g.getId(), newProgress)) {
                        showAlert(Alert.AlertType.INFORMATION, "Sukses",
                                String.format("Progress ditambah Rp %,.0f", amount));
                        dialog.close();
                        loadGoalsList();
                        loadNotifications();
                    }
                }
            }
        });

        addProgressBox.getChildren().addAll(addLabel, tfAddAmount, btnAddProgress);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnDelete = new Button("🗑 Hapus");
        btnDelete.getStyleClass().add("btnPopupDanger");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus goals ini?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (batasUangDAO.hapus(g.getId())) {
                        dialog.close();
                        loadGoalsList();
                    }
                }
            });
        });

        Pane btnSpacer = new Pane();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);

        Button btnClose = new Button("Tutup");
        btnClose.getStyleClass().add("btnPopupSecondary");
        btnClose.setOnAction(e -> dialog.close());

        buttons.getChildren().addAll(btnDelete, btnSpacer, btnClose);

        content.getChildren().addAll(header, saldoLabel, amountLabel, progressBar, progressInfo, addProgressBox,
                buttons);

        showDialog(dialog, content);
    }

    private void handleCompleteGoals(BatasUang g) {
        Dompet linkedWallet = dompetDAO.ambilDompetById(g.getDompetId());
        if (linkedWallet == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Dompet yang terkait tidak ditemukan.");
            return;
        }

        double amount = g.getMaxUang();
        if (linkedWallet.getSaldoSekarang() < amount) {
            showAlert(Alert.AlertType.WARNING, "Saldo Tidak Cukup",
                    String.format("Saldo dompet %s (Rp %,.0f) tidak mencukupi untuk pengeluaran Rp %,.0f",
                            linkedWallet.getNamaDompet(), linkedWallet.getSaldoSekarang(), amount));
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Selesaikan Goals");
        confirm.setHeaderText("🎉 Selesaikan Goals: " + g.getKategoriNama());
        confirm.setContentText(String.format(
                "Nominal: Rp %,.0f\nDompet: %s\n\nTransaksi pengeluaran akan dicatat dan goals akan dihapus.\n\nLanjutkan?",
                amount, linkedWallet.getNamaDompet()));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CatatanUang transaction = new CatatanUang(
                        linkedWallet.getId(),
                        pengguna.getIdUser(),
                        "Pengeluaran",
                        amount);
                transaction.setCatatan("Goals Tercapai: " + g.getKategoriNama());
                transaction.setKategoriId(g.getKategoriId());
                transaction.setTanggalCatat(java.time.LocalDateTime.now());

                boolean transactionSaved = catatanUangDAO.tambahCatatan(transaction);

                if (transactionSaved) {
                    double newBalance = linkedWallet.getSaldoSekarang() - amount;
                    dompetDAO.ubahSaldo(linkedWallet.getId(), newBalance);
                    boolean deleted = batasUangDAO.hapus(g.getId());

                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                                String.format(
                                        "Goals \"%s\" berhasil diselesaikan!\n\nPengeluaran Rp %,.0f telah dicatat.",
                                        g.getKategoriNama(), amount));
                        loadGoalsList();
                        loadNotifications();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus goals.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mencatat transaksi pengeluaran.");
                }
            }
        });
    }

    private Stage createDialogStage(String titleText) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle(titleText);
        return dialog;
    }

    private void showDialog(Stage dialog, VBox content) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popupOverlay");
        overlay.getChildren().add(content);
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                dialog.close();
            }
        });

        Scene scene = new Scene(overlay, 600, 550);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/Proyek/css/planningmahasiswa.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void setupSidebarToggle() {
        if (sidebar == null || btnToggleSidebar == null)
            return;
        sidebarWidth = sidebar.getPrefWidth() > 0 ? sidebar.getPrefWidth() : 260.0;
        btnToggleSidebar.setOnAction(e -> toggleSidebar());
    }

    private void toggleSidebar() {
        if (sidebar == null)
            return;

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), sidebar);
        if (sidebarVisible) {
            tt.setFromX(0);
            tt.setToX(-sidebarWidth);
            tt.setOnFinished(ev -> {
                sidebar.setVisible(false);
                sidebar.setManaged(false);
                sidebar.setTranslateX(0);
            });
            tt.play();
        } else {
            sidebar.setVisible(true);
            sidebar.setManaged(true);
            sidebar.setTranslateX(-sidebarWidth);
            tt.setFromX(-sidebarWidth);
            tt.setToX(0);
            tt.setOnFinished(ev -> sidebar.setTranslateX(0));
            tt.play();
        }
        sidebarVisible = !sidebarVisible;
    }

    private void setupSidebarActions() {
        btnDashboard.setOnAction(e -> navigateTo(Dompetku::showMahasiswaDashboardScene));
        btnTransaction.setOnAction(e -> navigateTo(Dompetku::showTransactionMahasiswa));
        btnWallet.setOnAction(e -> navigateTo(Dompetku::showWalletMahasiswaScene));
        btnPlanning.setOnAction(e -> updateSidebarActiveButton());
        btnHistory.setOnAction(e -> navigateTo(Dompetku::showHistoryMahasiswaScene));
        btnSettings.setOnAction(e -> showSettingsMenu());
    }

    private void updateSidebarActiveButton() {
        if (btnPlanning != null) {
            btnPlanning.getStyleClass().add("tombolSidebarAktif");
        }
    }

    private void navigateTo(NavigationAction action) {
        try {
            action.navigate();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal navigasi.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSettingsMenu() {
        ContextMenu settingsMenu = new ContextMenu();

        MenuItem logoutItem = new MenuItem("🚪 Logout");
        logoutItem.setOnAction(e -> handleLogout());

        MenuItem changePhotoItem = new MenuItem("📷 Ubah Foto Profil");
        changePhotoItem.setOnAction(e -> {
            try {
                pilihDanGantiFoto();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengganti foto profil.");
            }
        });

        MenuItem generateCodeItem = new MenuItem("🔗 Hubungkan ke Orang Tua");
        generateCodeItem.setOnAction(e -> showGenerateCodePopup());

        settingsMenu.getItems().addAll(changePhotoItem, generateCodeItem, new SeparatorMenuItem(), logoutItem);
        settingsMenu.show(btnSettings, javafx.geometry.Side.TOP, 0, 0);
    }

    private void handleLogout() {
        Session.clear();
        navigateTo(Dompetku::showLoginScene);
    }

    private void showGenerateCodePopup() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setTitle("Generate Kode");

        VBox content = new VBox(20);
        content.setPadding(new Insets(28));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(360);

        Label titleLabel = new Label("🔗 Hubungkan ke Orang Tua");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label("Generate kode unik untuk menghubungkan\nakun Anda dengan akun orang tua.");
        descLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-text-alignment: center;");
        descLabel.setWrapText(true);

        VBox codeBox = new VBox(8);
        codeBox.setAlignment(Pos.CENTER);
        codeBox.setStyle("-fx-background-color: #334155; -fx-background-radius: 12; -fx-padding: 16;");

        Label codeLabel = new Label("------");
        codeLabel.setStyle(
                "-fx-text-fill: #14b8a6; -fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        Label codeHintLabel = new Label("Berikan kode ini kepada orang tua Anda");
        codeHintLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        codeBox.getChildren().addAll(codeLabel, codeHintLabel);

        Button btnGenerate = new Button("🔄 Generate Kode");
        btnGenerate.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-padding: 12 24 12 24; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnGenerate.setOnAction(e -> {
            String code = generateRandomCode(6);
            codeLabel.setText(code);
        });

        Button btnClose = new Button("✕ Tutup");
        btnClose.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-padding: 10 20 10 20; -fx-font-size: 13px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> dialogStage.close());

        HBox buttonBox = new HBox(12, btnGenerate, btnClose);
        buttonBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(titleLabel, descLabel, codeBox, buttonBox);

        Scene scene = new Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    @FunctionalInterface
    private interface NavigationAction {
        void navigate() throws IOException;
    }
}
