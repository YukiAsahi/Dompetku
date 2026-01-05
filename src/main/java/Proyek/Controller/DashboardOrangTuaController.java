package Proyek.Controller;

import Proyek.DAO.DashboardOrangTuaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
public class DashboardOrangTuaController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private TextField kodeAnakField;

    @FXML
    private StackPane cardArea;
    @FXML
    private Label cardPlaceholder;

    private final DashboardOrangTuaDAO dao = new DashboardOrangTuaDAO();

    @FXML
    public void initialize() {
        if (bgImage != null && root != null) {
            bgImage.fitWidthProperty().bind(root.widthProperty());
            bgImage.fitHeightProperty().bind(root.heightProperty());
        }
    }

    @FXML
    private void handleConnect(ActionEvent event) {
        String kode = kodeAnakField.getText();

        if (kode == null || kode.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Form Error",
                    "Mohon isi kode anak terlebih dahulu.");
            return;
        }
        if (cardPlaceholder != null) {
            cardPlaceholder.setText("Terhubung dengan anak kode: " + kode);
        }

        showAlert(Alert.AlertType.INFORMATION, "Terhubung",
                "Berhasil menghubungkan ke akun anak dengan kode: " + kode);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


