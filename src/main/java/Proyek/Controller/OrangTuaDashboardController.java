package Proyek.Controller;

import Proyek.DAO.DashboardOrangTuaDAO;
import Proyek.DAO.HubungkanOrtuAnakDAO;
import Proyek.DAO.HubungkanOrtuAnakDAO.ChildInfo;
import Proyek.DAO.KategoriDAO;
import Proyek.Dompetku;
import Proyek.Model.BatasUang;
import Proyek.Model.CatatanUang;
import Proyek.Model.Kategori;
import Proyek.Session;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OrangTuaDashboardController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Label lblChildName;
    @FXML
    private Label lblConnectionStatus;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnTransaksi;
    @FXML
    private Button btnBudgeting;
    @FXML
    private Button btnGoals;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnNotifHeader;
    @FXML
    private StackPane mainContent;
    @FXML
    private VBox contentArea;
    @FXML
    private Label lblPageTitle;
    @FXML
    private VBox connectPanel;
    @FXML
    private VBox dashboardContent;
    @FXML
    private VBox budgetingContent;
    @FXML
    private VBox goalsContent;
    @FXML
    private VBox transactionHistoryContent;
    @FXML
    private VBox notificationContent;
    @FXML
    private TextField childCodeField;
    @FXML
    private Button connectButton;
    @FXML
    private Label statusLabel;
    @FXML
    private HBox summaryCards;
    @FXML
    private Label lblTotalSaldo;
    @FXML
    private Label lblMonthlyExpense;
    @FXML
    private Label lblMonthlyIncome;
    @FXML
    private Label lblTransactionCount;
    @FXML
    private PieChart pieExpenseByCategory;
    @FXML
    private VBox largeTransactionsList;
    @FXML
    private TableView<CatatanUang> transactionTable;
    @FXML
    private TableColumn<CatatanUang, String> colTanggal;
    @FXML
    private TableColumn<CatatanUang, String> colKategori;
    @FXML
    private TableColumn<CatatanUang, String> colDompet;
    @FXML
    private TableColumn<CatatanUang, String> colCatatan;
    @FXML
    private TableColumn<CatatanUang, String> colNominal;
    @FXML
    private TableColumn<CatatanUang, String> colJenis;
    @FXML
    private TableView<CatatanUang> fullTransactionTable;
    @FXML
    private TableColumn<CatatanUang, String> colFullTanggal;
    @FXML
    private TableColumn<CatatanUang, String> colFullKategori;
    @FXML
    private TableColumn<CatatanUang, String> colFullDompet;
    @FXML
    private TableColumn<CatatanUang, String> colFullCatatan;
    @FXML
    private TableColumn<CatatanUang, String> colFullNominal;
    @FXML
    private TableColumn<CatatanUang, String> colFullJenis;
    @FXML
    private Button btnAddBudget;
    @FXML
    private FlowPane budgetCardsPane;
    @FXML
    private FlowPane goalsCardsPane;
    @FXML
    private TextField thresholdField;
    @FXML
    private Button btnApplyThreshold;
    @FXML
    private VBox notificationList;
    @FXML
    private Button btnAddChild;
    @FXML
    private Button btnSwitchChild;
    private DashboardOrangTuaDAO dao;
    private HubungkanOrtuAnakDAO linkDAO;
    private KategoriDAO kategoriDAO;
    private double notificationThreshold = 500000;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public void initialize() {
        dao = new DashboardOrangTuaDAO();
        linkDAO = new HubungkanOrtuAnakDAO();
        kategoriDAO = new KategoriDAO();

        setupNavigation();
        setupTableColumns();
        if (Session.hasConnectedChild()) {
            showConnectedState();
            loadDashboardData();
        }
    }

    private void showChildSelectionPopup(List<ChildInfo> children) {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Pilih Anak");

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15; " +
                "-fx-border-color: #334155; -fx-border-radius: 15; -fx-border-width: 1;");

        Label titleLabel = new Label("👨‍👩‍👧‍👦 Pilih Anak");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label descLabel = new Label("Pilih anak yang ingin Anda pantau:");
        descLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        VBox childList = new VBox(10);
        childList.setAlignment(Pos.CENTER);

        for (ChildInfo child : children) {
            Button btnChild = new Button(child.namaLengkap);
            btnChild.setPrefWidth(250);
            btnChild.setStyle("-fx-background-color: #334155; -fx-text-fill: white; " +
                    "-fx-background-radius: 10; -fx-padding: 15 20; -fx-font-size: 14px; -fx-cursor: hand;");
            btnChild.setOnMouseEntered(e -> btnChild.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; " +
                    "-fx-background-radius: 10; -fx-padding: 15 20; -fx-font-size: 14px; -fx-cursor: hand;"));
            btnChild.setOnMouseExited(e -> btnChild.setStyle("-fx-background-color: #334155; -fx-text-fill: white; " +
                    "-fx-background-radius: 10; -fx-padding: 15 20; -fx-font-size: 14px; -fx-cursor: hand;"));
            btnChild.setOnAction(e -> {
                Session.setConnectedChild(child.idAnak, child.namaLengkap);
                dialogStage.close();
                showConnectedState();
                loadDashboardData();
            });
            childList.getChildren().add(btnChild);
        }
        Button btnAddNew = new Button("➕ Hubungkan Anak Baru");
        btnAddNew.setStyle("-fx-background-color: transparent; -fx-text-fill: #14b8a6; " +
                "-fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true;");
        btnAddNew.setOnAction(e -> {
            dialogStage.close();
        });

        content.getChildren().addAll(titleLabel, descLabel, childList, btnAddNew);

        javafx.scene.Scene scene = new javafx.scene.Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showConnectNewChildPopup() {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Hubungkan Anak Baru");

        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15; " +
                "-fx-border-color: #334155; -fx-border-radius: 15; -fx-border-width: 1;");

        Label titleLabel = new Label("➕ Hubungkan Anak Baru");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label("Masukkan kode yang diberikan anak:");
        descLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        TextField codeField = new TextField();
        codeField.setPromptText("Masukkan kode 6 karakter");
        codeField.setMaxWidth(200);
        codeField.setStyle("-fx-background-color: #334155; -fx-text-fill: white; " +
                "-fx-prompt-text-fill: #64748b; -fx-background-radius: 8; -fx-padding: 10;");

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");

        Button btnConnect = new Button("Hubungkan");
        btnConnect.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-size: 13px; -fx-cursor: hand;");
        btnConnect.setOnAction(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                resultLabel.setText("Masukkan kode terlebih dahulu");
                return;
            }

            int childId = dao.validateAndGetChildId(code);
            if (childId > 0) {
                int parentId = Session.ambilPengguna().getIdUser();
                if (linkDAO.isAlreadyConnected(parentId, childId)) {
                    resultLabel.setText("Anda sudah terhubung dengan anak ini");
                    return;
                }
                linkDAO.connectParent(parentId, code);
                String childName = dao.getChildName(childId);

                resultLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 12px;");
                resultLabel.setText("Berhasil terhubung dengan " + childName + "!");
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> {
                            dialogStage.close();
                            List<ChildInfo> children = linkDAO.getConnectedChildren(parentId);
                            if (children.size() > 1) {
                                showChildSelectionPopup(children);
                            }
                        });
                    } catch (InterruptedException ex) {
                    }
                }).start();
            } else {
                resultLabel.setText("Kode tidak valid atau sudah terpakai");
            }
        });

        Button btnCancel = new Button("Batal");
        btnCancel.setStyle("-fx-background-color: #475569; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 25; -fx-font-size: 13px; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> dialogStage.close());

        HBox buttonBox = new HBox(10, btnConnect, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(titleLabel, descLabel, codeField, resultLabel, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setupNavigation() {
        btnDashboard.setOnAction(e -> showDashboard());
        btnTransaksi.setOnAction(e -> showTransactionHistory());
        btnBudgeting.setOnAction(e -> showBudgeting());
        btnGoals.setOnAction(e -> showGoals());
        btnLogout.setOnAction(e -> handleLogout());

        if (btnAddBudget != null) {
            btnAddBudget.setOnAction(e -> showAddBudgetDialog());
        }
        if (btnApplyThreshold != null) {
            btnApplyThreshold.setOnAction(e -> applyNotificationThreshold());
        }
        if (btnAddChild != null) {
            btnAddChild.setOnAction(e -> showConnectNewChildPopup());
        }
        if (btnSwitchChild != null) {
            btnSwitchChild.setOnAction(e -> {
                int parentId = Session.ambilPengguna().getIdUser();
                List<ChildInfo> children = linkDAO.getConnectedChildren(parentId);
                if (children.size() > 1) {
                    showChildSelectionPopup(children);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Info", "Anda hanya memiliki 1 anak yang terhubung.");
                }
            });
        }
    }

    private void setActiveNavButton(Button activeBtn) {
        btnDashboard.getStyleClass().remove("active");
        btnTransaksi.getStyleClass().remove("active");
        btnBudgeting.getStyleClass().remove("active");
        btnGoals.getStyleClass().remove("active");

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("active");
        }
    }

    private void hideAllContent() {
        connectPanel.setVisible(false);
        connectPanel.setManaged(false);
        dashboardContent.setVisible(false);
        dashboardContent.setManaged(false);
        budgetingContent.setVisible(false);
        budgetingContent.setManaged(false);
        goalsContent.setVisible(false);
        goalsContent.setManaged(false);
        transactionHistoryContent.setVisible(false);
        transactionHistoryContent.setManaged(false);
        notificationContent.setVisible(false);
        notificationContent.setManaged(false);
    }

    @FXML
    private void handleConnect() {
        String code = childCodeField.getText().trim();

        if (code.isEmpty()) {
            statusLabel.setText("Mohon masukkan kode anak.");
            return;
        }

        int childId = dao.validateAndGetChildId(code);

        if (childId > 0) {
            int parentId = Session.ambilPengguna().getIdUser();
            linkDAO.connectParent(parentId, code);

            String childName = dao.getChildName(childId);
            Session.setConnectedChild(childId, childName);
            statusLabel.setText("");
            showConnectedState();
            loadDashboardData();
        } else {
            statusLabel.setText("Kode tidak ditemukan atau tidak valid.");
        }
    }

    private void showConnectedState() {
        if (lblChildName != null) {
            lblChildName.setText(Session.getConnectedChildName());
        }
        if (lblConnectionStatus != null) {
            lblConnectionStatus.setText("Terhubung ✓");
            lblConnectionStatus.setStyle("-fx-text-fill: #23e879;");
        }

        hideAllContent();
        dashboardContent.setVisible(true);
        dashboardContent.setManaged(true);
        setActiveNavButton(btnDashboard);
        lblPageTitle.setText("Dashboard - " + Session.getConnectedChildName());
    }

    private void showDashboard() {
        if (!Session.hasConnectedChild()) {
            hideAllContent();
            connectPanel.setVisible(true);
            connectPanel.setManaged(true);
            lblPageTitle.setText("Dashboard Orang Tua");
            return;
        }

        hideAllContent();
        dashboardContent.setVisible(true);
        dashboardContent.setManaged(true);
        setActiveNavButton(btnDashboard);
        lblPageTitle.setText("Dashboard - " + Session.getConnectedChildName());
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (!Session.hasConnectedChild())
            return;

        int childId = Session.getConnectedChildId();
        double totalBalance = dao.getChildTotalBalance(childId);
        double monthlyExpense = dao.getChildMonthlyExpense(childId);
        double monthlyIncome = dao.getChildMonthlyIncome(childId);
        int transactionCount = dao.getChildTransactionCount(childId);

        lblTotalSaldo.setText(formatRupiah(totalBalance));
        lblMonthlyExpense.setText(formatRupiah(monthlyExpense));
        lblMonthlyIncome.setText(formatRupiah(monthlyIncome));
        lblTransactionCount.setText(String.valueOf(transactionCount));
        loadExpensePieChart(childId);
        loadRecentTransactions(childId);
        loadLargeTransactionAlerts(childId);
    }

    private void loadExpensePieChart(int childId) {
        Map<String, Double> expenses = dao.getChildExpenseByCategory(childId);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : expenses.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieExpenseByCategory.setData(pieData);
    }

    private void loadRecentTransactions(int childId) {
        List<CatatanUang> transactions = dao.getChildTransactions(childId, 10);
        transactionTable.setItems(FXCollections.observableArrayList(transactions));
    }

    private void loadLargeTransactionAlerts(int childId) {
        List<CatatanUang> largeTransactions = dao.getChildLargeTransactions(childId, notificationThreshold);
        largeTransactionsList.getChildren().clear();

        if (largeTransactions.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada transaksi besar");
            emptyLabel.getStyleClass().add("empty-text");
            largeTransactionsList.getChildren().add(emptyLabel);
        } else {
            for (CatatanUang tx : largeTransactions) {
                largeTransactionsList.getChildren().add(createAlertItem(tx));
            }
        }
    }

    private HBox createAlertItem(CatatanUang tx) {
        HBox item = new HBox(15);
        item.getStyleClass().add("alert-item");
        item.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(3);
        Label categoryLabel = new Label(tx.getKategoriNama() != null ? tx.getKategoriNama() : "Unknown");
        categoryLabel.setStyle("-fx-font-weight: bold;");
        Label dateLabel = new Label(tx.getTanggalCatat() != null ? tx.getTanggalCatat().format(dateFormatter) : "");
        dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(categoryLabel, dateLabel);

        Label amountLabel = new Label(formatRupiah(tx.getNominal()));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        item.getChildren().addAll(infoBox, amountLabel);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        return item;
    }

    private void showTransactionHistory() {
        if (!Session.hasConnectedChild()) {
            showDashboard();
            return;
        }

        hideAllContent();
        transactionHistoryContent.setVisible(true);
        transactionHistoryContent.setManaged(true);
        setActiveNavButton(btnTransaksi);
        lblPageTitle.setText("Riwayat Transaksi - " + Session.getConnectedChildName());

        loadFullTransactionHistory();
    }

    private void loadFullTransactionHistory() {
        int childId = Session.getConnectedChildId();
        List<CatatanUang> transactions = dao.getChildTransactions(childId, 100);
        fullTransactionTable.setItems(FXCollections.observableArrayList(transactions));
    }

    private void showBudgeting() {
        if (!Session.hasConnectedChild()) {
            showDashboard();
            return;
        }

        hideAllContent();
        budgetingContent.setVisible(true);
        budgetingContent.setManaged(true);
        setActiveNavButton(btnBudgeting);
        lblPageTitle.setText("Budget Limits - " + Session.getConnectedChildName());

        loadBudgets();
    }

    private void loadBudgets() {
        int childId = Session.getConnectedChildId();
        List<BatasUang> budgets = dao.getChildBudgets(childId);

        budgetCardsPane.getChildren().clear();

        if (budgets.isEmpty()) {
            Label emptyLabel = new Label("Belum ada budget yang diatur");
            emptyLabel.getStyleClass().add("empty-text");
            budgetCardsPane.getChildren().add(emptyLabel);
        } else {
            for (BatasUang budget : budgets) {
                budgetCardsPane.getChildren().add(createBudgetCard(budget));
            }
        }
    }

    private VBox createBudgetCard(BatasUang budget) {
        VBox card = new VBox(10);
        card.getStyleClass().add("budget-card");
        card.setPadding(new Insets(15));

        Label nameLabel = new Label(budget.getKategoriNama());
        nameLabel.getStyleClass().add("budget-name");

        double spent = budget.getCurrentSpending();
        double max = budget.getMaxUang();
        double percentage = max > 0 ? (spent / max) : 0;

        ProgressBar progressBar = new ProgressBar(Math.min(percentage, 1.0));
        progressBar.setPrefWidth(250);
        progressBar.getStyleClass().add(percentage > 1 ? "budget-exceeded" : "budget-progress");

        Label statusLabel = new Label(formatRupiah(spent) + " / " + formatRupiah(max));
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (percentage > 1 ? "#e74c3c" : "#7f8c8d") + ";");

        String dateRange = "";
        if (budget.getMulaiDari() != null && budget.getSelesaiSampai() != null) {
            dateRange = budget.getMulaiDari().format(dateFormatter) + " - " +
                    budget.getSelesaiSampai().format(dateFormatter);
        }
        Label dateLabel = new Label(dateRange);
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");

        card.getChildren().addAll(nameLabel, progressBar, statusLabel, dateLabel);
        return card;
    }

    private void showAddBudgetDialog() {
        Dialog<BatasUang> dialog = new Dialog<>();
        dialog.setTitle("Tambah Budget");
        dialog.setHeaderText("Set batas pengeluaran untuk kategori");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Kategori> categoryCombo = new ComboBox<>();
        List<Kategori> categories = kategoriDAO.ambilKategoriPengeluaran(Session.getConnectedChildId());
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
        categoryCombo.setPromptText("Pilih Kategori");

        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Jumlah maksimal");

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusMonths(1));

        grid.add(new Label("Kategori:"), 0, 0);
        grid.add(categoryCombo, 1, 0);
        grid.add(new Label("Batas Maksimal:"), 0, 1);
        grid.add(maxAmountField, 1, 1);
        grid.add(new Label("Mulai:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("Selesai:"), 0, 3);
        grid.add(endDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    Kategori selectedCategory = categoryCombo.getValue();
                    double maxAmount = Double.parseDouble(maxAmountField.getText().replaceAll("[^0-9]", ""));
                    LocalDate startDate = startDatePicker.getValue();
                    LocalDate endDate = endDatePicker.getValue();

                    if (selectedCategory != null && maxAmount > 0) {
                        int childId = Session.getConnectedChildId();
                        boolean success = dao.setChildBudget(childId, selectedCategory.getId(),
                                maxAmount, startDate, endDate);
                        if (success) {
                            loadBudgets();
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Jumlah tidak valid");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showGoals() {
        if (!Session.hasConnectedChild()) {
            showDashboard();
            return;
        }

        hideAllContent();
        goalsContent.setVisible(true);
        goalsContent.setManaged(true);
        setActiveNavButton(btnGoals);
        lblPageTitle.setText("Target Tabungan - " + Session.getConnectedChildName());

        loadGoals();
    }

    private void loadGoals() {
        int childId = Session.getConnectedChildId();
        List<BatasUang> goals = dao.getChildGoals(childId);

        goalsCardsPane.getChildren().clear();

        if (goals.isEmpty()) {
            Label emptyLabel = new Label("Belum ada target tabungan");
            emptyLabel.getStyleClass().add("empty-text");
            goalsCardsPane.getChildren().add(emptyLabel);
        } else {
            for (BatasUang goal : goals) {
                goalsCardsPane.getChildren().add(createGoalCard(goal));
            }
        }
    }

    private VBox createGoalCard(BatasUang goal) {
        VBox card = new VBox(10);
        card.getStyleClass().add("goal-card");
        card.setPadding(new Insets(15));

        Label nameLabel = new Label(goal.getKategoriNama() != null ? goal.getKategoriNama() : "Goal");
        nameLabel.getStyleClass().add("goal-name");

        double progress = goal.getProgressTabungan();
        double target = goal.getMaxUang();
        double percentage = target > 0 ? (progress / target) : 0;

        ProgressBar progressBar = new ProgressBar(Math.min(percentage, 1.0));
        progressBar.setPrefWidth(250);
        progressBar.getStyleClass().add("goal-progress");

        Label statusLabel = new Label(formatRupiah(progress) + " / " + formatRupiah(target));
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label percentLabel = new Label(String.format("%.1f%%", percentage * 100));
        percentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        if (goal.getNamaDompet() != null) {
            Label walletLabel = new Label("Dompet: " + goal.getNamaDompet());
            walletLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
            card.getChildren().addAll(nameLabel, progressBar, statusLabel, percentLabel, walletLabel);
        } else {
            card.getChildren().addAll(nameLabel, progressBar, statusLabel, percentLabel);
        }

        return card;
    }

    @FXML
    private void showNotifications() {
        if (!Session.hasConnectedChild()) {
            showDashboard();
            return;
        }
        int childId = Session.getConnectedChildId();
        List<CatatanUang> largeTransactions = dao.getChildLargeTransactions(childId, notificationThreshold);
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Notifikasi");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(400);
        content.setMaxHeight(500);
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Notifikasi");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        javafx.scene.layout.Pane spacer = new javafx.scene.layout.Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button btnSettings = new Button("⚙");
        btnSettings.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 18px; -fx-cursor: hand;");
        btnSettings.setOnAction(ev -> {
            dialogStage.close();
            showNotificationSettings();
        });
        Button btnClose = new Button("✕");
        btnClose.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-cursor: hand;");
        btnClose.setOnAction(ev -> dialogStage.close());

        header.getChildren().addAll(titleLabel, spacer, btnSettings, btnClose);
        Label thresholdLabel = new Label("Transaksi > " + formatRupiah(notificationThreshold));
        thresholdLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        VBox notifList = new VBox(10);
        notifList.setAlignment(Pos.TOP_CENTER);

        if (largeTransactions.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada transaksi besar");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            notifList.getChildren().add(emptyLabel);
        } else {
            for (CatatanUang tx : largeTransactions) {
                VBox item = new VBox(4);
                item.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-border-color: #ef4444; " +
                        "-fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

                Label itemTitle = new Label("⚠️ Transaksi Besar");
                itemTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

                String kategori = tx.getKategoriNama() != null ? tx.getKategoriNama() : "Unknown";
                String tanggal = tx.getTanggalCatat() != null ? tx.getTanggalCatat().format(dateFormatter) : "";
                Label itemMsg = new Label(
                        String.format("%s - %s: %s", tanggal, kategori, formatRupiah(tx.getNominal())));
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

        content.getChildren().addAll(header, thresholdLabel, scrollPane);

        javafx.scene.Scene scene = new javafx.scene.Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showNotificationSettings() {
        hideAllContent();
        notificationContent.setVisible(true);
        notificationContent.setManaged(true);
        lblPageTitle.setText("Pengaturan Notifikasi - " + Session.getConnectedChildName());
        loadNotificationsInView();
    }

    private void loadNotificationsInView() {
        int childId = Session.getConnectedChildId();
        List<CatatanUang> largeTransactions = dao.getChildLargeTransactions(childId, notificationThreshold);

        notificationList.getChildren().clear();

        if (largeTransactions.isEmpty()) {
            Label emptyLabel = new Label(
                    "Tidak ada transaksi melebihi threshold " + formatRupiah(notificationThreshold));
            emptyLabel.getStyleClass().add("empty-text");
            notificationList.getChildren().add(emptyLabel);
        } else {
            for (CatatanUang tx : largeTransactions) {
                notificationList.getChildren().add(createNotificationItem(tx));
            }
        }
    }

    private void applyNotificationThreshold() {
        try {
            notificationThreshold = Double.parseDouble(thresholdField.getText().replaceAll("[^0-9]", ""));
            loadNotificationsInView();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Threshold tidak valid");
        }
    }

    private HBox createNotificationItem(CatatanUang tx) {
        HBox item = new HBox(20);
        item.getStyleClass().add("notification-item");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15));

        VBox infoBox = new VBox(5);
        Label categoryLabel = new Label(tx.getKategoriNama() != null ? tx.getKategoriNama() : "Unknown");
        categoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descLabel = new Label(tx.getCatatan() != null ? tx.getCatatan() : "");
        descLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Label dateLabel = new Label(tx.getTanggalCatat() != null ? tx.getTanggalCatat().format(dateFormatter) : "");
        dateLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

        infoBox.getChildren().addAll(categoryLabel, descLabel, dateLabel);

        Label amountLabel = new Label(formatRupiah(tx.getNominal()));
        amountLabel.getStyleClass().add("notification-amount");

        item.getChildren().addAll(infoBox, amountLabel);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

        return item;
    }

    private void setupTableColumns() {
        if (colTanggal != null) {
            colTanggal.setCellValueFactory(data -> {
                if (data.getValue().getTanggalCatat() != null) {
                    return new SimpleStringProperty(data.getValue().getTanggalCatat().format(dateFormatter));
                }
                return new SimpleStringProperty("");
            });
        }
        if (colKategori != null) {
            colKategori.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategoriNama()));
        }
        if (colDompet != null) {
            colDompet.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDompetNama()));
        }
        if (colCatatan != null) {
            colCatatan.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCatatan()));
        }
        if (colNominal != null) {
            colNominal
                    .setCellValueFactory(data -> new SimpleStringProperty(formatRupiah(data.getValue().getNominal())));
        }
        if (colJenis != null) {
            colJenis.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisCatatan()));
        }
        if (colFullTanggal != null) {
            colFullTanggal.setCellValueFactory(data -> {
                if (data.getValue().getTanggalCatat() != null) {
                    return new SimpleStringProperty(data.getValue().getTanggalCatat().format(dateFormatter));
                }
                return new SimpleStringProperty("");
            });
        }
        if (colFullKategori != null) {
            colFullKategori.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategoriNama()));
        }
        if (colFullDompet != null) {
            colFullDompet.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDompetNama()));
        }
        if (colFullCatatan != null) {
            colFullCatatan.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCatatan()));
        }
        if (colFullNominal != null) {
            colFullNominal
                    .setCellValueFactory(data -> new SimpleStringProperty(formatRupiah(data.getValue().getNominal())));
        }
        if (colFullJenis != null) {
            colFullJenis.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisCatatan()));
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            Dompetku.showLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal logout.");
        }
    }

    private String formatRupiah(double value) {
        return currencyFormat.format(value).replace("Rp", "Rp ");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
