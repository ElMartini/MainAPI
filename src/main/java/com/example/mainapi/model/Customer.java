package com.example.mainapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

    private String cID;
    private String cFirstName;
    private String cLastName;
    private String cEmail;
    private String cPassword;

}
