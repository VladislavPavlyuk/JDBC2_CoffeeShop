package org.example.dao.mapper;

import org.example.model.Shift;

import java.sql.ResultSet;
import java.sql.SQLException;

// converts database row to Shift object
public class ShiftMapper implements ResultSetMapper<Shift> {

    @Override
    public Shift map(ResultSet resultSet) throws SQLException {
        return Shift.builder()
                .id(resultSet.getLong("id"))
                .shiftTitle(resultSet.getString("shift_code"))
                .build();
    }
}





