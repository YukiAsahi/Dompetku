package Proyek.Controller;

import Proyek.DAO.CatatanUangDAO;
import Proyek.DAO.DompetDAO;
import Proyek.DAO.PenggunaDAO;
import Proyek.DAO.KategoriDAO;
import Proyek.Dompetku;
import Proyek.Model.CatatanUang;
import Proyek.Model.Dompet;
import Proyek.Model.Kategori;
import Proyek.Model.Pengguna;
import Proyek.Session;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.application.Platform;

public class TransactionMahasiswaController implements Initializable {

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
    private TextField txtSearch;
    @FXML
    private TableView<CatatanUang> tblTransaksi;
    @FXML
    private TableColumn<CatatanUang, String> colTanggal, colDompet, colKategori, colCatatan, colNominal;
    @FXML
    private TableColumn<CatatanUang, Void> colSort;
    @FXML
    private TableColumn<CatatanUang, Void> colAksi;

    private final CatatanUangDAO catatanDAO = new CatatanUangDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();
    private final DompetDAO dompetDAO = new DompetDAO();

    private final ObservableList<CatatanUang> transactionList = FXCollections.observableArrayList();
    private FilteredList<CatatanUang> filteredList;

    private int userId;
    private Pengguna pengguna;
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    private boolean sidebarVisible = true;
    private double sidebarWidth = 260.0;
    private String dialogDefaultJenis = "Pemasukan";
    private String currentSortBy = "tanggal";
    private boolean sortAscending = false;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

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

        setupTableColumns();
        setupSearch();
        setupSidebarActions();
        setupSidebarToggle();
        setupSorting();

        updatePageTitle();
        updateSidebarActiveButton();
        loadTransactions();
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
        content.setPadding(new javafx.geometry.Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(380);
        content.setMaxHeight(450);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("\uD83D\uDD14 Notifikasi");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        javafx.scene.layout.Pane spacer = new javafx.scene.layout.Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
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
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

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
        btnTransaction.setOnAction(e -> {
            updatePageTitle();
            updateSidebarActiveButton();
            loadTransactions();
        });

        btnWallet.setOnAction(e -> navigateTo(Dompetku::showWalletMahasiswaScene));
        btnPlanning.setOnAction(e -> navigateTo(Dompetku::showPlanningMahasiswaScene));
        btnHistory.setOnAction(e -> navigateTo(Dompetku::showHistoryMahasiswaScene));

        btnSettings.setOnAction(e -> showSettingsMenu());
    }

    private void updateSidebarActiveButton() {
        if (btnTransaction == null)
            return;
        btnTransaction.getStyleClass().add("tombolSidebarAktif");
    }

    private void updatePageTitle() {
        if (lblPageTitle != null) {
            lblPageTitle.setText("TRANSACTION");
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

    private void setupTableColumns() {
        colTanggal.setCellValueFactory(cellData -> {
            LocalDateTime tanggal = cellData.getValue().getTanggalCatat();
            return new SimpleStringProperty(tanggal != null ? tanggal.format(DATE_FORMATTER) : "-");
        });
        colDompet.setCellValueFactory(cellData -> {
            String nama = cellData.getValue().getDompetNama();
            return new SimpleStringProperty(nama != null ? nama : "-");
        });
        colKategori.setCellValueFactory(cellData -> {
            CatatanUang c = cellData.getValue();
            if (c.isTransfer()) {
                return new SimpleStringProperty("Transfer");
            }
            String nama = c.getKategoriNama();
            return new SimpleStringProperty(nama != null ? nama : "-");
        });
        colCatatan.setCellValueFactory(cellData -> {
            String catatan = cellData.getValue().getCatatan();
            return new SimpleStringProperty(catatan != null && !catatan.isEmpty() ? catatan : "-");
        });
        colNominal.setCellValueFactory(cellData -> {
            CatatanUang c = cellData.getValue();
            double nominal = c.getNominal();
            String prefix;
            if (c.isTransfer()) {
                prefix = "↔ Rp ";
            } else if (c.isPemasukan()) {
                prefix = "+ Rp ";
            } else {
                prefix = "- Rp ";
            }
            return new SimpleStringProperty(prefix + String.format("%,.0f", nominal));
        });

        colNominal.setCellFactory(column -> new TableCell<CatatanUang, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    CatatanUang catatan = getTableView().getItems().get(getIndex());
                    if (catatan.isTransfer()) {
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold;");
                    } else if (catatan.isPemasukan()) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
        if (colAksi != null) {
            colAksi.setCellFactory(column -> new TableCell<CatatanUang, Void>() {
                private final Button btnEdit = new Button("✏ Edit");

                {
                    btnEdit.getStyleClass().add("tombolEdit");
                    btnEdit.setPrefWidth(90);
                    btnEdit.setPrefHeight(30);
                    btnEdit.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-color: #22c55e; -fx-text-fill: white; " +
                            "-fx-background-radius: 6;");
                    btnEdit.setFocusTraversable(false);

                    btnEdit.setOnAction(e -> {
                        try {
                            CatatanUang catatan = getTableView().getItems().get(getIndex());

                            if (catatan != null) {
                                System.out.println("Mengedit transaksi: " + catatan.getCatatan()); // Debugging di
                                showEditDeleteDialog(catatan);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Error",
                                    "Terjadi kesalahan saat membuka edit: " + ex.getMessage());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnEdit);
                        setAlignment(Pos.CENTER);
                    }
                }
            });
        }
        tblTransaksi.setRowFactory(tv -> {
            TableRow<CatatanUang> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CatatanUang catatan = row.getItem();
                    showEditDeleteDialog(catatan);
                }
            });
            return row;
        });
    }

    private void setupSearch() {
        filteredList = new FilteredList<>(transactionList, p -> true);
        tblTransaksi.setItems(filteredList);

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(catatan -> {
                if (newVal == null || newVal.isEmpty())
                    return true;
                String lower = newVal.toLowerCase();

                if (catatan.getCatatan() != null && catatan.getCatatan().toLowerCase().contains(lower))
                    return true;
                if (catatan.getKategoriNama() != null && catatan.getKategoriNama().toLowerCase().contains(lower))
                    return true;
                if (catatan.getJenisCatatan() != null && catatan.getJenisCatatan().toLowerCase().contains(lower))
                    return true;

                return false;
            });
        });
    }

    private void setupSorting() {
        currentSortBy = "tanggal";
        ComboBox<String> cmbSort = new ComboBox<>();
        cmbSort.setItems(FXCollections.observableArrayList(
                "Tanggal", "Kategori", "Dompet", "Jumlah"));
        cmbSort.setValue("Tanggal");
        cmbSort.getStyleClass().add("inputSortHeader");
        cmbSort.setPrefWidth(100);

        Button btnOrder = new Button("↓");
        btnOrder.getStyleClass().add("tombolSortHeader");
        cmbSort.setOnAction(e -> {
            String selected = cmbSort.getValue();
            if (selected != null) {
                switch (selected) {
                    case "Tanggal":
                        currentSortBy = "tanggal";
                        break;
                    case "Kategori":
                        currentSortBy = "kategori";
                        break;
                    case "Dompet":
                        currentSortBy = "dompet";
                        break;
                    case "Jumlah":
                        currentSortBy = "nominal";
                        break;
                    default:
                        currentSortBy = "tanggal";
                }
                loadTransactions();
            }
        });
        btnOrder.setOnAction(e -> {
            sortAscending = !sortAscending;
            btnOrder.setText(sortAscending ? "↑" : "↓");
            loadTransactions();
        });
        HBox sortBox = new HBox(8, cmbSort, btnOrder);
        sortBox.setAlignment(Pos.CENTER);
        colSort.setGraphic(sortBox);
        colSort.setText("");
    }

    private void handleEditSelected() {
        CatatanUang selected = tblTransaksi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih transaksi yang ingin diedit terlebih dahulu.");
            return;
        }
        showTransactionDialog(selected);
    }

    private void handleDeleteSelected() {
        CatatanUang selected = tblTransaksi.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih transaksi yang ingin dihapus terlebih dahulu.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Transaksi");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus transaksi ini?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = catatanDAO.hapusCatatan(selected.getId());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Transaksi berhasil dihapus.");
                loadTransactions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus transaksi.");
            }
        }
    }

    private void loadTransactions() {
        List<CatatanUang> sorted = catatanDAO.ambilCatatanSorted(userId, currentSortBy, sortAscending);
        transactionList.setAll(sorted != null ? sorted : new ArrayList<>());
    }

    @FXML
    private void handleShowAddDialog() {
        showTransactionDialog(null);
    }

    private void showTransactionDialog(CatatanUang existingCatatan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Proyek/View/TmbhTransaksi.fxml"));
            VBox dialogContent = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.setTitle(existingCatatan == null ? "Add Transaction" : "Edit Transaction");
            ScrollPane scrollPane = new ScrollPane(dialogContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.getStyleClass().add("edge-to-edge");

            scrollPane.setMaxHeight(600);

            Scene scene = new Scene(scrollPane);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/Proyek/css/TmbhTransaksi.css").toExternalForm());

            dialogStage.setScene(scene);
            Label lblDialogTitle = (Label) dialogContent.lookup("#lblDialogTitle");
            ToggleButton rbIncome = (ToggleButton) dialogContent.lookup("#rbIncome");
            ToggleButton rbExpense = (ToggleButton) dialogContent.lookup("#rbExpense");
            ToggleButton rbTransfer = (ToggleButton) dialogContent.lookup("#rbTransfer");
            ComboBox<Dompet> cmbDompetSumber = (ComboBox<Dompet>) dialogContent.lookup("#cmbDompetSumber");
            VBox vboxDompetTujuan = (VBox) dialogContent.lookup("#vboxDompetTujuan");
            ComboBox<Dompet> cmbDompetTujuan = (ComboBox<Dompet>) dialogContent.lookup("#cmbDompetTujuan");
            VBox vboxKategori = (VBox) dialogContent.lookup("#vboxKategori");
            TextField txtNominal = (TextField) dialogContent.lookup("#txtNominal");
            ComboBox<Kategori> cmbKategori = (ComboBox<Kategori>) dialogContent.lookup("#cmbKategori");
            DatePicker dpTanggal = (DatePicker) dialogContent.lookup("#dpTanggal");
            TextArea txtCatatan = (TextArea) dialogContent.lookup("#txtCatatan");
            Button btnSave = (Button) dialogContent.lookup("#btnSave");
            Button btnCancel = (Button) dialogContent.lookup("#btnCancel");
            VBox vboxBiayaAdmin = (VBox) dialogContent.lookup("#vboxBiayaAdmin");
            TextField txtBiayaAdmin = (TextField) dialogContent.lookup("#txtBiayaAdmin");
            ToggleGroup toggleGroup = new ToggleGroup();
            rbIncome.setToggleGroup(toggleGroup);
            rbExpense.setToggleGroup(toggleGroup);
            rbTransfer.setToggleGroup(toggleGroup);
            loadDompetToCombo(cmbDompetSumber);
            loadDompetToCombo(cmbDompetTujuan);
            Runnable updateUIForType = () -> {
                boolean isTransfer = rbTransfer.isSelected();
                vboxDompetTujuan.setVisible(isTransfer);
                vboxDompetTujuan.setManaged(isTransfer);
                vboxBiayaAdmin.setVisible(isTransfer);
                vboxBiayaAdmin.setManaged(isTransfer);
                vboxKategori.setVisible(!isTransfer);
                vboxKategori.setManaged(!isTransfer);

                if (!isTransfer) {
                    loadKategoriToCombo(cmbKategori, rbIncome.isSelected() ? "INCOME" : "EXPENSE");
                }
            };
            if (existingCatatan != null) {
                lblDialogTitle.setText("Edit Transaction");
                txtNominal.setText(String.valueOf((int) existingCatatan.getNominal()));
                txtCatatan.setText(existingCatatan.getCatatan());
                dpTanggal.setValue(existingCatatan.getTanggalCatat() != null
                        ? existingCatatan.getTanggalCatat().toLocalDate()
                        : LocalDate.now());

                if (existingCatatan.isTransfer()) {
                    rbTransfer.setSelected(true);
                } else if (existingCatatan.isPemasukan()) {
                    rbIncome.setSelected(true);
                } else {
                    rbExpense.setSelected(true);
                }
                for (Dompet d : cmbDompetSumber.getItems()) {
                    if (d.getId() == existingCatatan.getDompetId()) {
                        cmbDompetSumber.setValue(d);
                        break;
                    }
                }
                if (existingCatatan.getDompetTujuanId() != null) {
                    for (Dompet d : cmbDompetTujuan.getItems()) {
                        if (d.getId() == existingCatatan.getDompetTujuanId()) {
                            cmbDompetTujuan.setValue(d);
                            break;
                        }
                    }
                }

            } else {
                lblDialogTitle.setText("Add Transaction");
                dpTanggal.setValue(LocalDate.now());

                if ("Pengeluaran".equalsIgnoreCase(dialogDefaultJenis))
                    rbExpense.setSelected(true);
                else if ("Transfer".equalsIgnoreCase(dialogDefaultJenis))
                    rbTransfer.setSelected(true);
                else
                    rbIncome.setSelected(true);
            }

            updateUIForType.run();
            if (!rbTransfer.isSelected()) {
                loadKategoriToCombo(cmbKategori, rbIncome.isSelected() ? "INCOME" : "EXPENSE");
            }
            if (existingCatatan != null && !existingCatatan.isTransfer()) {
                for (Kategori k : cmbKategori.getItems()) {
                    if (k.getId() == existingCatatan.getKategoriId()) {
                        cmbKategori.setValue(k);
                        break;
                    }
                }
            }
            rbIncome.setOnAction(e -> updateUIForType.run());
            rbExpense.setOnAction(e -> updateUIForType.run());
            rbTransfer.setOnAction(e -> updateUIForType.run());

            btnCancel.setOnAction(e -> dialogStage.close());
            if (existingCatatan != null) {
                Button btnDelete = new Button("🗑 Delete");
                btnDelete.getStyleClass().add("tombolHapusKartu");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-font-weight: bold;");
                HBox buttonBox = (HBox) btnCancel.getParent();
                if (buttonBox != null) {
                    int saveIndex = buttonBox.getChildren().indexOf(btnSave);
                    buttonBox.getChildren().add(saveIndex, btnDelete);
                }
                final CatatanUang catatanToDelete = existingCatatan;
                btnDelete.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Konfirmasi Hapus");
                    confirm.setHeaderText("Hapus Transaksi");
                    confirm.setContentText("Yakin ingin menghapus transaksi ini? Saldo dompet akan disesuaikan.");

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        int dompetId = catatanToDelete.getDompetId();
                        Integer dompetTujuanId = catatanToDelete.getDompetTujuanId();
                        double nominal = catatanToDelete.getNominal();
                        String jenis = catatanToDelete.getJenisCatatan();

                        boolean success = catatanDAO.hapusCatatan(catatanToDelete.getId());
                        if (success) {
                            Dompet dompet = dompetDAO.ambilDompetById(dompetId);
                            if (dompet != null) {
                                double saldoBaru;
                                if ("Pemasukan".equalsIgnoreCase(jenis)) {
                                    saldoBaru = dompet.getSaldoSekarang() - nominal;
                                } else if ("Pengeluaran".equalsIgnoreCase(jenis)) {
                                    saldoBaru = dompet.getSaldoSekarang() + nominal;
                                } else if ("Transfer".equalsIgnoreCase(jenis)) {
                                    saldoBaru = dompet.getSaldoSekarang() + nominal;
                                } else {
                                    saldoBaru = dompet.getSaldoSekarang();
                                }
                                dompetDAO.ubahSaldo(dompetId, saldoBaru);
                            }
                            if ("Transfer".equalsIgnoreCase(jenis) && dompetTujuanId != null) {
                                Dompet dompetTujuan = dompetDAO.ambilDompetById(dompetTujuanId);
                                if (dompetTujuan != null) {
                                    double saldoTujuanBaru = dompetTujuan.getSaldoSekarang() - nominal;
                                    dompetDAO.ubahSaldo(dompetTujuanId, saldoTujuanBaru);
                                }
                            }

                            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Transaksi berhasil dihapus.");
                            dialogStage.close();
                            loadTransactions();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus transaksi.");
                        }
                    }
                });
            }

            btnSave.setOnAction(e -> {
                String nominalText = txtNominal.getText().replaceAll("[^0-9]", "");
                if (nominalText.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Amount is required.");
                    return;
                }

                double nominal = Double.parseDouble(nominalText);
                if (nominal <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Amount must be greater than 0.");
                    return;
                }

                Dompet dompetSumber = cmbDompetSumber.getValue();
                if (dompetSumber == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Please select source wallet.");
                    return;
                }

                boolean isTransfer = rbTransfer.isSelected();
                double biayaAdmin = 0;
                if (isTransfer) {
                    String biayaAdminText = txtBiayaAdmin.getText().replaceAll("[^0-9]", "");
                    if (!biayaAdminText.isEmpty()) {
                        biayaAdmin = Double.parseDouble(biayaAdminText);
                    }
                }
                double totalDeduction = nominal + biayaAdmin;

                if (isTransfer) {
                    Dompet dompetTujuan = cmbDompetTujuan.getValue();
                    if (dompetTujuan == null) {
                        showAlert(Alert.AlertType.WARNING, "Validation", "Please select destination wallet.");
                        return;
                    }
                    if (dompetSumber.getId() == dompetTujuan.getId()) {
                        showAlert(Alert.AlertType.WARNING, "Validation",
                                "Source and destination wallet cannot be the same.");
                        return;
                    }
                } else {
                    Kategori kategori = cmbKategori.getValue();
                    if (kategori == null) {
                        showAlert(Alert.AlertType.WARNING, "Validation", "Please select a category.");
                        return;
                    }
                }

                LocalDate tanggal = dpTanggal.getValue();
                if (tanggal == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Please select a date.");
                    return;
                }

                String jenis = rbIncome.isSelected() ? "Pemasukan"
                        : (rbExpense.isSelected() ? "Pengeluaran" : "Transfer");
                dialogDefaultJenis = jenis;
                String catatanText = txtCatatan.getText();
                if (isTransfer && biayaAdmin > 0) {
                    catatanText = (catatanText.isEmpty() ? "" : catatanText + " | ")
                            + "Biaya Admin: Rp" + String.format("%,.0f", biayaAdmin);
                }

                if (existingCatatan != null) {
                    double oldNominal = existingCatatan.getNominal();
                    int oldDompetId = existingCatatan.getDompetId();
                    Integer oldDompetTujuanId = existingCatatan.getDompetTujuanId();
                    String oldJenis = existingCatatan.getJenisCatatan();
                    double nominalToSave = isTransfer ? totalDeduction : nominal;
                    existingCatatan.setNominal(nominalToSave);
                    existingCatatan.setDompetId(dompetSumber.getId());
                    existingCatatan.setCatatan(catatanText);
                    existingCatatan.setTanggalCatat(tanggal.atStartOfDay());
                    existingCatatan.setJenisCatatan(jenis);

                    if (isTransfer) {
                        existingCatatan.setDompetTujuanId(cmbDompetTujuan.getValue().getId());
                        existingCatatan.setKategoriId(0);
                    } else {
                        existingCatatan.setKategoriId(cmbKategori.getValue().getId());
                        existingCatatan.setDompetTujuanId(null);
                    }

                    boolean success = catatanDAO.ubahCatatan(existingCatatan);
                    if (success) {
                        updateSaldoSetelahEdit(oldDompetId, oldDompetTujuanId, oldNominal, oldJenis,
                                dompetSumber.getId(), isTransfer ? cmbDompetTujuan.getValue().getId() : null,
                                nominalToSave, jenis);

                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Transaction updated and wallet balance adjusted.");
                        dialogStage.close();
                        loadTransactions();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update transaction.");
                    }

                } else {
                    double nominalToSave = isTransfer ? totalDeduction : nominal;
                    CatatanUang newCatatan = new CatatanUang(dompetSumber.getId(), userId, jenis, nominalToSave);
                    newCatatan.setCatatan(catatanText);
                    newCatatan.setTanggalCatat(tanggal.atStartOfDay());

                    if (isTransfer) {
                        newCatatan.setDompetTujuanId(cmbDompetTujuan.getValue().getId());
                        newCatatan.setKategoriId(0);
                    } else {
                        newCatatan.setKategoriId(cmbKategori.getValue().getId());
                    }

                    boolean success = catatanDAO.tambahCatatan(newCatatan);
                    if (success) {
                        updateSaldoSetelahAdd(dompetSumber.getId(),
                                isTransfer ? cmbDompetTujuan.getValue().getId() : null,
                                nominalToSave, jenis);

                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Transaction added and wallet balance updated.");
                        dialogStage.close();
                        loadTransactions();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to add transaction.");
                    }
                }
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dialog.");
        }
    }

    private void loadDompetToCombo(ComboBox<Dompet> combo) {
        List<Dompet> list = dompetDAO.ambilDompetByUserId(userId);
        if (list == null || list.isEmpty()) {
            Dompet mainDompet = dompetDAO.getMainDompetByUser(userId);
            if (mainDompet != null) {
                list = new ArrayList<>();
                list.add(mainDompet);
            }
        }

        combo.setItems(FXCollections.observableArrayList(list != null ? list : new ArrayList<>()));
        combo.setConverter(new javafx.util.StringConverter<Dompet>() {
            @Override
            public String toString(Dompet d) {
                return d != null ? d.getNamaDompet() : "";
            }

            @Override
            public Dompet fromString(String s) {
                return null;
            }
        });
        if (list != null && !list.isEmpty()) {
            for (Dompet d : list) {
                if (d.isUtama()) {
                    combo.setValue(d);
                    break;
                }
            }
            if (combo.getValue() == null) {
                combo.setValue(list.get(0));
            }
        }
    }

    private void loadKategoriToCombo(ComboBox<Kategori> combo, String jenis) {
        List<Kategori> list = "INCOME".equals(jenis)
                ? kategoriDAO.ambilKategoriPemasukan(userId)
                : kategoriDAO.ambilKategoriPengeluaran(userId);

        combo.setItems(FXCollections.observableArrayList(list));
        combo.setConverter(new javafx.util.StringConverter<Kategori>() {
            @Override
            public String toString(Kategori k) {
                return k != null ? k.getNamaKategori() : "";
            }

            @Override
            public Kategori fromString(String s) {
                return null;
            }
        });
    }

    private void showEditDeleteDialog(CatatanUang catatan) {
        showTransactionDialog(catatan);
    }

    private void handleEdit(CatatanUang catatan) {
        showTransactionDialog(catatan);
    }

    private void handleDelete(CatatanUang catatan) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Transaction");
        confirm.setContentText(
                "Are you sure you want to delete this transaction? This will also update your wallet balance.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int dompetId = catatan.getDompetId();
            Integer dompetTujuanId = catatan.getDompetTujuanId();
            double nominal = catatan.getNominal();
            String jenis = catatan.getJenisCatatan();

            boolean success = catatanDAO.hapusCatatan(catatan.getId());
            if (success) {
                Dompet dompet = dompetDAO.ambilDompetById(dompetId);
                if (dompet != null) {
                    double saldoBaru;
                    if ("Pemasukan".equalsIgnoreCase(jenis)) {
                        saldoBaru = dompet.getSaldoSekarang() - nominal;
                    } else if ("Pengeluaran".equalsIgnoreCase(jenis)) {
                        saldoBaru = dompet.getSaldoSekarang() + nominal;
                    } else if ("Transfer".equalsIgnoreCase(jenis)) {
                        saldoBaru = dompet.getSaldoSekarang() + nominal;
                    } else {
                        saldoBaru = dompet.getSaldoSekarang();
                    }
                    dompetDAO.ubahSaldo(dompetId, saldoBaru);
                }
                if ("Transfer".equalsIgnoreCase(jenis) && dompetTujuanId != null) {
                    Dompet dompetTujuan = dompetDAO.ambilDompetById(dompetTujuanId);
                    if (dompetTujuan != null) {
                        double saldoTujuanBaru = dompetTujuan.getSaldoSekarang() - nominal;
                        dompetDAO.ubahSaldo(dompetTujuanId, saldoTujuanBaru);
                    }
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction deleted and wallet balance updated.");
                loadTransactions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete transaction.");
            }
        }
    }

    private void updateSaldoSetelahEdit(int oldDompetId, Integer oldDompetTujuanId, double oldNominal, String oldJenis,
            int newDompetId, Integer newDompetTujuanId, double newNominal, String newJenis) {
        Dompet oldDompet = dompetDAO.ambilDompetById(oldDompetId);
        if (oldDompet != null) {
            double saldo = oldDompet.getSaldoSekarang();
            if ("Pemasukan".equalsIgnoreCase(oldJenis)) {
                saldo -= oldNominal; // batalkan income lama
            } else if ("Pengeluaran".equalsIgnoreCase(oldJenis)) {
                saldo += oldNominal; // batalkan expense lama
            } else if ("Transfer".equalsIgnoreCase(oldJenis)) {
                saldo += oldNominal; // kembalikan ke dompet sumber
            }
            dompetDAO.ubahSaldo(oldDompetId, saldo);
        }
        if ("Transfer".equalsIgnoreCase(oldJenis) && oldDompetTujuanId != null) {
            Dompet oldTujuan = dompetDAO.ambilDompetById(oldDompetTujuanId);
            if (oldTujuan != null) {
                double saldo = oldTujuan.getSaldoSekarang() - oldNominal;
                dompetDAO.ubahSaldo(oldDompetTujuanId, saldo);
            }
        }
        Dompet newDompet = dompetDAO.ambilDompetById(newDompetId);
        if (newDompet != null) {
            double saldo = newDompet.getSaldoSekarang();
            if ("Pemasukan".equalsIgnoreCase(newJenis)) {
                saldo += newNominal;
            } else if ("Pengeluaran".equalsIgnoreCase(newJenis)) {
                saldo -= newNominal;
            } else if ("Transfer".equalsIgnoreCase(newJenis)) {
                saldo -= newNominal; // kurangi dari dompet sumber
            }
            dompetDAO.ubahSaldo(newDompetId, saldo);
        }
        if ("Transfer".equalsIgnoreCase(newJenis) && newDompetTujuanId != null) {
            Dompet newTujuan = dompetDAO.ambilDompetById(newDompetTujuanId);
            if (newTujuan != null) {
                double saldo = newTujuan.getSaldoSekarang() + newNominal;
                dompetDAO.ubahSaldo(newDompetTujuanId, saldo);
            }
        }
    }

    private void updateSaldoSetelahAdd(int dompetId, Integer dompetTujuanId, double nominal, String jenis) {
        Dompet dompet = dompetDAO.ambilDompetById(dompetId);
        if (dompet != null) {
            double saldo = dompet.getSaldoSekarang();
            if ("Pemasukan".equalsIgnoreCase(jenis)) {
                saldo += nominal; // Pemasukan menambah saldo
            } else if ("Pengeluaran".equalsIgnoreCase(jenis)) {
                saldo -= nominal; // Pengeluaran mengurangi saldo
            } else if ("Transfer".equalsIgnoreCase(jenis)) {
                saldo -= nominal; // Transfer mengurangi saldo dari dompet sumber
            }
            dompetDAO.ubahSaldo(dompetId, saldo);
        }
        if ("Transfer".equalsIgnoreCase(jenis) && dompetTujuanId != null) {
            Dompet dompetTujuan = dompetDAO.ambilDompetById(dompetTujuanId);
            if (dompetTujuan != null) {
                double saldoTujuan = dompetTujuan.getSaldoSekarang() + nominal;
                dompetDAO.ubahSaldo(dompetTujuanId, saldoTujuan);
            }
        }
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
        content.setPadding(new javafx.geometry.Insets(28));
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
                    .add(getClass().getResource("/Proyek/css/transactionmahasiswa.css").toExternalForm());
            dialogPane.getStyleClass().add("my-alert");
        } catch (Exception e) {
        }
    }

    @FunctionalInterface
    private interface NavigationAction {
        void navigate() throws IOException;
    }
}
