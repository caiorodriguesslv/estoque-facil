package org.devilish.dao;

import org.devilish.entity.Product;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public interface ProductDAO {
    void save(Product product);
    Product findByCode(String code);
    List<Product> findAll();
    List<Product> findByExpiryDate(LocalDate date);
    List<Product> findExpiredProducts();
    void update(Product product);
    void delete(String code);
    boolean existsByCode(String code);
    void updateQuantity(String code, BigDecimal newQuantity);
    
} 