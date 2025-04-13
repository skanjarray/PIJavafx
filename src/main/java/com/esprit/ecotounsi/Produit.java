package com.esprit.ecotounsi;

public class Produit {
    private int id;
    private String nom;
    private String unite;
    private int quantite;

    // Constructeur sans l'ID, car l'ID est généré par la base de données
    public Produit(String nom, String unite, int quantite) {
        this.nom = nom;
        this.unite = unite;
        this.quantite = quantite;
    }

    // Constructeur avec l'ID, utile pour la mise à jour
    public Produit(int id, String nom, String unite, int quantite) {
        this.id = id;
        this.nom = nom;
        this.unite = unite;
        this.quantite = quantite;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Méthode toString pour afficher un produit
    @Override
    public String toString() {
        return "Produit [id=" + id + ", nom=" + nom + ", unite=" + unite + ", quantite=" + quantite + "]";
    }

}




