package service;

import java.util.List;

public interface IService<T> {
    boolean ajouter(T t);
    void modifier(T t);
    void supprimer(int id);
    List<T> afficher();
}
