package org.devilish.impl;

import org.devilish.dao.ProductDAO;
import org.devilish.entity.Product;
import org.devilish.exceptions.BusinessException;
import org.devilish.exceptions.DAOException;
import org.devilish.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    
    private final SessionFactory sessionFactory;
    
    public ProductDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public void save(Product product) {
        validateProduct(product);
        
        if (existsByCode(product.getCode())) {
            throw new BusinessException("Produto com código " + product.getCode() + " já existe");
        }
        
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(product);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao salvar produto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public Product findByCode(String code) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.get(Product.class, code);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produto por código", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Product> findAll() {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery("FROM Product p", Product.class).list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar todos os produtos", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Product> findByExpiryDate(LocalDate date) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery("FROM Product p WHERE p.expiryDate = :date", Product.class)
                         .setParameter("date", date)
                         .list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos por data de validade", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<Product> findExpiredProducts() {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery("FROM Product p WHERE p.expiryDate < :today", Product.class)
                         .setParameter("today", LocalDate.now())
                         .list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar produtos vencidos", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void update(Product product) {
        validateProduct(product);
        
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(product);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao atualizar produto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void delete(String code) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, code);
            if (product != null) {
                session.delete(product);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao deletar produto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public boolean existsByCode(String code) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            Long count = session.createQuery("SELECT COUNT(p) FROM Product p WHERE p.code = :code", Long.class)
                               .setParameter("code", code)
                               .uniqueResult();
            return count > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar existência do produto", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void updateQuantity(String code, BigDecimal newQuantity) {
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Quantidade não pode ser negativa");
        }
        
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, code);
            if (product == null) {
                throw new BusinessException("Produto não encontrado");
            }
            product.setQuantity(newQuantity);
            session.update(product);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao atualizar quantidade", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    private void validateProduct(Product product) {
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new BusinessException("Código do produto é obrigatório");
        }
        
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new BusinessException("Descrição do produto é obrigatória");
        }
        
        if (product.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Quantidade não pode ser negativa");
        }
        
        if (product.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Data de validade não pode ser anterior à data atual");
        }
    }
} 