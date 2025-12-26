package com.example.demo.model;

import lombok.Data;
import com.example.demo.dto.ProductStockDTO;

/**
 * Placeholder for missing ProductStockModel.
 */
@Data
public class ProductStockModel {
    private static final com.example.demo.dao.ProductDAO productDAO = new com.example.demo.dao.ProductDAO();

    public static ProductStockDTO getStockDetails(int id) {
        Product p = productDAO.getById(id);
        if (p == null)
            return null;
        return ProductStockDTO.builder()
                .productId(p.getProductID())
                .quantity(p.getStockQuantity())
                .updateTime(java.time.LocalDateTime.now())
                .build();
    }

    public static java.util.List<Integer> getAllProductId() {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (Product p : productDAO.getAll()) {
            ids.add(p.getProductID());
        }
        return ids;
    }

    public static int saveNewStock(ProductStockDTO dto) {
        // Here we might be overriding or adding to stock
        // Assuming updateStock in DAO adds to existing
        return productDAO.updateStock(dto.getProductId(), (int) dto.getQuantity()) ? 1 : -1;
    }

    public static int deleteProductStock(int id) {
        // No direct 'stock delete', maybe set to 0?
        return productDAO.updateStock(id, -Integer.MAX_VALUE) ? 1 : 0;
    }

    public static java.util.List<ProductStockDTO> getAllProductStock() {
        java.util.List<ProductStockDTO> list = new java.util.ArrayList<>();
        for (Product p : productDAO.getAll()) {
            list.add(ProductStockDTO.builder()
                    .productId(p.getProductID())
                    .quantity(p.getStockQuantity())
                    .updateTime(java.time.LocalDateTime.now())
                    .build());
        }
        return list;
    }
}
