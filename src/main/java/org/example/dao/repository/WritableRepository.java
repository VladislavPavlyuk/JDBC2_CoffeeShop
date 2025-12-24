package org.example.dao.repository;

import java.util.List;

// write operations for database
public interface WritableRepository<T> {
    T save(T entity);

    List<T> saveAll(List<T> entities);

    void update(T entity);

    void deleteById(Long id);

    void deleteAll();
}







