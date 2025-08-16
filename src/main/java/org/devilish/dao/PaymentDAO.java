package org.devilish.dao;

import org.devilish.entity.Payment;
import java.util.List;
import java.time.LocalDate;

public interface PaymentDAO {
    void save(Payment payment);
    Payment findById(Long id);
    List<Payment> findAll();
    List<Payment> findByUser(Long userId);
    List<Payment> findByProduct(String productCode);
    List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate);
    void update(Payment payment);
    void delete(Long id);
}
