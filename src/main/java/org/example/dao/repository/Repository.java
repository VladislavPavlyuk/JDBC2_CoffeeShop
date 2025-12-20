package org.example.dao.repository;

import java.util.List;
import java.util.Optional;

// read operations for database
public interface Repository<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);
}





