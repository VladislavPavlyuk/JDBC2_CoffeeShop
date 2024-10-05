package org.example.dao.shiftDAO;


import org.example.dao.CRUDInterface;
import org.example.model.Shift;

import java.util.List;

public interface ShiftDao extends CRUDInterface<Shift> {

    List<String> findAllShiftsWithLessOrEqualStaffNumber(int numberStaff);
}
