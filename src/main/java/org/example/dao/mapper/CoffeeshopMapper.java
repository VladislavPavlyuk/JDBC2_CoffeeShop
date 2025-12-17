package org.example.dao.mapper;

import org.example.model.Coffeeshop;

import java.sql.ResultSet;
import java.sql.SQLException;

// converts database row to Coffeeshop object
public class CoffeeshopMapper implements ResultSetMapper<Coffeeshop> {

    @Override
    public Coffeeshop map(ResultSet resultSet) throws SQLException {
        return Coffeeshop.builder()
                .id(resultSet.getLong("id"))
                .coffeeshopTitle(resultSet.getString("coffeeshop_Title"))
                .coffeeshopDescription(resultSet.getString("coffeeshop_description"))
                .build();
    }
}



