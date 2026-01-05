package Proyek.Controller;

import Proyek.DAO.CatatanUangDAO;
import Proyek.DAO.DompetDAO;
import Proyek.DAO.PenggunaDAO;
import Proyek.Dompetku;
import Proyek.Model.Dompet;
import Proyek.Model.Pengguna;
import Proyek.Session;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class WalletMahasiswaController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Circle imgProfileCircle;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPageTitle;
    @FXML
    private Button btnToggleSidebar;
    @FXML
    private Button btnDashboard, btnTransaction, btnWallet, btnPlanning, btnHistory, btnSettings;
    @FXML
    private Button btnNotification;
    @FXML
    private VBox walletContainer;
    @FXML
    private VBox emptyState;
    @FXML
    private FlowPane walletGrid;
    @FXML
    private Button btnAddFirstWallet;

    private final DompetDAO dompetDAO = new DompetDAO();
    private final CatatanUangDAO catatanUangDAO = new CatatanUangDAO();
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private int userId;
    private Pengguna pengguna;
    private boolean sidebarVisible = true;
    private double sidebarWidth = 260.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pengguna = Session.ambilPengguna();
        if (pengguna == null)
            return;

        userId = pengguna.getIdUser();
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

        loadWallets();
        if (btnNotification != null) {
            btnNotification.setOnAction(e -> showNotificationPopup());
        }
    }

    private void showNotificationPopup() {
        Proyek.DAO.BatasUangDAO batasUangDAO = new Proyek.DAO.BatasUangDAO();
        java.util.List<Proyek.Model.BatasUang> exceededBudgets = batasUangDAO.ambilBudgetMelebihi(pengguna.getIdUser());
        java.util.List<Proyek.Model.BatasUang> completedGoals = batasUangDAO.ambilGoalsTercapai(pengguna.getIdUser());

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
            for (Proyek.Model.BatasUang bu : exceededBudgets) {
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

            for (Proyek.Model.BatasUang g : completedGoals) {
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
                Session.updateProfileImage(fotoBytes); // Update cached image

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
        btnWallet.setOnAction(e -> {
            updateSidebarActiveButton();
            loadWallets();
        });
        btnPlanning.setOnAction(e -> navigateTo(Dompetku::showPlanningMahasiswaScene));
        btnHistory.setOnAction(e -> navigateTo(Dompetku::showHistoryMahasiswaScene));
        btnSettings.setOnAction(e -> showSettingsMenu());
    }

    private void updateSidebarActiveButton() {
        if (btnWallet != null) {
            btnWallet.getStyleClass().add("tombolSidebarAktif");
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

    private void loadWallets() {
        List<Dompet> wallets = dompetDAO.ambilDompetByUserId(userId);

        walletGrid.getChildren().clear();

        if (wallets == null || wallets.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            walletGrid.setVisible(false);
            walletGrid.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            walletGrid.setVisible(true);
            walletGrid.setManaged(true);

            for (Dompet dompet : wallets) {
                VBox walletCard = createWalletCard(dompet);
                walletGrid.getChildren().add(walletCard);
            }
            VBox addCard = createAddWalletCard();
            walletGrid.getChildren().add(addCard);
        }
    }

    private void showSettingsMenu() {
        ContextMenu settingsMenu = new ContextMenu();
        MenuItem logoutItem = new MenuItem("🚪 Logout");
        logoutItem.setOnAction(e -> {
            Session.clear();
            navigateTo(Dompetku::showLoginScene);
        });
        MenuItem changePhotoItem = new MenuItem("📷 Ubah Foto Profil");
        changePhotoItem.setOnAction(e -> handleChangeProfilePhoto());
        MenuItem generateCodeItem = new MenuItem("🔗 Hubungkan ke Orang Tua");
        generateCodeItem.setOnAction(e -> showGenerateCodePopup());

        settingsMenu.getItems().addAll(changePhotoItem, generateCodeItem, new SeparatorMenuItem(), logoutItem);
        settingsMenu.show(btnSettings, javafx.geometry.Side.TOP, 0, 0);
    }

    private void handleChangeProfilePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Profil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        Window window = rootPane.getScene() != null ? rootPane.getScene().getWindow() : null;
        File file = chooser.showOpenDialog(window);

        if (file != null) {
            try {
                byte[] photoBytes = Files.readAllBytes(file.toPath());
                pengguna.setFoto(photoBytes);
                boolean success = penggunaDAO.gantiFoto(pengguna.getIdUser(), photoBytes);
                if (success) {
                    Image img = new Image(new ByteArrayInputStream(photoBytes));
                    imgProfileCircle.setFill(new ImagePattern(img));
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Foto profil berhasil diubah.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah foto profil.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat gambar.");
            }
        }
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

    private VBox createWalletCard(Dompet dompet) {
        VBox cardContent = new VBox(8);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(16));
        if (dompet.getIcon() != null && dompet.getIcon().length > 0) {
            Circle iconCircle = new Circle(48);
            Image img = new Image(new ByteArrayInputStream(dompet.getIcon()));
            iconCircle.setFill(new ImagePattern(img));
            cardContent.getChildren().add(iconCircle);
        } else {
            Label iconLabel = new Label("💰");
            iconLabel.setStyle("-fx-font-size: 56px;");
            cardContent.getChildren().add(iconLabel);
        }
        Label nameLabel = new Label(dompet.getNamaDompet());
        nameLabel.getStyleClass().add("walletName");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        cardContent.getChildren().add(nameLabel);
        if (dompet.isUtama()) {
            Label badge = new Label("DEFAULT");
            badge.getStyleClass().add("walletDefaultBadge");
            cardContent.getChildren().add(badge);
        }
        Button menuButton = new Button();
        menuButton.getStyleClass().add("walletMenuButton");
        try {
            javafx.scene.image.ImageView threeDotsIcon = new javafx.scene.image.ImageView(
                    new Image(getClass().getResourceAsStream("/Proyek/image/three dots.png")));
            threeDotsIcon.setFitWidth(16);
            threeDotsIcon.setFitHeight(16);
            threeDotsIcon.setPreserveRatio(true);
            menuButton.setGraphic(threeDotsIcon);
        } catch (Exception e) {
            menuButton.setText("⋮"); // fallback to emoji if image fails
        }
        menuButton.setOnAction(e -> {
            e.consume();
            showWalletContextMenu(dompet, menuButton);
        });
        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setMargin(menuButton, new Insets(4, 4, 0, 0));
        StackPane card = new StackPane();
        card.getChildren().addAll(cardContent, menuButton);
        card.getStyleClass().add("walletCard");
        if (dompet.isUtama()) {
            card.getStyleClass().add("walletCardDefault");
        }
        card.setPrefWidth(180);
        card.setPrefHeight(180);
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != menuButton) {
                showWalletDetail(dompet);
            }
        });
        VBox wrapper = new VBox(card);
        wrapper.setPrefWidth(180);
        wrapper.setPrefHeight(180);

        return wrapper;
    }

    private void showWalletContextMenu(Dompet dompet, Button menuButton) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem("✏️ Ubah Nama");
        renameItem.setOnAction(e -> handleRenameWallet(dompet));
        MenuItem changeIconItem = new MenuItem("🖼️ Ubah Ikon");
        changeIconItem.setOnAction(e -> handleChangeIcon(dompet));
        MenuItem toggleDefaultItem;
        if (dompet.isUtama()) {
            toggleDefaultItem = new MenuItem("⭐ Hapus dari Default");
        } else {
            toggleDefaultItem = new MenuItem("⭐ Jadikan Default");
        }
        toggleDefaultItem.setOnAction(e -> handleToggleDefault(dompet));
        MenuItem deleteItem = new MenuItem("🗑️ Hapus");
        deleteItem.setOnAction(e -> handleDeleteWallet(dompet));

        contextMenu.getItems().addAll(renameItem, changeIconItem, toggleDefaultItem, new SeparatorMenuItem(),
                deleteItem);
        contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void handleRenameWallet(Dompet dompet) {
        TextInputDialog dialog = new TextInputDialog(dompet.getNamaDompet());
        dialog.setTitle("Ubah Nama Wallet");
        dialog.setHeaderText("Masukkan nama baru untuk wallet:");
        dialog.setContentText("Nama:");
        dialog.setGraphic(null);
        dialog.initStyle(StageStyle.UTILITY);
        styleDialog(dialog.getDialogPane());

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                dompet.setNamaDompet(newName.trim());
                boolean success = dompetDAO.ubahDompet(dompet);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Nama wallet berhasil diubah.");
                    loadWallets();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah nama wallet.");
                }
            }
        });
    }

    private void handleChangeIcon(Dompet dompet) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Icon Wallet");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        Window window = walletGrid.getScene() != null ? walletGrid.getScene().getWindow() : null;
        File file = chooser.showOpenDialog(window);

        if (file != null) {
            try {
                byte[] iconBytes = Files.readAllBytes(file.toPath());
                dompet.setIcon(iconBytes);
                boolean success = dompetDAO.ubahDompet(dompet);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Ikon wallet berhasil diubah.");
                    loadWallets();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah ikon wallet.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat gambar.");
            }
        }
    }

    private void handleToggleDefault(Dompet dompet) {
        if (dompet.isUtama()) {
            boolean success = dompetDAO.removeAsMainDompet(dompet.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Wallet berhasil dihapus dari default.");
                loadWallets();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus wallet dari default.");
            }
        } else {
            boolean success = dompetDAO.setAsMainDompet(dompet.getId(), userId);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Wallet berhasil dijadikan default.");
                loadWallets();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menjadikan wallet sebagai default.");
            }
        }
    }

    private void handleDeleteWallet(Dompet dompet) {
        if (dompet.isUtama()) {
            showAlert(Alert.AlertType.WARNING, "Tidak Dapat Dihapus",
                    "Wallet default tidak dapat dihapus. Jadikan wallet lain sebagai default terlebih dahulu.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Konfirmasi Hapus");
        confirmDialog.setHeaderText("Hapus Wallet: " + dompet.getNamaDompet());
        confirmDialog.setContentText("Apakah Anda yakin ingin menghapus wallet ini? Tindakan ini tidak dapat dibatalkan.");
        confirmDialog.setGraphic(null);
        styleDialog(confirmDialog.getDialogPane());

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = dompetDAO.hapusDompet(dompet.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Wallet berhasil dihapus.");
                    loadWallets();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus wallet.");
                }
            }
        });
    }

    private void showWalletDetail(Dompet dompet) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        double monthlyIncome = catatanUangDAO.getMonthlyIncomeByDompet(dompet.getId(), year, month);
        double monthlyExpense = catatanUangDAO.getMonthlyExpenseByDompet(dompet.getId(), year, month);
        double spendingRatio = 0;
        if (monthlyIncome > 0) {
            spendingRatio = monthlyExpense / monthlyIncome;
            if (spendingRatio > 1)
                spendingRatio = 1; // Cap at 100%
        }
        double remainingPercent = monthlyIncome > 0 ? ((monthlyIncome - monthlyExpense) / monthlyIncome) * 100 : 0;
        if (remainingPercent < 0)
            remainingPercent = 0;
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setTitle("Detail Wallet");

        VBox content = new VBox(20);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(380);
        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER);

        if (dompet.getIcon() != null && dompet.getIcon().length > 0) {
            Circle iconCircle = new Circle(40);
            Image img = new Image(new ByteArrayInputStream(dompet.getIcon()));
            iconCircle.setFill(new ImagePattern(img));
            titleBox.getChildren().add(iconCircle);
        } else {
            Label iconLabel = new Label("💰");
            iconLabel.setStyle("-fx-font-size: 48px;");
            titleBox.getChildren().add(iconLabel);
        }

        Label titleLabel = new Label(dompet.getNamaDompet());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        if (dompet.isUtama()) {
            Label badgeLabel = new Label("DEFAULT");
            badgeLabel.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-padding: 4 12 4 12; " +
                    "-fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            titleBox.getChildren().add(badgeLabel);
        }
        VBox balanceBox = new VBox(4);
        balanceBox.setAlignment(Pos.CENTER);
        Label balanceLabel = new Label("Total Saldo");
        balanceLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        Label balanceValue = new Label(String.format("Rp %,.0f", dompet.getSaldoSekarang()));
        balanceValue.setStyle("-fx-text-fill: #14b8a6; -fx-font-size: 32px; -fx-font-weight: bold;");
        balanceBox.getChildren().addAll(balanceLabel, balanceValue);
        VBox statsBox = new VBox(12);
        statsBox.setStyle("-fx-background-color: #334155; -fx-background-radius: 12; -fx-padding: 16;");

        Label monthTitle = new Label("📊 Pengeluaran Bulan Ini");
        monthTitle.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px; -fx-font-weight: bold;");
        HBox statsHeader = new HBox();
        statsHeader.setAlignment(Pos.CENTER_LEFT);

        VBox incomeInfo = new VBox(2);
        Label incomeLbl = new Label("Income");
        incomeLbl.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 11px;");
        Label incomeVal = new Label(String.format("Rp %,.0f", monthlyIncome));
        incomeVal.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");
        incomeInfo.getChildren().addAll(incomeLbl, incomeVal);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox expenseInfo = new VBox(2);
        expenseInfo.setAlignment(Pos.CENTER_RIGHT);
        Label expenseLbl = new Label("Outcome");
        expenseLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");
        Label expenseVal = new Label(String.format("Rp %,.0f", monthlyExpense));
        expenseVal.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold;");
        expenseInfo.getChildren().addAll(expenseLbl, expenseVal);

        statsHeader.getChildren().addAll(incomeInfo, spacer, expenseInfo);
        StackPane barContainer = new StackPane();
        barContainer.setAlignment(Pos.CENTER_LEFT);
        Region bgBar = new Region();
        bgBar.setPrefHeight(24);
        bgBar.setMaxWidth(Double.MAX_VALUE);
        bgBar.setStyle("-fx-background-color: #22c55e; -fx-background-radius: 12;");
        Region spentBar = new Region();
        spentBar.setPrefHeight(24);
        spentBar.setMaxWidth(spendingRatio * 316); // 316px max width
        spentBar.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 12 0 0 12;");
        StackPane.setAlignment(spentBar, Pos.CENTER_LEFT);

        barContainer.getChildren().addAll(bgBar, spentBar);
        HBox remainingBox = new HBox();
        remainingBox.setAlignment(Pos.CENTER);
        double remaining = monthlyIncome - monthlyExpense;
        String remainingText = remaining >= 0
                ? String.format("Sisa: Rp %,.0f (%.0f%%)", remaining, remainingPercent)
                : String.format("Melebihi: Rp %,.0f", Math.abs(remaining));
        Label remainingLabel = new Label(remainingText);
        remainingLabel.setStyle("-fx-text-fill: " + (remaining >= 0 ? "#22c55e" : "#ef4444") +
                "; -fx-font-size: 12px; -fx-font-weight: bold;");
        remainingBox.getChildren().add(remainingLabel);

        statsBox.getChildren().addAll(monthTitle, statsHeader, barContainer, remainingBox);
        Button btnClose = new Button("✕ Tutup");
        btnClose.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-padding: 12 32 12 32; -fx-font-size: 13px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> dialogStage.close());

        HBox buttonBox = new HBox(btnClose);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(8, 0, 0, 0));

        content.getChildren().addAll(titleBox, balanceBox, statsBox, buttonBox);

        Scene scene = new Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private VBox createAddWalletCard() {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("addWalletCard");
        card.setPrefWidth(180);
        card.setPrefHeight(180);

        Label plusIcon = new Label("+");
        plusIcon.getStyleClass().add("addWalletIcon");

        Label text = new Label("Tambah Wallet");
        text.getStyleClass().add("addWalletText");

        card.getChildren().addAll(plusIcon, text);
        card.setOnMouseClicked(e -> handleShowAddWalletDialog());

        return card;
    }

    @FXML
    private void handleShowAddWalletDialog() {
        showWalletDialog(null);
    }

    private void showWalletDialog(Dompet existingDompet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Proyek/View/TmbhWallet.fxml"));
            VBox dialogContent = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.setTitle(existingDompet == null ? "Tambah Wallet" : "Edit Wallet");

            Scene scene = new Scene(dialogContent);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            Label lblDialogTitle = (Label) dialogContent.lookup("#lblDialogTitle");
            TextField txtNamaWallet = (TextField) dialogContent.lookup("#txtNamaWallet");
            TextField txtSaldoAwal = (TextField) dialogContent.lookup("#txtSaldoAwal");
            StackPane iconPickerContainer = (StackPane) dialogContent.lookup("#iconPickerContainer");
            Circle circleIconPreview = (Circle) dialogContent.lookup("#circleIconPreview");
            Label lblIconPlaceholder = (Label) dialogContent.lookup("#lblIconPlaceholder");
            VBox vboxDefaultInfo = (VBox) dialogContent.lookup("#vboxDefaultInfo");
            Label lblDefaultInfo = (Label) dialogContent.lookup("#lblDefaultInfo");
            Button btnSave = (Button) dialogContent.lookup("#btnSave");
            Button btnCancel = (Button) dialogContent.lookup("#btnCancel");
            final byte[][] selectedIcon = { null };
            iconPickerContainer.setOnMouseClicked(e -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Pilih Icon Wallet");
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

                File file = chooser.showOpenDialog(dialogStage);

                if (file != null) {
                    try {
                        byte[] iconBytes = Files.readAllBytes(file.toPath());
                        selectedIcon[0] = iconBytes;
                        Image img = new Image(new ByteArrayInputStream(iconBytes));
                        circleIconPreview.setFill(new ImagePattern(img));
                        lblIconPlaceholder.setVisible(false);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat gambar.");
                    }
                }
            });
            List<Dompet> existingWallets = dompetDAO.ambilDompetByUserId(userId);
            boolean isFirstWallet = (existingWallets == null || existingWallets.isEmpty());

            if (existingDompet != null) {
                lblDialogTitle.setText("✏️ Edit Wallet");
                txtNamaWallet.setText(existingDompet.getNamaDompet());
                txtSaldoAwal.setText(String.valueOf((int) existingDompet.getSaldoSekarang()));
                txtSaldoAwal.setDisable(true);
                vboxDefaultInfo.setVisible(false);
                if (existingDompet.getIcon() != null && existingDompet.getIcon().length > 0) {
                    selectedIcon[0] = existingDompet.getIcon();
                    Image img = new Image(new ByteArrayInputStream(existingDompet.getIcon()));
                    circleIconPreview.setFill(new ImagePattern(img));
                    lblIconPlaceholder.setVisible(false);
                }
            } else {
                lblDialogTitle.setText("💳 Tambah Wallet Baru");
                if (isFirstWallet) {
                    lblDefaultInfo.setText("Wallet ini akan menjadi wallet default Anda");
                    vboxDefaultInfo.setVisible(true);
                } else {
                    vboxDefaultInfo.setVisible(false);
                }
            }

            btnCancel.setOnAction(e -> dialogStage.close());

            btnSave.setOnAction(e -> {
                String namaWallet = txtNamaWallet.getText().trim();
                if (namaWallet.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi", "Nama wallet harus diisi.");
                    return;
                }

                if (existingDompet != null) {
                    existingDompet.setNamaDompet(namaWallet);
                    if (selectedIcon[0] != null) {
                        existingDompet.setIcon(selectedIcon[0]);
                    }

                    boolean success = dompetDAO.ubahDompet(existingDompet);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Wallet berhasil diperbarui.");
                        dialogStage.close();
                        loadWallets();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal memperbarui wallet.");
                    }
                } else {
                    String saldoText = txtSaldoAwal.getText().replaceAll("[^0-9]", "");
                    double saldoAwal = saldoText.isEmpty() ? 0 : Double.parseDouble(saldoText);

                    String jenisDompet = isFirstWallet ? "Utama" : "Tabungan";

                    Dompet newDompet = new Dompet(userId, namaWallet, jenisDompet, saldoAwal);
                    if (selectedIcon[0] != null) {
                        newDompet.setIcon(selectedIcon[0]);
                    }

                    boolean success = dompetDAO.tambahDompet(newDompet);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Wallet berhasil ditambahkan.");
                        dialogStage.close();
                        loadWallets();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan wallet.");
                    }
                }
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka dialog.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title); // Use Title as Header
        alert.setContentText(message);
        alert.setGraphic(null); // Remove default icon
        styleDialog(alert.getDialogPane());

        alert.showAndWait();
    }
    private void styleDialog(DialogPane dialogPane) {
        try {
            dialogPane.getStylesheets()
                    .add(getClass().getResource("/Proyek/css/walletmahasiswa.css").toExternalForm());
            dialogPane.getStyleClass().add("my-alert");
        } catch (Exception e) {
        }
    }

    @FunctionalInterface
    private interface NavigationAction {
        void navigate() throws IOException;
    }
}


