package utils;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Livraison;

public class QRCodeTableCell<T> extends TableCell<T, String> {

    private final ImageView imageView;

    public QRCodeTableCell() {
        this.imageView = new ImageView();
        // Increase QR code size in table
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            imageView.setImage(null);
        } else {
            // Generate QR code image with larger size
            Image qrImage = QRCodeGenerator.generateQRCodeImage(item, 200, 200);
            imageView.setImage(qrImage);
        }
    }
}