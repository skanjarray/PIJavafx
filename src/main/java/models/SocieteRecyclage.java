package models;

import javafx.beans.property.*;

public class SocieteRecyclage {

    private IntegerProperty id;
    private StringProperty nom;
    private StringProperty adresse;
    private StringProperty email;

    // Constructors
    public SocieteRecyclage() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.adresse = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
    }

    public SocieteRecyclage(int id, String nom, String adresse, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty(adresse);
        this.email = new SimpleStringProperty(email);
    }

    public SocieteRecyclage(String nom, String adresse, String email) {
        this.nom = new SimpleStringProperty(nom);
        this.adresse = new SimpleStringProperty(adresse);
        this.email = new SimpleStringProperty(email);
        this.id = new SimpleIntegerProperty(); // Optional if id is auto-generated
    }

    // Getters and Setters with Property access
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty adresseProperty() {
        return adresse;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getAdresse() {
        return adresse.get();
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Override
    public String toString() {
        return nom.get();
    }
}
