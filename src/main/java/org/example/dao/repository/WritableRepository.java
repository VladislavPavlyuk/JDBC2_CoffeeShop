package org.example.dao.repository;

import java.util.List;

public interface WritableRepository<T> {
    T save(T entity);

    List<T> saveAll(List<T> entities);

    void update(T entity);

    void deleteById(Long id);

    void deleteAll();
}















