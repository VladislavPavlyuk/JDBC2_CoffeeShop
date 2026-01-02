package org.example.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.model.Shift;

public class ShiftMapper implements ResultSetMapper<Shift> {

    @Override
    public Shift map(ResultSet resultSet) throws SQLException {
        return Shift.builder()
                .id(resultSet.getLong("id"))
                .shiftTitle(resultSet.getString("shift_code"))
                .build();
    }
}













