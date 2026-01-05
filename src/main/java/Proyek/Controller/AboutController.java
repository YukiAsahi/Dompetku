package Proyek.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import Proyek.Dompetku;

public class AboutController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleHomeClick(MouseEvent event) throws IOException {
        Dompetku.showHomeScene();
    }

    @FXML
    private void handleContactClick(MouseEvent event) throws IOException {
        Dompetku.showContactScene();
    }
}


