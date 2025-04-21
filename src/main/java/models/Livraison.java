package models;

import java.time.LocalDate;

public class Livraison {
    private int id;
    private LocalDate date;
    private float poids;
    private String produit;
    private SocieteRecyclage societeRecyclage;

    // Constructors
    public Livraison() {
    }

    public Livraison(int id, LocalDate date, float poids, String produit, SocieteRecyclage societeRecyclage) {
        this.id = id;
        this.date = date;
        this.poids = poids;
        this.produit = produit;
        this.societeRecyclage = societeRecyclage;
    }

    public Livraison(LocalDate date, float poids, String produit, SocieteRecyclage societeRecyclage) {
        this.date = date;
        this.poids = poids;
        this.produit = produit;
        this.societeRecyclage = societeRecyclage;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public SocieteRecyclage getSocieteRecyclage() {
        return societeRecyclage;
    }

    public void setSocieteRecyclage(SocieteRecyclage societeRecyclage) {
        this.societeRecyclage = societeRecyclage;
    }

    @Override
    public String toString() {
        return "Livraison{" +
                "id=" + id +
                ", date=" + date +
                ", poids=" + poids +
                ", produit='" + produit + '\'' +
                ", societeRecyclage=" + societeRecyclage.getNom() +
                '}';
    }
}
