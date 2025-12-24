package org.example.dao.repository;

// combines read and write operations
public interface CrudRepository<T, ID> extends Repository<T, ID>, WritableRepository<T> {
}







