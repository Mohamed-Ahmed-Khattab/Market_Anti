package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Salary entity representing employee compensation
 * Maps to the Salary table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    private Integer salaryID;
    private Integer employeeID;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private String paymentFrequency; // weekly, bi-weekly, monthly, annual

    /**
     * Calculate net salary (base + bonus - deductions)
     */
    public BigDecimal getNetSalary() {
        BigDecimal net = baseSalary;
        if (bonus != null) {
            net = net.add(bonus);
        }
        if (deductions != null) {
            net = net.subtract(deductions);
        }
        return net;
    }

    /**
     * Check if this salary record is currently active
     */
    public boolean isActive() {
        return endDate == null;
    }
}
