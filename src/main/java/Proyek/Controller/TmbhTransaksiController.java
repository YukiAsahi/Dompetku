package Proyek.Controller;

import Proyek.Model.CatatanUang;
import Proyek.DAO.CatatanUangDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
public class TmbhTransaksiController {

    @FXML
    private TableView<CatatanUang> transactionTable;
    @FXML
    private RadioButton rbIncome;
    @FXML
    private RadioButton rbExpense;
    @FXML
    private TextField txtNominal;
    @FXML
    private ComboBox<String> comboDompet;
    @FXML
    private ComboBox<String> comboKategori;
    @FXML
    private TextArea txtCatatan;

    private ToggleGroup jenisGroup;
    private Stage dialogStage;
    private final int CURRENT_USER_ID = 1;

    @FXML
    public void initialize() {
        jenisGroup = new ToggleGroup();
        if (rbIncome != null)
            rbIncome.setToggleGroup(jenisGroup);
        if (rbExpense != null)
            rbExpense.setToggleGroup(jenisGroup);
        loadTransactionData();
    }

    private void loadTransactionData() {
        CatatanUangDAO dao = new CatatanUangDAO();
        List<CatatanUang> data = dao.ambilCatatanByUserId(CURRENT_USER_ID);
        if (transactionTable != null) {
            transactionTable.getItems().setAll(data);
        }
    }

    @FXML
    private void handleAddTransaction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Proyek/View/TmbhTransaksi.fxml"));
            Parent dialogRoot = loader.load();

            dialogStage = new Stage();
            dialogStage.setTitle("Tambah Transaksi");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            loadTransactionData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat dialog.");
        }
    }

    @FXML
    private void handleSaveTransaction() {
        if (txtNominal == null || txtNominal.getText().isEmpty() ||
                comboDompet == null || comboDompet.getValue() == null ||
                jenisGroup == null || jenisGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Mohon lengkapi semua field.");
            return;
        }

        try {
            double nominal = Double.parseDouble(txtNominal.getText());
            String jenis = rbIncome != null && rbIncome.isSelected() ? "INCOME" : "EXPENSE";

            CatatanUang newCatatan = new CatatanUang(1, CURRENT_USER_ID, jenis, nominal);
            newCatatan.setKategoriId(1);
            if (txtCatatan != null) {
                newCatatan.setCatatan(txtCatatan.getText());
            }

            CatatanUangDAO dao = new CatatanUangDAO();
            if (dao.tambahCatatan(newCatatan)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Transaksi berhasil disimpan!");
                handleCancelDialog();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan ke database.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Nominal harus berupa angka.");
        }
    }

    @FXML
    private void handleCancelDialog() {
        if (dialogStage != null) {
            dialogStage.close();
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
                    .add(getClass().getResource("/Proyek/css/TmbhTransaksi.css").toExternalForm());
            dialogPane.getStyleClass().add("my-alert");
        } catch (Exception e) {
        }
    }
}


