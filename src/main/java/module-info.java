module com.esprit.ecotounsi {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jakarta.mail; // ➔ Ajoute cette ligne pour autoriser l'utilisation de Jakarta Mail

    opens com.esprit.ecotounsi to javafx.fxml;
    exports com.esprit.ecotounsi;
    exports com.esprit.ecotounsi.Repositories;
    opens com.esprit.ecotounsi.Repositories to javafx.fxml;
    exports com.esprit.ecotounsi.Controllers;
    opens com.esprit.ecotounsi.Controllers to javafx.fxml;
    exports com.esprit.ecotounsi.Models;
    opens com.esprit.ecotounsi.Models to javafx.fxml;
}