package org.devilish.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um produto no sistema
 */
@Entity
@Table(name = "products")
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    

    public Product() {}
    
    public Product(String code, String description, LocalDate entryDate, LocalDate expiryDate, BigDecimal quantity) {
        this.code = code;
        this.description = description;
        this.entryDate = entryDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
    }
    
 
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getEntryDate() {
        return entryDate;
    }
    
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    // Verifica se o produto está dentro da data de validade
    public boolean isValid() {
        return LocalDate.now().isBefore(expiryDate) || LocalDate.now().isEqual(expiryDate);
    }
    
    
    // Verifica se o produto tem estoque suficiente
    public boolean hasStock(BigDecimal requestedQuantity) {
        return quantity.compareTo(requestedQuantity) >= 0;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", entryDate=" + entryDate +
                ", expiryDate=" + expiryDate +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Product product = (Product) o;
        
        return code != null ? code.equals(product.code) : product.code == null;
    }
    
    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
} 