package org.devilish.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity that represents a payment in the system
 */
@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code", nullable = false, referencedColumnName = "code")
    private Product product;
    
    @Column(name = "paid_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidQuantity;
    
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    // Constructors
    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }
    
    public Payment(Product product, BigDecimal paidQuantity, LocalDate deliveryDate, User user) {
        this();
        this.product = product;
        this.paidQuantity = paidQuantity;
        this.deliveryDate = deliveryDate;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public BigDecimal getPaidQuantity() {
        return paidQuantity;
    }
    
    public void setPaidQuantity(BigDecimal paidQuantity) {
        this.paidQuantity = paidQuantity;
    }
    
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", product=" + (product != null ? product.getCode() : "null") +
                ", paidQuantity=" + paidQuantity +
                ", deliveryDate=" + deliveryDate +
                ", user=" + (user != null ? user.getName() : "null") +
                ", paymentDate=" + paymentDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Payment payment = (Payment) o;
        
        return id != null ? id.equals(payment.id) : payment.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 