package Proyek.Utils;

import java.util.Properties;
import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;

public class Email {

    private static final String MY_EMAIL = "dompet.ku.promlan@gmail.com";

    private static final String MY_PASSWORD = "yyyu jjrm qzqq xwjn";

    public static void sendPasswordEmail(String recipientEmail, String userPassword) throws Exception {
        System.out.println("Menyiapkan pengiriman email ke: " + recipientEmail);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, MY_PASSWORD);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MY_EMAIL, "Admin Dompetku"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Permintaan Pemulihan Akun - Dompetku");
        message.setSentDate(new Date());
        String htmlContent = """
                    <div style="font-family: Helvetica, Arial, sans-serif; font-size: 16px; margin: 0; color: #333; background-color: #f4f4f4; padding: 20px;">
                        <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">

                            <div style="text-align: center; padding-bottom: 20px; border-bottom: 2px solid #2E7D32;">
                                <h2 style="color: #2E7D32; margin: 0; font-size: 28px;">Dompetku</h2>
                                <p style="font-size: 14px; color: #777; margin-top: 5px;">Solusi Keuangan Anda</p>
                            </div>

                            <div style="padding: 20px 0; text-align: center;">
                                <p style="margin-bottom: 20px;">Halo,</p>
                                <p>Kami menerima permintaan untuk memulihkan password akun Anda.</p>
                                <p>Berikut adalah password Anda saat ini:</p>

                                <div style="background-color: #e8f5e9; border: 1px dashed #2E7D32; padding: 15px; width: fit-content; margin: 25px auto; border-radius: 5px;">
                                    <span style="font-size: 24px; font-weight: bold; letter-spacing: 2px; color: #1b5e20;">
                                        %s
                                    </span>
                                </div>

                                <p style="font-size: 14px; color: #555;">Silakan gunakan password di atas untuk login kembali ke aplikasi.</p>
                                <p style="font-size: 12px; color: #d32f2f; margin-top: 20px;">*Demi keamanan, kami sarankan Anda segera menghubungi admin untuk melakukan pergantian password ini setelah berhasil login.</p>
                            </div>

                            <div style="text-align: center; font-size: 11px; color: #999; border-top: 1px solid #eee; padding-top: 20px; margin-top: 20px;">
                                <p>&copy; 2026 Dompetku App. All rights reserved.</p>
                                <p>Email ini dikirim secara otomatis, mohon tidak membalas email ini.</p>
                            </div>
                        </div>
                    </div>
                """
                .formatted(userPassword);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
        System.out.println("Email sukses dikirim ke: " + recipientEmail);
    }
}


