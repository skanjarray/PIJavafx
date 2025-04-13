package com.esprit.ecotounsi;

public class Produit {
    private int id;
    private String nom;
    private String unite;
    private int quantite;
    private Categorie categorie;  // Relation avec la catégorie

    // Constructeur sans ID, l'ID est généré par la base de données
    public Produit(String nom, String unite, int quantite, Categorie categorie) {
        this.nom = nom;
        this.unite = unite;
        this.quantite = quantite;
        this.categorie = categorie;
    }

    // Constructeur avec ID, utile pour la mise à jour
    public Produit(int id, String nom, String unite, int quantite, Categorie categorie) {
        this.id = id;
        this.nom = nom;
        this.unite = unite;
        this.quantite = quantite;
        this.categorie = categorie;
    }

    public Produit(String nom, String unite, int quantite) {
        this.nom = nom;
        this.unite = unite;
        this.quantite = quantite;
    }

    public Produit() {

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

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    // Méthode toString modifiée pour inclure la catégorie
    @Override
    public String toString() {
        return "Produit [id=" + id + ", nom=" + nom + ", unite=" + unite + ", quantite=" + quantite
                + ", categorie=" + (categorie != null ? categorie.getNom() : "Aucune") + "]";
    }

}
