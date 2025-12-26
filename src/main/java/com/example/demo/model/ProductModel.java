package com.example.demo.model;

import com.example.demo.dto.ProductDTO;
import lombok.Data;

/**
 * Placeholder for missing ProductModel.
 * Often in these projects, there's confusion between 'Product' entity and
 * 'ProductModel' (view model or usage wrapper).
 * Or it was a rename that wasn't propagated.
 */
@Data
public class ProductModel {
    private static final com.example.demo.dao.ProductDAO productDAO = new com.example.demo.dao.ProductDAO();
    private static final com.example.demo.dao.SupplierDAO supplierDAO = new com.example.demo.dao.SupplierDAO();

    public static ProductDTO getProductByID(int id) {
        Product p = productDAO.getById(id);
        if (p == null)
            return null;
        return ProductDTO.builder()
                .id(p.getProductID())
                .name(p.getName())
                .unitPrice(p.getPrice())
                .description("No description available") // Default
                .build();
    }

    public static java.util.List<ProductDTO> getAllProducts() {
        java.util.List<ProductDTO> list = new java.util.ArrayList<>();
        for (Product p : productDAO.getAll()) {
            list.add(ProductDTO.builder()
                    .id(p.getProductID())
                    .name(p.getName())
                    .unitPrice(p.getPrice())
                    .build());
        }
        return list;
    }

    public static java.util.List<String> getSuppliersId() {
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (Supplier s : supplierDAO.getAll()) {
            ids.add(String.valueOf(s.getSupplierID()));
        }
        return ids;
    }

    public static int saveNewProduct(ProductDTO dto) {
        Product p = new Product();
        p.setName(dto.getName());
        p.setPrice(dto.getUnitPrice());
        // Simple mapping, category and others handled by DAO defaults
        return productDAO.create(p) ? 1 : -1;
    }

    public static int deleteProduct(int id) {
        return productDAO.delete(id) ? 1 : 0;
    }

    public static int updateProductDetails(ProductDTO dto) {
        Product p = productDAO.getById(dto.getId());
        if (p != null) {
            p.setName(dto.getName());
            p.setPrice(dto.getUnitPrice());
            return productDAO.update(p) ? 1 : 0;
        }
        return 0;
    }
}
