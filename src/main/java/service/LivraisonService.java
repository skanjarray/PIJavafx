
package service;

import models.Livraison;
import java.util.ArrayList;
import java.util.List;

public class LivraisonService implements IService<Livraison> {
    List<Livraison> livraisons = new ArrayList<>();

    @Override
    public void ajouter(Livraison l) {
        livraisons.add(l);
    }

    @Override
    public void modifier(Livraison l) {
        for (int i = 0; i < livraisons.size(); i++) {
            if (livraisons.get(i).getId() == l.getId()) {
                livraisons.set(i, l);
            }
        }
    }

    @Override
    public void supprimer(int id) {
        livraisons.removeIf(l -> l.getId() == id);
    }

    @Override
    public List<Livraison> afficher() {
        return livraisons;
    }
}
