package com.example.demo.controller;

import com.example.demo.dao.CartDAO;
import com.example.demo.dao.CartItemDAO;
import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.util.AlertUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML
    private ComboBox<Customer> customerCombo;
    @FXML
    private ComboBox<Product> productCombo;
    @FXML
    private TextField quantityField;
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> productNameColumn;
    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> totalColumn;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Button addToCartBtn;
    @FXML
    private Button checkoutBtn;

    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private Cart currentCart;
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadCustomers();
        loadProducts();

        // Disable controls until customer selected
        enableCartControls(false);
    }

    private void setupTable() {
        productNameColumn.setCellValueFactory(cellData -> {
            Product p = productDAO.getById(cellData.getValue().getProductID());
            return new SimpleStringProperty(p != null ? p.getProductName() : "Unknown");
        });

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceAtAdd"));

        totalColumn.setCellValueFactory(cellData -> {
            CartItem item = cellData.getValue();
            return new SimpleObjectProperty<>(
                    BigDecimal.valueOf(item.getPriceAtAdd()).multiply(BigDecimal.valueOf(item.getQuantity())));
        });

        cartTable.setItems(cartItems);
    }

    private void loadCustomers() {
        customerCombo.setItems(FXCollections.observableArrayList(customerDAO.getAll()));
        customerCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer c) {
                return c != null ? c.getFullName() : "";
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
    }

    private void loadProducts() {
        productCombo.setItems(FXCollections.observableArrayList(productDAO.getAll()));
        productCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product p) {
                return p != null ? p.getProductName() + " ($" + p.getPrice() + ")" : "";
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    void handleCustomerSelect(ActionEvent event) {
        Customer selected = customerCombo.getValue();
        if (selected != null) {
            // Check for existing active cart
            currentCart = cartDAO.getActiveCartByCustomer(selected.getCustomerID());

            if (currentCart == null) {
                // Create new cart
                currentCart = new Cart();
                currentCart.setCustomerID(selected.getCustomerID());
                currentCart.setStatus("active");
                currentCart.setCreatedAt(LocalDateTime.now());
                cartDAO.create(currentCart);
            }

            enableCartControls(true);
            refreshCartItems();
        } else {
            enableCartControls(false);
            currentCart = null;
            cartItems.clear();
            updateTotalLabel();
        }
    }

    @FXML
    void handleAddToCart(ActionEvent event) {
        Product product = productCombo.getValue();
        String qtyStr = quantityField.getText();

        if (product == null || qtyStr.isEmpty()) {
            AlertUtil.showWarning("Validation", "Select a product and quantity.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0)
                throw new NumberFormatException();

            if (product.getStockQuantity() < qty) {
                AlertUtil.showWarning("Stock Error", "Not enough stock. Available: " + product.getStockQuantity());
                return;
            }

            // Check if item already exists in cart, then just update quantity
            boolean exists = false;
            for (CartItem item : cartItems) {
                if (item.getProductID() == product.getProductID()) {
                    int newQty = item.getQuantity() + qty;
                    if (product.getStockQuantity() < newQty) {
                        AlertUtil.showWarning("Stock Error", "Total quantity exceeds stock.");
                        return;
                    }
                    cartItemDAO.updateQuantity(item.getCartItemID(), newQty);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                CartItem newItem = new CartItem();
                newItem.setCartID(currentCart.getCartID());
                newItem.setProductID(product.getProductID());
                newItem.setQuantity(qty);
                newItem.setPriceAtAdd(product.getPrice());
                cartItemDAO.create(newItem);
            }

            refreshCartItems();
            quantityField.setText("1");
            productCombo.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Quantity must be a positive integer.");
        }
    }

    @FXML
    void handleRemoveItem(ActionEvent event) {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItemDAO.delete(selected.getCartItemID());
            refreshCartItems();
        }
    }

    @FXML
    void handleCheckout(ActionEvent event) {
        if (cartItems.isEmpty()) {
            AlertUtil.showInfo("Empty Cart", "Add items to cart before checkout.");
            return;
        }

        if (AlertUtil.showConfirmation("Checkout", "Process payment for $" + totalAmountLabel.getText() + "?")) {
            // Deduct stock
            for (CartItem item : cartItems) {
                productDAO.updateStock(item.getProductID(), -item.getQuantity());
            }

            // Mark cart as checked_out (for sales report)
            cartDAO.updateStatus(currentCart.getCartID(), "checked_out");

            // Add loyalty points (1 point per $10 spent)
            BigDecimal total = cartItemDAO.getCartTotal(currentCart.getCartID());
            int points = total.divideToIntegralValue(BigDecimal.TEN).intValue();
            customerDAO.updateLoyaltyPoints(currentCart.getCustomerID(), points);

            AlertUtil.showInfo("Success", "Order completed! Loyalty points added: " + points);

            // Reset UI
            customerCombo.getSelectionModel().clearSelection();
            handleCustomerSelect(null);
        }
    }

    private void refreshCartItems() {
        if (currentCart != null) {
            cartItems.setAll(cartItemDAO.getByCart(currentCart.getCartID()));
            updateTotalLabel();
        }
    }

    private void updateTotalLabel() {
        if (currentCart != null) {
            BigDecimal total = cartItemDAO.getCartTotal(currentCart.getCartID());
            totalAmountLabel.setText(String.format("%.2f", total));
        } else {
            totalAmountLabel.setText("0.00");
        }
    }

    private void enableCartControls(boolean enable) {
        productCombo.setDisable(!enable);
        quantityField.setDisable(!enable);
        addToCartBtn.setDisable(!enable);
        checkoutBtn.setDisable(!enable);
        cartTable.setDisable(!enable);
    }
}
