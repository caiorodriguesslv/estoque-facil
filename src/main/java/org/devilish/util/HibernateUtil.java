package org.devilish.util;

import org.hibernate.Session;
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
            System.out.println("=== INICIANDO CONFIGURAÇÃO DO HIBERNATE ===");
            // Cria a configuração a partir do arquivo hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure();
            System.out.println("=== CONFIGURAÇÃO CARREGADA COM SUCESSO ===");
            
            // Constrói e retorna a SessionFactory
            SessionFactory factory = configuration.buildSessionFactory();
            System.out.println("=== SESSION FACTORY CRIADA COM SUCESSO ===");
            return factory;
        } catch (Throwable ex) {
            System.err.println("=== ERRO AO INICIALIZAR SESSION FACTORY ===");
            System.err.println("Erro ao inicializar SessionFactory: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Retorna a SessionFactory configurada
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            System.err.println("=== ALERTA: SESSION FACTORY É NULL ===");
            return null;
        }
        
        if (sessionFactory.isClosed()) {
            System.err.println("=== ALERTA: SESSION FACTORY ESTÁ FECHADA ===");
            System.err.println("=== TENTANDO RECRIAR SESSION FACTORY ===");
            // Não podemos recriar aqui pois é static final, mas podemos logar
            return sessionFactory; // Retorna mesmo fechada para debug
        }
        
        return sessionFactory;
    }
    
    /**
     * Verifica o status da SessionFactory
     */
    public static String getStatus() {
        if (sessionFactory == null) {
            return "NULL";
        }
        return sessionFactory.isClosed() ? "FECHADA" : "ABERTA";
    }
    
    /**
     * Força verificação de conectividade
     */
    public static boolean testConnection() {
        try {
            if (sessionFactory == null || sessionFactory.isClosed()) {
                System.err.println("=== TESTE CONEXÃO: SessionFactory indisponível ===");
                return false;
            }
            
            Session testSession = sessionFactory.openSession();
            Long count = testSession.createQuery("SELECT COUNT(*) FROM Product", Long.class).uniqueResult();
            testSession.close();
            
            System.out.println("=== TESTE CONEXÃO: Sucesso - " + count + " produtos ===");
            return true;
            
        } catch (Exception e) {
            System.err.println("=== TESTE CONEXÃO: Falha - " + e.getMessage() + " ===");
            e.printStackTrace();
            return false;
        }
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