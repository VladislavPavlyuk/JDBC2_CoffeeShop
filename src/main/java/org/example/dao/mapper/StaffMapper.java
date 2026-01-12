package org.example.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.model.Staff;

public class StaffMapper implements ResultSetMapper<Staff> {

    @Override
    public Staff map(ResultSet resultSet) throws SQLException {
        return Staff.builder()
                .id(resultSet.getLong("id"))
                .shift_Id(resultSet.getLong("shift_id"))
                .positionId(resultSet.getLong("position_id"))
                .firstName(resultSet.getString("firstname"))
                .lastName(resultSet.getString("lastname"))
                .build();
    }
}















