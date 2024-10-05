package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffToCoffeeshop {

    private Long id;
    private long staff_Id;
    private long coffeeshop_Id;

}
