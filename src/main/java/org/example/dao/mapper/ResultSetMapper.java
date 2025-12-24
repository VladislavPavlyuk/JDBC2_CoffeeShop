package org.example.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

// converts database row to Java object
public interface ResultSetMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}







