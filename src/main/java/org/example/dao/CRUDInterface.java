package org.example.dao;

import java.util.List;

public interface CRUDInterface<T> {

    void save(T coffeeshop);

    void saveMany(List<T> coffeeshops) ;

    void update(T coffeeshop);

    void delete(T coffeeshop);

    List<T> findAll();

    void deleteAll();

}
