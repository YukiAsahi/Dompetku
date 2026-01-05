package Proyek.Controller;

import Proyek.DAO.DashboardOrangTuaDAO;
import Proyek.DAO.HubungkanOrtuAnakDAO;
import Proyek.DAO.HubungkanOrtuAnakDAO.ChildInfo;
import Proyek.Dompetku;
import Proyek.Session;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
public class PilihAnakController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView bgImage;
    @FXML
    private Label lblWelcome;
    @FXML
    private ScrollPane childrenScrollPane;
    @FXML
    private VBox childrenList;
    @FXML
    private VBox noChildrenBox;
    @FXML
    private TextField codeField;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnLogout;
    @FXML
    private Label statusLabel;

    private HubungkanOrtuAnakDAO linkDAO;
    private DashboardOrangTuaDAO dao;

    public void initialize() {
        linkDAO = new HubungkanOrtuAnakDAO();
        dao = new DashboardOrangTuaDAO();
        if (bgImage != null && rootPane != null) {
            bgImage.fitWidthProperty().bind(rootPane.widthProperty());
            bgImage.fitHeightProperty().bind(rootPane.heightProperty());
        }
        String parentName = Session.ambilPengguna().getNamaLengkap();
        lblWelcome.setText("Selamat Datang, " + parentName + "!");
        btnConnect.setOnAction(e -> handleConnect());
        btnLogout.setOnAction(e -> handleLogout());
        loadConnectedChildren();
    }

    private void loadConnectedChildren() {
        childrenList.getChildren().clear();

        int parentId = Session.ambilPengguna().getIdUser();
        List<ChildInfo> children = linkDAO.getConnectedChildren(parentId);

        if (children.isEmpty()) {
            childrenScrollPane.setVisible(false);
            childrenScrollPane.setManaged(false);
            noChildrenBox.setVisible(true);
            noChildrenBox.setManaged(true);
        } else {
            childrenScrollPane.setVisible(true);
            childrenScrollPane.setManaged(true);
            noChildrenBox.setVisible(false);
            noChildrenBox.setManaged(false);

            for (ChildInfo child : children) {
                HBox childCard = createChildCard(child);
                childrenList.getChildren().add(childCard);
            }
        }
    }

    private HBox createChildCard(ChildInfo child) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.getStyleClass().add("child-card");
        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 24px;");
        Label nameLabel = new Label(child.namaLengkap);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);
        Button btnSelect = new Button("Pilih →");
        btnSelect.getStyleClass().add("select-button");
        btnSelect.setOnAction(e -> selectChild(child));

        card.getChildren().addAll(icon, nameLabel, btnSelect);
        card.setOnMouseClicked(e -> selectChild(child));
        card.setCursor(javafx.scene.Cursor.HAND);

        return card;
    }

    private void selectChild(ChildInfo child) {
        Session.setConnectedChild(child.idAnak, child.namaLengkap);
        try {
            Dompetku.showOrangTuaDashboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnect() {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            statusLabel.setText("Masukkan kode anak terlebih dahulu");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        int childId = dao.validateAndGetChildId(code);

        if (childId > 0) {
            int parentId = Session.ambilPengguna().getIdUser();
            if (linkDAO.isAlreadyConnected(parentId, childId)) {
                statusLabel.setText("Anda sudah terhubung dengan anak ini");
                statusLabel.setStyle("-fx-text-fill: #f59e0b;");
                return;
            }
            linkDAO.connectParent(parentId, code);
            String childName = dao.getChildName(childId);

            statusLabel.setText("Berhasil terhubung dengan " + childName + "!");
            statusLabel.setStyle("-fx-text-fill: #22c55e;");

            codeField.clear();
            loadConnectedChildren();
        } else {
            statusLabel.setText("Kode tidak valid atau sudah terpakai");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    private void handleLogout() {
        Session.clear();
        try {
            Dompetku.showLoginScene();
        } catch (IOException e) {
        }
    }
}


