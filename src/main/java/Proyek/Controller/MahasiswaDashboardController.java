package Proyek.Controller;

import Proyek.DAO.MahasiswaDashboardDAO;
import Proyek.DAO.HubungkanOrtuAnakDAO;
import Proyek.DAO.DompetDAO;
import Proyek.DAO.PenggunaDAO;
import Proyek.Model.Pengguna;
import Proyek.Session;
import Proyek.Dompetku;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.application.Platform;
public class MahasiswaDashboardController implements Initializable {
    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnToggleSidebar;
    @FXML
    private Label lblUsername;

    @FXML
    private Circle imgProfileCircle;

    @FXML
    private Button btnDashboard, btnTransaction, btnWallet, btnPlanning, btnHistory, btnSettings;

    @FXML
    private Button btnNotification;
    @FXML
    private LineChart<String, Number> lineCashFlowChart;

    @FXML
    private BarChart<String, Number> barMonthlySalesChart;

    @FXML
    private PieChart pieRemainingBudget;
    @FXML
    private ProgressBar pbRegionA, pbRegionB, pbRegionC, pbRegionD;

    @FXML
    private Label lblRegionAPercent, lblRegionBPercent, lblRegionCPercent, lblRegionDPercent;

    @FXML
    private Label lblCatA, lblCatB, lblCatC, lblCatD;
    @FXML
    private ProgressBar pbSavingNew, pbSavingRepeat, pbSavingVip;

    @FXML
    private Label lblSavingNew, lblSavingRepeat, lblSavingVip;

    @FXML
    private Label lblGoalA, lblGoalB, lblGoalC;
    @FXML
    private Label lblProductARemain, lblProductBRemain, lblProductCRemain, lblProductDRemain, lblProductERemain;

    @FXML
    private Label lblBudgetA, lblBudgetB, lblBudgetC, lblBudgetD, lblBudgetE;
    @FXML
    private Label lblTotalSaldo, lblPengeluaranBulan, lblPemasukanBulan, lblJumlahTransaksi;

    private Pengguna pengguna;
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private final MahasiswaDashboardDAO dashboardDAO = new MahasiswaDashboardDAO();
    private final HubungkanOrtuAnakDAO linkDAO = new HubungkanOrtuAnakDAO();

    private static final String[] NAMA_BULAN_SINGKAT = {
            "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    };
    private boolean sidebarVisible = true;
    private double sidebarWidth = 260.0; // default dari FXML

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pengguna = Session.ambilPengguna();

        if (pengguna == null) {
            try {
                Dompetku.showLoginScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
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
        initSidebarActions();
        initSidebarToggle();
        loadDashboardData();
        if (btnNotification != null) {
            btnNotification.setOnAction(e -> showNotificationPopup());
        }
    }

    private void showNotificationPopup() {
        Proyek.DAO.BatasUangDAO batasUangDAO = new Proyek.DAO.BatasUangDAO();
        java.util.List<Proyek.Model.BatasUang> exceededBudgets = batasUangDAO.ambilBudgetMelebihi(pengguna.getIdUser());
        java.util.List<Proyek.Model.BatasUang> completedGoals = batasUangDAO.ambilGoalsTercapai(pengguna.getIdUser());

        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Notifikasi");

        VBox content = new VBox(16);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(380);
        content.setMaxHeight(450);

        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label titleLabel = new Label("🔔 Notifikasi");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        javafx.scene.layout.Pane spacer = new javafx.scene.layout.Pane();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button btnClose = new Button("✕");
        btnClose.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 16px; -fx-cursor: hand;");
        btnClose.setOnAction(ev -> dialogStage.close());
        header.getChildren().addAll(titleLabel, spacer, btnClose);

        VBox notifList = new VBox(10);
        notifList.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        if (exceededBudgets.isEmpty() && completedGoals.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada notifikasi");
            emptyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            notifList.getChildren().add(emptyLabel);
        } else {
            for (Proyek.Model.BatasUang bu : exceededBudgets) {
                VBox item = new VBox(4);
                item.setStyle("-fx-background-color: rgba(239, 68, 68, 0.2); -fx-border-color: #ef4444; " +
                        "-fx-border-width: 0 0 0 3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
                Label itemTitle = new Label("⚠️ Budget Terlampaui!");
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
                Label itemTitle = new Label("🎉 Goals Tercapai!");
                itemTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
                Label itemMsg = new Label(String.format("Target %s Rp %,.0f tercapai!",
                        g.getKategoriNama(), g.getMaxUang()));
                itemMsg.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                itemMsg.setWrapText(true);
                item.getChildren().addAll(itemTitle, itemMsg);
                notifList.getChildren().add(item);
            }
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(notifList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        content.getChildren().addAll(header, scrollPane);

        javafx.scene.Scene scene = new javafx.scene.Scene(content);
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
    private void initSidebarToggle() {
        if (sidebar == null || btnToggleSidebar == null)
            return;
        if (sidebar.getPrefWidth() > 0) {
            sidebarWidth = sidebar.getPrefWidth();
        } else if (sidebar.getWidth() > 0) {
            sidebarWidth = sidebar.getWidth();
        }

        if (sidebarWidth <= 0) {
            sidebarWidth = 260.0;
        }

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
                sidebar.setManaged(false); // supaya content area melebar
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
    private void loadDashboardData() {
        int userId = pengguna.getIdUser();
        int year = Year.now().getValue();
        loadSummaryCards(userId);
        Map<Integer, Double> expensePerMonth = dashboardDAO.getExpensePerMonth(userId, year);
        updateLineCashFlowChart(expensePerMonth);
        Map<Integer, Double> incomePerMonth = dashboardDAO.getIncomePerMonth(userId, year);
        updateBarMonthlySalesChart(incomePerMonth);
        Map<String, Double> spendingPerCategory = dashboardDAO.getSpendingPerCategory(userId, year);
        updateSpendingWidgets(spendingPerCategory);
        Map<String, double[]> goalsProgress = dashboardDAO.getGoalsProgress(userId);
        updateGoalsProgress(goalsProgress);
        updateBudgetUsageWithSpending(spendingPerCategory);
    }

    private void loadSummaryCards(int userId) {
        java.text.NumberFormat cf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        double totalSaldo = dashboardDAO.getTotalSaldo(userId);
        if (lblTotalSaldo != null) {
            lblTotalSaldo.setText(cf.format(totalSaldo));
        }
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int year = Year.now().getValue();
        double monthExpense = dashboardDAO.getMonthExpense(userId, year, currentMonth);
        if (lblPengeluaranBulan != null) {
            lblPengeluaranBulan.setText(cf.format(monthExpense));
        }
        double monthIncome = dashboardDAO.getMonthIncome(userId, year, currentMonth);
        if (lblPemasukanBulan != null) {
            lblPemasukanBulan.setText(cf.format(monthIncome));
        }
        int transactionCount = dashboardDAO.getMonthTransactionCount(userId, year, currentMonth);
        if (lblJumlahTransaksi != null) {
            lblJumlahTransaksi.setText(String.valueOf(transactionCount));
        }
    }

    private void updateLineCashFlowChart(Map<Integer, Double> expensePerMonth) {
        if (lineCashFlowChart == null)
            return;

        lineCashFlowChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pengeluaran");

        for (int month = 1; month <= 12; month++) {
            String label = NAMA_BULAN_SINGKAT[month - 1];
            double value = expensePerMonth.getOrDefault(month, 0.0);
            series.getData().add(new XYChart.Data<>(label, value));
        }

        lineCashFlowChart.getData().add(series);
    }

    private void updateBarMonthlySalesChart(Map<Integer, Double> incomePerMonth) {
        if (barMonthlySalesChart == null)
            return;

        barMonthlySalesChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pemasukan");

        for (int month = 1; month <= 12; month++) {
            String label = NAMA_BULAN_SINGKAT[month - 1];
            double value = incomePerMonth.getOrDefault(month, 0.0);
            series.getData().add(new XYChart.Data<>(label, value));
        }

        barMonthlySalesChart.getData().add(series);
    }

    private void updateSpendingWidgets(Map<String, Double> spendingPerCategory) {
        resetCategoryLabels();

        if (spendingPerCategory == null || spendingPerCategory.isEmpty()) {
            if (pieRemainingBudget != null) {
                pieRemainingBudget.setData(FXCollections.observableArrayList());
            }
            resetRegionProgressBars();
            return;
        }

        double total = 0.0;
        for (double v : spendingPerCategory.values()) {
            total += v;
        }
        if (total <= 0) {
            resetRegionProgressBars();
            return;
        }

        if (pieRemainingBudget != null) {
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> e : spendingPerCategory.entrySet()) {
                if (e.getValue() <= 0)
                    continue;
                pieData.add(new PieChart.Data(e.getKey(), e.getValue()));
            }
            pieRemainingBudget.setData(pieData);
        }

        List<Map.Entry<String, Double>> list = new ArrayList<>(spendingPerCategory.entrySet());
        Label[] catLabels = { lblCatA, lblCatB, lblCatC, lblCatD };
        for (int i = 0; i < catLabels.length; i++) {
            if (catLabels[i] != null && i < list.size()) {
                catLabels[i].setText(truncateName(list.get(i).getKey(), 10));
            }
        }

        updateOneRegion(pbRegionA, lblRegionAPercent, list, 0, total);
        updateOneRegion(pbRegionB, lblRegionBPercent, list, 1, total);
        updateOneRegion(pbRegionC, lblRegionCPercent, list, 2, total);
        updateOneRegion(pbRegionD, lblRegionDPercent, list, 3, total);
    }

    private void updateGoalsProgress(Map<String, double[]> goalsProgress) {
        resetGoalsProgress();

        if (goalsProgress == null || goalsProgress.isEmpty()) {
            return;
        }

        List<Map.Entry<String, double[]>> list = new ArrayList<>(goalsProgress.entrySet());

        Label[] goalLabels = { lblGoalA, lblGoalB, lblGoalC };
        ProgressBar[] progressBars = { pbSavingNew, pbSavingRepeat, pbSavingVip };
        Label[] percentLabels = { lblSavingNew, lblSavingRepeat, lblSavingVip };

        for (int i = 0; i < goalLabels.length && i < list.size(); i++) {
            Map.Entry<String, double[]> entry = list.get(i);
            double target = entry.getValue()[0];
            double saldo = entry.getValue()[1];
            double percent = target > 0 ? Math.min(saldo / target, 1.0) : 0;

            if (goalLabels[i] != null) {
                goalLabels[i].setText(truncateName(entry.getKey(), 8));
            }
            if (progressBars[i] != null) {
                progressBars[i].setProgress(percent);
            }
            if (percentLabels[i] != null) {
                percentLabels[i].setText(String.format("%.0f%%", percent * 100));
            }
        }
    }

    private void updateBudgetUsage(Map<String, double[]> budgetUsage) {
        resetBudgetLabels();

        if (budgetUsage == null || budgetUsage.isEmpty()) {
            return;
        }

        List<Map.Entry<String, double[]>> list = new ArrayList<>(budgetUsage.entrySet());

        Label[] budgetLabels = { lblBudgetA, lblBudgetB, lblBudgetC, lblBudgetD, lblBudgetE };
        Label[] remainLabels = { lblProductARemain, lblProductBRemain, lblProductCRemain, lblProductDRemain,
                lblProductERemain };

        for (int i = 0; i < budgetLabels.length && i < list.size(); i++) {
            Map.Entry<String, double[]> entry = list.get(i);
            double budget = entry.getValue()[0];
            double spent = entry.getValue()[1];
            double remaining = Math.max(budget - spent, 0);

            if (budgetLabels[i] != null) {
                budgetLabels[i].setText(truncateName(entry.getKey(), 8));
            }
            if (remainLabels[i] != null) {
                remainLabels[i].setText(formatRupiah(remaining));
            }
        }
    }
    private void updateBudgetUsageWithSpending(Map<String, Double> spendingPerCategory) {
        if (spendingPerCategory == null || spendingPerCategory.isEmpty()) {
            if (lblBudgetA != null)
                lblBudgetA.setText("-");
            if (lblBudgetB != null)
                lblBudgetB.setText("-");
            if (lblBudgetC != null)
                lblBudgetC.setText("-");
            if (lblBudgetD != null)
                lblBudgetD.setText("-");
            if (lblBudgetE != null)
                lblBudgetE.setText("-");
            if (lblProductARemain != null)
                lblProductARemain.setText("0");
            if (lblProductBRemain != null)
                lblProductBRemain.setText("0");
            if (lblProductCRemain != null)
                lblProductCRemain.setText("0");
            if (lblProductDRemain != null)
                lblProductDRemain.setText("0");
            if (lblProductERemain != null)
                lblProductERemain.setText("0");
            if (pieRemainingBudget != null) {
                pieRemainingBudget.setData(FXCollections.observableArrayList());
            }
            return;
        }

        List<Map.Entry<String, Double>> list = new ArrayList<>(spendingPerCategory.entrySet());
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(100), event -> {
                    Label[] budgetLabels = { lblBudgetA, lblBudgetB, lblBudgetC, lblBudgetD, lblBudgetE };
                    Label[] remainLabels = { lblProductARemain, lblProductBRemain, lblProductCRemain,
                            lblProductDRemain, lblProductERemain };

                    for (int i = 0; i < budgetLabels.length; i++) {
                        if (i < list.size()) {
                            Map.Entry<String, Double> entry = list.get(i);
                            if (budgetLabels[i] != null) {
                                budgetLabels[i].setText(truncateName(entry.getKey(), 10));
                            }
                            if (remainLabels[i] != null) {
                                remainLabels[i].setText(formatRupiah(entry.getValue()));
                            }
                        } else {
                            if (budgetLabels[i] != null)
                                budgetLabels[i].setText("-");
                            if (remainLabels[i] != null)
                                remainLabels[i].setText("0");
                        }
                    }
                    if (pieRemainingBudget != null) {
                        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                        for (Map.Entry<String, Double> e : list) {
                            if (e.getValue() > 0) {
                                pieData.add(new PieChart.Data(e.getKey(), e.getValue()));
                            }
                        }
                        pieRemainingBudget.setData(pieData);
                    }
                }));
        timeline.play();
    }

    private String truncateName(String name, int maxLen) {
        if (name == null)
            return "-";
        return name.length() > maxLen ? name.substring(0, maxLen) + ".." : name;
    }

    private void updateOneRegion(ProgressBar pb, Label lblPercent,
            List<Map.Entry<String, Double>> list,
            int index, double total) {
        if (pb == null || lblPercent == null)
            return;

        if (index < list.size()) {
            double value = list.get(index).getValue();
            double percent = value / total;
            pb.setProgress(percent);
            lblPercent.setText(String.format("%.0f%%", percent * 100));
        } else {
            pb.setProgress(0);
            lblPercent.setText("0%");
        }
    }

    private void resetCategoryLabels() {
        if (lblCatA != null)
            lblCatA.setText("-");
        if (lblCatB != null)
            lblCatB.setText("-");
        if (lblCatC != null)
            lblCatC.setText("-");
        if (lblCatD != null)
            lblCatD.setText("-");
    }

    private void resetGoalsProgress() {
        if (lblGoalA != null)
            lblGoalA.setText("-");
        if (lblGoalB != null)
            lblGoalB.setText("-");
        if (lblGoalC != null)
            lblGoalC.setText("-");
        if (pbSavingNew != null)
            pbSavingNew.setProgress(0);
        if (pbSavingRepeat != null)
            pbSavingRepeat.setProgress(0);
        if (pbSavingVip != null)
            pbSavingVip.setProgress(0);
        if (lblSavingNew != null)
            lblSavingNew.setText("0%");
        if (lblSavingRepeat != null)
            lblSavingRepeat.setText("0%");
        if (lblSavingVip != null)
            lblSavingVip.setText("0%");
    }

    private void resetBudgetLabels() {
        new Exception("Stack trace for resetBudgetLabels").printStackTrace();
        if (lblBudgetA != null)
            lblBudgetA.setText("-");
        if (lblBudgetB != null)
            lblBudgetB.setText("-");
        if (lblBudgetC != null)
            lblBudgetC.setText("-");
        if (lblBudgetD != null)
            lblBudgetD.setText("-");
        if (lblBudgetE != null)
            lblBudgetE.setText("-");
        if (lblProductARemain != null)
            lblProductARemain.setText("0");
        if (lblProductBRemain != null)
            lblProductBRemain.setText("0");
        if (lblProductCRemain != null)
            lblProductCRemain.setText("0");
        if (lblProductDRemain != null)
            lblProductDRemain.setText("0");
        if (lblProductERemain != null)
            lblProductERemain.setText("0");
    }

    private void resetRegionProgressBars() {
        if (pbRegionA != null)
            pbRegionA.setProgress(0);
        if (pbRegionB != null)
            pbRegionB.setProgress(0);
        if (pbRegionC != null)
            pbRegionC.setProgress(0);
        if (pbRegionD != null)
            pbRegionD.setProgress(0);

        if (lblRegionAPercent != null)
            lblRegionAPercent.setText("0%");
        if (lblRegionBPercent != null)
            lblRegionBPercent.setText("0%");
        if (lblRegionCPercent != null)
            lblRegionCPercent.setText("0%");
        if (lblRegionDPercent != null)
            lblRegionDPercent.setText("0%");
    }

    private String formatRupiah(double value) {
        return String.format("%,.0f", value);
    }
    private void initSidebarActions() {
        btnDashboard.setOnAction(e -> {
            try {
                Dompetku.showMahasiswaDashboardScene();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka dashboard.");
            }
        });

        btnTransaction.setOnAction(e -> {
            try {
                Dompetku.showTransactionMahasiswa();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Transaksi.");
            }
        });

        btnWallet.setOnAction(e -> {
            try {
                Dompetku.showWalletMahasiswaScene();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman wallet.");
            }
        });

        btnPlanning.setOnAction(e -> {
            try {
                Dompetku.showPlanningMahasiswaScene();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman planning.");
            }
        });

        btnHistory.setOnAction(e -> {
            try {
                Dompetku.showHistoryMahasiswaScene();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman riwayat transaksi.");
            }
        });

        btnSettings.setOnAction(e -> showSettingsMenu());
    }

    private void handleLogout() {
        try {
            Session.clear();
            Dompetku.showLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal logout.");
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

    private void showGenerateCodePopup() {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Generate Kode");

        VBox content = new VBox(20);
        content.setPadding(new javafx.geometry.Insets(28));
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 16;");
        content.setPrefWidth(360);

        Label titleLabel = new Label("🔗 Hubungkan ke Orang Tua");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label("Generate kode unik untuk menghubungkan\nakun Anda dengan akun orang tua.");
        descLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-text-alignment: center;");
        descLabel.setWrapText(true);

        VBox codeBox = new VBox(8);
        codeBox.setAlignment(javafx.geometry.Pos.CENTER);
        codeBox.setStyle("-fx-background-color: #334155; -fx-background-radius: 12; -fx-padding: 16;");
        String existingCode = linkDAO.getChildCode(pengguna.getIdUser());
        Label codeLabel = new Label(existingCode != null && !existingCode.isEmpty() ? existingCode : "------");
        codeLabel.setStyle(
                "-fx-text-fill: #14b8a6; -fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");

        Label codeHintLabel = new Label("Berikan kode ini kepada orang tua Anda");
        codeHintLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        codeBox.getChildren().addAll(codeLabel, codeHintLabel);
        Button btnGenerate = new Button(
                existingCode != null && !existingCode.isEmpty() ? "🔄 Generate Ulang" : "🔄 Generate Kode");
        btnGenerate.setStyle("-fx-background-color: #14b8a6; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-padding: 12 24 12 24; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnGenerate.setOnAction(e -> {
            System.out.flush();

            if (linkDAO == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal generate kode. linkDAO null.");
                return;
            }
            System.out.flush();
            String newCode = linkDAO.generateCode(pengguna.getIdUser());
            System.out.flush();
            if (newCode != null) {
                codeLabel.setText(newCode);
                btnGenerate.setText("🔄 Generate Ulang");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal generate kode. Coba lagi.");
            }
        });

        Button btnClose = new Button("✕ Tutup");
        btnClose.setStyle("-fx-background-color: #475569; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-padding: 10 20 10 20; -fx-font-size: 13px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> dialogStage.close());

        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(12, btnGenerate, btnClose);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        content.getChildren().addAll(titleLabel, descLabel, codeBox, buttonBox);

        javafx.scene.Scene scene = new javafx.scene.Scene(content);
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
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


