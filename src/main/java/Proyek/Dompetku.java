package Proyek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
public class Dompetku extends Application {

    private static Stage primaryStage;
    private static boolean firstShow = true; 

    @Override
    public void start(Stage stage) throws IOException {
        loadCustomFonts();

        primaryStage = stage;
        showHomeScene(); 
    }
    private void loadCustomFonts() {
        try {
            InputStream loraRegular = getClass().getResourceAsStream("/fonts/Lora/static/Lora-Regular.ttf");
            if (loraRegular != null) {
                Font font = Font.loadFont(loraRegular, 12);
                if (font != null) {
                    System.out.println("✓ Lora Regular loaded - Font family: " + font.getFamily());
                }
            } else {
                System.err.println("✗ Lora Regular font file not found");
            }
            InputStream loraBold = getClass().getResourceAsStream("/fonts/Lora/static/Lora-Bold.ttf");
            if (loraBold != null) {
                Font font = Font.loadFont(loraBold, 12);
                if (font != null) {
                    System.out.println("✓ Lora Bold loaded - Font family: " + font.getFamily());
                }
            } else {
                System.err.println("✗ Lora Bold font file not found");
            }
            InputStream loraItalic = getClass().getResourceAsStream("/fonts/Lora/static/Lora-Italic.ttf");
            if (loraItalic != null) {
                Font font = Font.loadFont(loraItalic, 12);
                if (font != null) {
                    System.out.println("✓ Lora Italic loaded - Font family: " + font.getFamily());
                }
            } else {
                System.err.println("✗ Lora Italic font file not found");
            }
            InputStream loraBoldItalic = getClass().getResourceAsStream("/fonts/Lora/static/Lora-BoldItalic.ttf");
            if (loraBoldItalic != null) {
                Font font = Font.loadFont(loraBoldItalic, 12);
                if (font != null) {
                    System.out.println("✓ Lora Bold Italic loaded - Font family: " + font.getFamily());
                }
            } else {
                System.err.println("✗ Lora Bold Italic font file not found");
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading fonts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void showHomeScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/HomeView.fxml"));
        Scene scene = new Scene(root);
        var cssUrl = Dompetku.class.getResource("/Proyek/css/home.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("DompetKu - Home");
        primaryStage.setScene(scene);
        if (firstShow) {
            primaryStage.setWidth(1365);
            primaryStage.setHeight(768);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(576);
            primaryStage.centerOnScreen();
            firstShow = false;
        }
        primaryStage.show();
    }

    public static void showAboutScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/AboutView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("DompetKu - About Us");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showContactScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/ContactView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("DompetKu - Contact");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(Dompetku.class.getResource("/Proyek/View/Login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Dompetku - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showRegisterScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(Dompetku.class.getResource("/Proyek/View/Register.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Dompetku - Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMahasiswaDashboardScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/MahasiswaDashboardView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Dashboard Mahasiswa");
        primaryStage.show();
    }
    public static void showPilihAnak() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/PilihAnakView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Pilih Anak");
        primaryStage.show();
    }

    public static void showOrangTuaDashboard() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/OrangTuaDashboardView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Dashboard Orang Tua");
        primaryStage.show();
    }
    public static void showOrangTuaDashboardScene() throws IOException {
        showPilihAnak(); 
    }
    public static void showTransactionMahasiswa() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/TransactionMahasiswaView.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Transaksi Mahasiswa");
        primaryStage.show();
    }
    public static void showWalletMahasiswaScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/WalletMahasiswaView.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Wallet Mahasiswa");
        primaryStage.show();
    }
    public static void showPlanningMahasiswaScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/PlanningMahasiswaView.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Planning Mahasiswa");
        primaryStage.show();
    }
    public static void showHistoryMahasiswaScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/HistoryMahasiswaView.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - History Mahasiswa");
        primaryStage.show();
    }

    public static void showAdminDashboardScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/AdminDashboardView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Dashboard Admin");
        primaryStage.show();
    }

    public static void showAdminUsersScene() throws IOException {
        Parent root = FXMLLoader.load(Dompetku.class.getResource("/Proyek/View/AdminUsersView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("DompetKu - Database Users");
        primaryStage.show();
    }

    public static void showAdminTrafficScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Dompetku.class.getResource("/Proyek/View/AdminJumlahPengunjungView.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Dompetku - Traffic Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAdminHistoryScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Dompetku.class.getResource("/Proyek/View/AdminHistoryView.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Dompetku - History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


