package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Getter
//@Setter
//@ToString

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeShop {

    private Long id;
    private String coffeeshopName;
    private String coffeeshopDescription;

}
