package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Endorsement entity representing product endorsements
 * Maps to the Endorsement table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endorsement {
    private Integer endorsementID;
    private Integer productID;
    private String endorserName;
    private String endorsementType;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private String description;

    /**
     * Check if endorsement is currently active
     */
    public boolean isActive() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return (startDate == null || !now.isBefore(startDate)) &&
                (endDate == null || !now.isAfter(endDate));
    }
}
