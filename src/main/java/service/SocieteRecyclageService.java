package service;

import models.SocieteRecyclage;
import java.util.ArrayList;
import java.util.List;

public class SocieteRecyclageService implements IService<SocieteRecyclage> {
    List<SocieteRecyclage> societes = new ArrayList<>();

    @Override
    public void ajouter(SocieteRecyclage s) {
        societes.add(s);
    }

    @Override
    public void modifier(SocieteRecyclage s) {
        for (int i = 0; i < societes.size(); i++) {
            if (societes.get(i).getId() == s.getId()) {
                societes.set(i, s);
            }
        }
    }

    @Override
    public void supprimer(int id) {
        societes.removeIf(s -> s.getId() == id);
    }

    @Override
    public List<SocieteRecyclage> afficher() {
        return societes;
    }
}
