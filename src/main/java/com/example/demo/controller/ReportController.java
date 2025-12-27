package com.example.demo.controller;

import com.example.demo.db.DatabaseManager;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.CommonMethod;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for Sales Report feature.
 * Shows cart checkout history for customers with total sales amount.
 * Automatically loads today's sales on page open.
 */
public class ReportController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<SalesReportItem> reportTable;

    @FXML
    private TableColumn<SalesReportItem, String> productNameColumn;

    @FXML
    private TableColumn<SalesReportItem, Integer> quantityColumn;

    @FXML
    private TableColumn<SalesReportItem, Double> unitPriceColumn;

    @FXML
    private TableColumn<SalesReportItem, Double> totalColumn;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label todayTotalLabel;

    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Setup table columns
        setupTableColumns();

        // Auto-load today's sales data on page open
        loadSalesData(LocalDate.now());
    }

    private void setupTableColumns() {
        if (productNameColumn != null) {
            productNameColumn.setCellValueFactory(
                    cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        }
        if (quantityColumn != null) {
            quantityColumn.setCellValueFactory(
                    cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        }
        if (unitPriceColumn != null) {
            unitPriceColumn.setCellValueFactory(
                    cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        }
        if (totalColumn != null) {
            totalColumn.setCellValueFactory(
                    cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        }
    }

    @FXML
    void handleGenerate(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            AlertUtil.showWarning("Validation", "Please select a date.");
            return;
        }
        loadSalesData(selectedDate);
    }

    private void loadSalesData(LocalDate selectedDate) {
        ObservableList<SalesReportItem> reportItems = FXCollections.observableArrayList();
        double totalSales = 0.0;

        // Query cart items from completed/checked-out carts on the selected date
        String sql = """
                SELECT p.productName, ci.quantity, ci.priceAtAdd, (ci.quantity * ci.priceAtAdd) as total,
                       c.cartID, cu.firstName, cu.lastName
                FROM CartItem ci
                JOIN Cart c ON ci.cartID = c.cartID
                JOIN Product p ON ci.productID = p.productID
                LEFT JOIN Customer cu ON c.customerID = cu.customerID
                WHERE (c.status = 'checked_out' OR c.status = 'completed')
                AND DATE(c.updatedAt) = ?
                ORDER BY c.cartID, p.productName
                """;

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(selectedDate));
            ResultSet rs = stmt.executeQuery();

<<<<<<< HEAD
            boolean foundData = false;
            while (rs.next()) {
                foundData = true;
=======
<<<<<<< HEAD
            boolean foundData = false;
            while (rs.next()) {
                foundData = true;
=======
            while (rs.next()) {
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
>>>>>>> 881edc6b6d85faf1aa7d86d91efda63334a2ee3e
                String productName = rs.getString("productName");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("priceAtAdd");
                double total = rs.getDouble("total");
                String customerName = rs.getString("firstName");
                if (customerName == null) {
                    customerName = "Guest";
                } else {
                    String lastName = rs.getString("lastName");
                    if (lastName != null) {
                        customerName += " " + lastName;
                    }
                }

                reportItems.add(new SalesReportItem(productName, quantity, unitPrice, total, customerName));
                totalSales += total;
            }

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 881edc6b6d85faf1aa7d86d91efda63334a2ee3e
            if (!foundData) {
                System.out.println("No sales data found for date: " + selectedDate);
            }

<<<<<<< HEAD
=======
=======
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
>>>>>>> 881edc6b6d85faf1aa7d86d91efda63334a2ee3e
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showError("Error", "Could not generate report: " + e.getMessage());
            return;
        }

        // Update the table
        if (reportTable != null) {
            reportTable.setItems(reportItems);
<<<<<<< HEAD
            reportTable.refresh();
=======
<<<<<<< HEAD
            reportTable.refresh();
=======
>>>>>>> bdef750f8e7c2fa3a4db403d7c8aa264e9ab3cb6
>>>>>>> 881edc6b6d85faf1aa7d86d91efda63334a2ee3e
        }

        // Update total labels
        String formattedTotal = String.format("$%.2f", totalSales);
        if (totalSalesLabel != null) {
            totalSalesLabel.setText("Total Sales: " + formattedTotal);
        }
        if (todayTotalLabel != null) {
            if (selectedDate.equals(LocalDate.now())) {
                todayTotalLabel.setText("TODAY'S TOTAL: " + formattedTotal);
            } else {
                todayTotalLabel.setText("TOTAL FOR " + selectedDate.toString() + ": " + formattedTotal);
            }
        }

        // Show message if no data (but only when explicitly generating, not on init)
        if (reportItems.isEmpty() && datePicker.getValue() != null) {
            // Don't show alert on auto-load, just update labels
        }
    }

    /**
     * Inner class to represent a sales report item
     */
    public static class SalesReportItem {
        private final String productName;
        private final int quantity;
        private final double unitPrice;
        private final double total;
        private final String customerName;

        public SalesReportItem(String productName, int quantity, double unitPrice, double total, String customerName) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = total;
            this.customerName = customerName;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return total;
        }

        public String getCustomerName() {
            return customerName;
        }
    }
}
