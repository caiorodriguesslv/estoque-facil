package org.devilish.impl;

import org.devilish.dao.PaymentDAO;
import org.devilish.entity.Payment;
import org.devilish.exceptions.DAOException;
import org.devilish.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.time.LocalDate;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    
    private final SessionFactory sessionFactory;
    
    public PaymentDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public void save(Payment payment) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao salvar pagamento", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public Payment findById(Long id) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.get(Payment.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pagamento por ID", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Payment> findAll() {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery(
                "SELECT p FROM Payment p " +
                "LEFT JOIN FETCH p.product " +
                "LEFT JOIN FETCH p.user", 
                Payment.class
            ).list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar todos os pagamentos", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Payment> findByUser(Long userId) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery(
                "SELECT p FROM Payment p " +
                "LEFT JOIN FETCH p.product " +
                "LEFT JOIN FETCH p.user " +
                "WHERE p.user.id = :userId", 
                Payment.class)
                         .setParameter("userId", userId)
                         .list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pagamentos por usuário", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Payment> findByProduct(String productCode) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery(
                "SELECT p FROM Payment p " +
                "LEFT JOIN FETCH p.product " +
                "LEFT JOIN FETCH p.user " +
                "WHERE p.product.code = :productCode", 
                Payment.class)
                         .setParameter("productCode", productCode)
                         .list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pagamentos por produto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery(
                "SELECT p FROM Payment p " +
                "LEFT JOIN FETCH p.product " +
                "LEFT JOIN FETCH p.user " +
                "WHERE p.deliveryDate BETWEEN :startDate AND :endDate", 
                Payment.class)
                         .setParameter("startDate", startDate)
                         .setParameter("endDate", endDate)
                         .list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar pagamentos por período", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void update(Payment payment) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao atualizar pagamento", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void delete(Long id) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) {
                session.delete(payment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao deletar pagamento", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
