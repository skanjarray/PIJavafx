package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import models.Livraison;
import models.SocieteRecyclage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    // Generate QR code for Livraison and save to file
    public static String generateQRCodeForLivraison(Livraison livraison, String filePath) {
        String qrCodeData = String.format(
                "ID: %d\nProduit: %s\nPoids: %.2f kg\nDate: %s\nSociété: %s\nCode Unique: LIV-%d-%s",
                livraison.getId(),
                livraison.getProduit(),
                livraison.getPoids(),
                livraison.getDate().toString(),
                livraison.getSocieteRecyclage().getNom(),
                livraison.getId(),
                java.util.UUID.randomUUID().toString().substring(0, 8) // Add unique identifier
        );

        try {
            // Increased size for better quality
            generateQRCode(qrCodeData, filePath, 300, 300);
            return filePath;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate QR code for SocieteRecyclage and save to file
    public static String generateQRCodeForSociete(SocieteRecyclage societe, String filePath) {
        String qrCodeData = String.format(
                "ID: %d\nNom: %s\nAdresse: %s\nEmail: %s\nCode Unique: SOC-%d-%s",
                societe.getId(),
                societe.getNom(),
                societe.getAdresse(),
                societe.getEmail(),
                societe.getId(),
                java.util.UUID.randomUUID().toString().substring(0, 8) // Add unique identifier
        );

        try {
            // Increased size for better quality
            generateQRCode(qrCodeData, filePath, 300, 300);
            return filePath;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate QR code as JavaFX Image for display in TableView
    public static Image generateQRCodeImage(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageData = outputStream.toByteArray();

            return new Image(new ByteArrayInputStream(imageData));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to generate QR code and save to file
    private static void generateQRCode(String data, String filePath, int width, int height)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        // Ensure directory exists
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}