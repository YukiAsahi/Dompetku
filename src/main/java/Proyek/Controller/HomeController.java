package Proyek.Controller;

import Proyek.Dompetku;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.io.IOException;
public class HomeController {

    @FXML
    private StackPane root;

    @FXML
    private ImageView bgImage;

    @FXML
    private Label navHome;

    @FXML
    public void initialize() {
        if (!navHome.getStyleClass().contains("nav-item-active")) {
            navHome.getStyleClass().add("nav-item-active");
        }
    }

    @FXML
    private void handleHomeClick() {
        try {
            Dompetku.showHomeScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAboutClick() {
        try {
            Dompetku.showAboutScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleContactClick() {
        try {
            Dompetku.showContactScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginClick(ActionEvent event) {
        try {
            Dompetku.showLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpClick(ActionEvent event) {
        try {
            Dompetku.showRegisterScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


