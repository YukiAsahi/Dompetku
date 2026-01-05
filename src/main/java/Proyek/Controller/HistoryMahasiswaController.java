package Proyek.Controller;

import Proyek.DAO.KategoriDAO;
import Proyek.DAO.PenggunaDAO;
import Proyek.Dompetku;
import Proyek.Model.Kategori;
import Proyek.Model.Pengguna;
import Proyek.Session;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class HistoryMahasiswaController implements Initializable {

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
    private Label lblAddKategori;
    @FXML
    private VBox kategoriContainer;
    @FXML
    private VBox emptyState;
    @FXML
    private ScrollPane kategoriScrollPane;
    @FXML
    private VBox kategoriGrid;

    private final KategoriDAO kategoriDAO = new KategoriDAO();
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private Pengguna pengguna;
    private boolean sidebarVisible = true;
    private double sidebarWidth = 260.0;
    private byte[] selectedIconBytes = null;

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

        lblAddKategori.setOnMouseClicked(e -> showKategoriDialog(null));

        if (btnNotification != null) {
            btnNotification.setOnAction(e -> showNotificationPopup());
        }

        loadKategori();
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
        btnPlanning.setOnAction(e -> navigateTo(Dompetku::showPlanningMahasiswaScene));
        btnHistory.setOnAction(e -> {
            updateSidebarActiveButton();
            loadKategori();
        });
        btnSettings.setOnAction(e -> showSettingsMenu());
    }

    private void updateSidebarActiveButton() {
        if (btnHistory != null) {
            btnHistory.getStyleClass().add("tombolSidebarAktif");
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

    private void loadKategori() {
        List<Kategori> kategoriList = kategoriDAO.ambilKategoriAktif(pengguna.getIdUser());

        kategoriGrid.getChildren().clear();

        if (kategoriList == null || kategoriList.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            kategoriScrollPane.setVisible(false);
            kategoriScrollPane.setManaged(false);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            kategoriScrollPane.setVisible(true);
            kategoriScrollPane.setManaged(true);

            for (Kategori kategori : kategoriList) {
                VBox card = createKategoriCard(kategori);
                kategoriGrid.getChildren().add(card);
            }
        }
    }

    private VBox createKategoriCard(Kategori kategori) {
        HBox cardContent = new HBox(16);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        cardContent.setPadding(new Insets(12, 20, 12, 20));
        StackPane iconContainer = new StackPane();
        iconContainer.setMinSize(50, 50);
        iconContainer.setMaxSize(50, 50);

        if (kategori.getIkon() != null && kategori.getIkon().length > 0) {
            try {
                Image iconImage = new Image(new ByteArrayInputStream(kategori.getIkon()));
                ImageView iconView = new ImageView(iconImage);
                iconView.setFitWidth(48);
                iconView.setFitHeight(48);
                iconView.setPreserveRatio(true);
                Circle clip = new Circle(24, 24, 24);
                iconView.setClip(clip);
                iconContainer.getChildren().add(iconView);
            } catch (Exception e) {
                Label iconLabel = new Label("📁");
                iconLabel.getStyleClass().add("kategoriIcon");
                iconContainer.getChildren().add(iconLabel);
            }
        } else {
            Label iconLabel = new Label("📁");
            iconLabel.getStyleClass().add("kategoriIcon");
            iconContainer.getChildren().add(iconLabel);
        }
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        Label nameLabel = new Label(kategori.getNamaKategori());
        nameLabel.getStyleClass().add("kategoriName");
        nameLabel.setWrapText(false);
        Label badge = new Label(kategori.isPemasukan() ? "INCOME" : "EXPENSE");
        badge.getStyleClass().add(kategori.isPemasukan() ? "badgeIncome" : "badgeExpense");

        infoBox.getChildren().addAll(nameLabel, badge);

        cardContent.getChildren().addAll(iconContainer, infoBox);
        Button menuButton = new Button("⋮");
        menuButton.getStyleClass().add("kategoriMenuButton");
        menuButton.setOnAction(e -> {
            e.consume();
            showKategoriContextMenu(kategori, menuButton);
        });

        StackPane.setAlignment(menuButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(menuButton, new Insets(0, 12, 0, 0));

        StackPane card = new StackPane();
        card.getChildren().addAll(cardContent, menuButton);
        card.getStyleClass().add("kategoriCard");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(80);
        HBox.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);

        card.setOnMouseClicked(e -> {
            if (e.getTarget() != menuButton) {
                showKategoriDialog(kategori);
            }
        });

        VBox wrapper = new VBox(card);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.setPrefHeight(80);
        HBox.setHgrow(wrapper, javafx.scene.layout.Priority.ALWAYS);

        return wrapper;
    }

    private void showKategoriContextMenu(Kategori kategori, Button menuButton) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏️ Edit");
        editItem.setOnAction(e -> showKategoriDialog(kategori));

        MenuItem deleteItem = new MenuItem("🗑️ Hapus");
        deleteItem.setOnAction(e -> handleDeleteKategori(kategori));

        contextMenu.getItems().addAll(editItem, new SeparatorMenuItem(), deleteItem);
        contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void handleDeleteKategori(Kategori kategori) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Konfirmasi Hapus");
        confirmDialog.setHeaderText("Hapus Kategori: " + kategori.getNamaKategori());
        confirmDialog.setContentText("Apakah Anda yakin ingin menghapus kategori ini?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = kategoriDAO.nonaktifkanKategori(kategori.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kategori berhasil dihapus.");
                    loadKategori();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus kategori.");
                }
            }
        });
    }

    private void showKategoriDialog(Kategori existingKategori) {
        selectedIconBytes = (existingKategori != null) ? existingKategori.getIkon() : null;

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setTitle(existingKategori == null ? "Tambah Kategori" : "Edit Kategori");

        VBox content = new VBox(20);
        content.setPadding(new Insets(28));
        content.setAlignment(Pos.CENTER);
        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #002b23, #004d40); -fx-background-radius: 16;");
        content.setPrefWidth(380);
        Label titleLabel = new Label(existingKategori == null ? "➕ Tambah Kategori" : "✏️ Edit Kategori");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label iconInputLabel = new Label("Ikon Kategori");
        iconInputLabel.setStyle("-fx-text-fill: #a5f3fc; -fx-font-size: 12px; -fx-font-weight: bold;");
        StackPane iconPreviewContainer = new StackPane();
        iconPreviewContainer.setPrefSize(90, 90);
        iconPreviewContainer.setMaxSize(90, 90);
        iconPreviewContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 45;");
        StackPane iconContent = new StackPane();
        iconContent.setPrefSize(90, 90);
        updateIconPreview(iconContent, selectedIconBytes);

        iconPreviewContainer.getChildren().add(iconContent);
        Button btnPickIcon = new Button("📷 Pilih Gambar");
        btnPickIcon.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; -fx-cursor: hand;");

        btnPickIcon.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Pilih Ikon Kategori");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            File file = chooser.showOpenDialog(dialogStage);
            if (file != null) {
                try {
                    selectedIconBytes = compressImage(file, 128, 128);
                    updateIconPreview(iconContent, selectedIconBytes);
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat gambar.");
                }
            }
        });

        VBox iconBox = new VBox(8, iconInputLabel, iconPreviewContainer, btnPickIcon);
        iconBox.setAlignment(Pos.CENTER);
        Label nameInputLabel = new Label("Nama Kategori");
        nameInputLabel.setStyle("-fx-text-fill: #a5f3fc; -fx-font-size: 12px; -fx-font-weight: bold;");

        TextField txtName = new TextField();
        txtName.setPromptText("Contoh: Makanan, Gaji, dll");
        txtName.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 8; -fx-text-fill: white; -fx-padding: 12;");
        Label typeLabel = new Label("Jenis Kategori");
        typeLabel.setStyle("-fx-text-fill: #a5f3fc; -fx-font-size: 12px; -fx-font-weight: bold;");

        ToggleGroup typeGroup = new ToggleGroup();
        ToggleButton btnIncome = new ToggleButton("💰 Income");
        ToggleButton btnExpense = new ToggleButton("💸 Expense");
        btnIncome.setToggleGroup(typeGroup);
        btnExpense.setToggleGroup(typeGroup);

        String toggleStyle = "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-size: 13px; -fx-cursor: hand;";
        String toggleActiveStyle = "-fx-background-color: #14b8a6; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 20; -fx-font-size: 13px; -fx-cursor: hand;";

        btnIncome.setStyle(toggleStyle);
        btnExpense.setStyle(toggleStyle);

        btnIncome.selectedProperty()
                .addListener((obs, o, n) -> btnIncome.setStyle(n ? toggleActiveStyle : toggleStyle));
        btnExpense.selectedProperty()
                .addListener((obs, o, n) -> btnExpense.setStyle(n ? toggleActiveStyle : toggleStyle));

        HBox typeBox = new HBox(12, btnIncome, btnExpense);
        typeBox.setAlignment(Pos.CENTER);
        if (existingKategori != null) {
            txtName.setText(existingKategori.getNamaKategori());
            if (existingKategori.isPemasukan()) {
                btnIncome.setSelected(true);
            } else {
                btnExpense.setSelected(true);
            }
        } else {
            btnExpense.setSelected(true);
        }
        Button btnSave = new Button(existingKategori == null ? "💾 Simpan" : "💾 Update");
        btnSave.setStyle("-fx-background-color: linear-gradient(to right, #14b8a6, #0d9488); -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 12 28; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");

        Button btnCancel = new Button("✕ Batal");
        btnCancel.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 8; -fx-padding: 12 24; -fx-font-size: 14px; -fx-cursor: hand;");

        btnCancel.setOnAction(e -> dialogStage.close());

        btnSave.setOnAction(e -> {
            String name = txtName.getText().trim();

            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Nama kategori tidak boleh kosong.");
                return;
            }

            if (typeGroup.getSelectedToggle() == null) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih jenis kategori.");
                return;
            }

            String jenis = btnIncome.isSelected() ? "INCOME" : "EXPENSE";

            if (existingKategori != null) {
                existingKategori.setNamaKategori(name);
                existingKategori.setIkon(selectedIconBytes);
                existingKategori.setJenisKategori(jenis);

                boolean success = kategoriDAO.ubahKategori(existingKategori);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kategori berhasil diupdate.");
                    dialogStage.close();
                    loadKategori();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengupdate kategori.");
                }
            } else {
                Kategori newKategori = new Kategori(name, jenis, pengguna.getIdUser());
                newKategori.setIkon(selectedIconBytes);

                boolean success = kategoriDAO.tambahKategori(newKategori);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Kategori berhasil ditambahkan.");
                    dialogStage.close();
                    loadKategori();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambahkan kategori.");
                }
            }
        });

        HBox buttonBox = new HBox(12, btnSave, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);

        VBox nameBox = new VBox(4, nameInputLabel, txtName);

        VBox typeBoxWrapper = new VBox(4, typeLabel, typeBox);
        typeBoxWrapper.setAlignment(Pos.CENTER);

        content.getChildren().addAll(titleLabel, iconBox, nameBox, typeBoxWrapper, buttonBox);

        Scene scene = new Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void updateIconPreview(StackPane container, byte[] iconBytes) {
        container.getChildren().clear();

        if (iconBytes != null && iconBytes.length > 0) {
            try {
                Image iconImage = new Image(new ByteArrayInputStream(iconBytes));
                ImageView iconView = new ImageView(iconImage);
                iconView.setFitWidth(70);
                iconView.setFitHeight(70);
                iconView.setPreserveRatio(false); // Fill the circle area completely

                Circle clip = new Circle(35, 35, 35);
                iconView.setClip(clip);

                container.getChildren().add(iconView);
            } catch (Exception e) {
                addDefaultIconToContainer(container);
            }
        } else {
            addDefaultIconToContainer(container);
        }
    }

    private void addDefaultIconToContainer(StackPane container) {
        Label defaultIcon = new Label("📁");
        defaultIcon.setStyle("-fx-font-size: 36px;");
        container.getChildren().add(defaultIcon);
    }

    private void showSettingsMenu() {
        ContextMenu settingsMenu = new ContextMenu();

        MenuItem logoutItem = new MenuItem("🚪 Logout");
        logoutItem.setOnAction(e -> {
            Session.clear();
            navigateTo(Dompetku::showLoginScene);
        });

        MenuItem changePhotoItem = new MenuItem("📷 Ubah Foto Profil");
        changePhotoItem.setOnAction(e -> {
            try {
                pilihDanGantiFoto();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        MenuItem generateCodeItem = new MenuItem("🔗 Hubungkan ke Orang Tua");
        generateCodeItem.setOnAction(e -> showGenerateCodePopup());

        settingsMenu.getItems().addAll(changePhotoItem, generateCodeItem, new SeparatorMenuItem(), logoutItem);
        settingsMenu.show(btnSettings, javafx.geometry.Side.TOP, 0, 0);
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
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    interface NavigationAction {
        void navigate() throws IOException;
    }

    private byte[] compressImage(File file, int maxWidth, int maxHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);
        if (originalImage == null) {
            return Files.readAllBytes(file.toPath());
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);

        return baos.toByteArray();
    }
}
