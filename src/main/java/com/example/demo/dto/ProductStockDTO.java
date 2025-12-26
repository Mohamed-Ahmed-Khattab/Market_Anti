package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductStockDTO {

    private Integer id;
    private Integer productId;
    private double quantity;
    private LocalDateTime updateTime;
}
