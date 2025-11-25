package Proyek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Dompetku extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginScene();
    }

    public static void showLoginScene() throws IOException {
        // Path berubah - ambil dari package Proyek.View
        FXMLLoader loader = new FXMLLoader(Dompetku.class.getResource("/Proyek/View/Login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Dompetku - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showRegisterScene() throws IOException {
        // Path berubah - ambil dari package Proyek.View
        FXMLLoader loader = new FXMLLoader(Dompetku.class.getResource("/Proyek/View/Register.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Dompetku - Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
