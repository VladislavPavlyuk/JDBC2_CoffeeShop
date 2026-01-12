package org.example.model;

import java.sql.Date;

public class StaffSchedule {
    private Long id;
    private Long staffId;
    private String staffFirstName;
    private String staffLastName;
    private Long shiftId;
    private String shiftCode;
    private Date workDate;
    private String notes;

    public StaffSchedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffFirstName() {
        return staffFirstName;
    }

    public void setStaffFirstName(String staffFirstName) {
        this.staffFirstName = staffFirstName;
    }

    public String getStaffLastName() {
        return staffLastName;
    }

    public void setStaffLastName(String staffLastName) {
        this.staffLastName = staffLastName;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Staff: %s %s (ID: %d), Shift: %s (ID: %d), Date: %s%s",
            staffFirstName, staffLastName, staffId,
            shiftCode != null ? shiftCode : "N/A", shiftId,
            workDate != null ? workDate.toString() : "N/A",
            notes != null && !notes.isEmpty() ? ", Notes: " + notes : "");
    }
}










