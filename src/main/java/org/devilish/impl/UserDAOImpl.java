package org.devilish.impl;

import org.devilish.dao.UserDAO;
import org.devilish.entity.User;
import org.devilish.exceptions.DAOException;
import org.devilish.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    
    private final SessionFactory sessionFactory;
    
    public UserDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public void save(User user) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao salvar usuário", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public User findById(Long id) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.get(User.class, id);
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar usuário por ID", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public User findByEmail(String email) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery("FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .uniqueResult();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar usuário por email", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public List<User> findAll() {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            return session.createQuery("FROM User u", User.class).list();
        } catch (Exception e) {
            throw new DAOException("Erro ao buscar todos os usuários", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public void update(User user) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao atualizar usuário", e);
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
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DAOException("Erro ao deletar usuário", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        Session session = null;
        
        try {
            session = sessionFactory.openSession();
            Long count = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                               .setParameter("email", email)
                               .uniqueResult();
            return count > 0;
        } catch (Exception e) {
            throw new DAOException("Erro ao verificar existência do email", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}