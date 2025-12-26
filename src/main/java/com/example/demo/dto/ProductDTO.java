package com.example.demo.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {

    private Integer id ;
    private String name ;
    private String description ;
    private double unitPrice ;
    private String supplierId;
}
