package models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Livraison {
    private IntegerProperty id;
    private ObjectProperty<LocalDate> date;
    private FloatProperty poids;
    private StringProperty produit;
    private ObjectProperty<SocieteRecyclage> societeRecyclage;

    // Constructors
    public Livraison() {
        this.id = new SimpleIntegerProperty();
        this.date = new SimpleObjectProperty<>();
        this.poids = new SimpleFloatProperty();
        this.produit = new SimpleStringProperty();
        this.societeRecyclage = new SimpleObjectProperty<>();
    }

    public Livraison(int id, LocalDate date, float poids, String produit, SocieteRecyclage societeRecyclage) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.poids = new SimpleFloatProperty(poids);
        this.produit = new SimpleStringProperty(produit);
        this.societeRecyclage = new SimpleObjectProperty<>(societeRecyclage);
    }

    public Livraison(LocalDate date, float poids, String produit, SocieteRecyclage societeRecyclage) {
        this.id = new SimpleIntegerProperty(); // Added to fix the null property issue
        this.date = new SimpleObjectProperty<>(date);
        this.poids = new SimpleFloatProperty(poids);
        this.produit = new SimpleStringProperty(produit);
        this.societeRecyclage = new SimpleObjectProperty<>(societeRecyclage);
    }

    // Getters and Setters with Property access
    public IntegerProperty idProperty() {
        return id;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public FloatProperty poidsProperty() {
        return poids;
    }

    public StringProperty produitProperty() {
        return produit;
    }

    public ObjectProperty<SocieteRecyclage> societeRecyclageProperty() {
        return societeRecyclage;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public float getPoids() {
        return poids.get();
    }

    public void setPoids(float poids) {
        this.poids.set(poids);
    }

    public String getProduit() {
        return produit.get();
    }

    public void setProduit(String produit) {
        this.produit.set(produit);
    }

    public SocieteRecyclage getSocieteRecyclage() {
        return societeRecyclage.get();
    }

    public void setSocieteRecyclage(SocieteRecyclage societeRecyclage) {
        this.societeRecyclage.set(societeRecyclage);
    }

    @Override
    public String toString() {
        return "Livraison{" +
                "id=" + id.get() +
                ", date=" + date.get() +
                ", poids=" + poids.get() +
                ", produit='" + produit.get() + '\'' +
                ", societeRecyclage=" + societeRecyclage.get().getNom() +
                '}';
    }
}