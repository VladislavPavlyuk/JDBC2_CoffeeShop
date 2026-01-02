package org.example.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.model.Coffeeshop;

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



