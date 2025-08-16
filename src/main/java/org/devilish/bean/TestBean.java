package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;

@ManagedBean
@ViewScoped
public class TestBean {
    
    private String message;
    
    @PostConstruct
    public void init() {
        System.out.println("=== TEST BEAN INICIALIZADO ===");
        message = "Teste funcionando!";
        
        // Teste direto de conexão
        try {
            System.out.println("=== TESTANDO CONEXÃO DIRETA ===");
            // Aqui vamos testar se conseguimos pelo menos instanciar as classes
            org.devilish.util.HibernateUtil.getSessionFactory();
            System.out.println("=== HIBERNATE OK ===");
        } catch (Exception e) {
            System.err.println("=== ERRO NO HIBERNATE ===");
            e.printStackTrace();
        }
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
