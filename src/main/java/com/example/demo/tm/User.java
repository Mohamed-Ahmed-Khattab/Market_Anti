package com.example.demo.tm;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import com.example.demo.util.UserType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class User {

    private Integer id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserType userType;

}
