package org.devilish.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Classe utilitária para gerenciar a sessão do Hibernate
 */
public class HibernateUtil {
    
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    /**
     * Constrói a SessionFactory do Hibernate
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Cria a configuração a partir do arquivo hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure();
            
            // Constrói e retorna a SessionFactory
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Erro ao inicializar SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Retorna a SessionFactory configurada
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**
     * Fecha a SessionFactory
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
} 