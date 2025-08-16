package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.devilish.dao.UserDAO;
import org.devilish.dao.ProductDAO;
import org.devilish.impl.UserDAOImpl;
import org.devilish.impl.ProductDAOImpl;
import org.devilish.exceptions.DAOException;
import java.util.List;
import org.devilish.entity.User;
import org.devilish.entity.Product;

@ManagedBean
@SessionScoped
public class DashboardBean {
    
    private UserDAO userDAO;
    private ProductDAO productDAO;
    
    private int totalUsers = 0;
    private int totalProducts = 0;
    private int expiredProducts = 0;
    private boolean initialized = false;
    private String errorMessage;
    
    @PostConstruct
    public void init() {
        System.out.println("=== INICIALIZANDO DASHBOARD BEAN ===");
        try {
            initializeDAOs();
            
       
            if (initialized) {
                loadStatistics();
                System.out.println("=== DASHBOARD BEAN INICIALIZADO COM SUCESSO ===");
                System.out.println("=== TOTAL USUARIOS: " + totalUsers + " ===");
                System.out.println("=== TOTAL PRODUTOS: " + totalProducts + " ===");
            }
        } catch (Exception e) {
            System.err.println("=== ERRO AO INICIALIZAR DASHBOARD BEAN ===");
            e.printStackTrace();
            handleInitializationError(e);
        }
    }
    
    private void initializeDAOs() {
        try {
            System.out.println("=== INICIALIZANDO DAOs ===");
            userDAO = new UserDAOImpl();
            productDAO = new ProductDAOImpl();
            initialized = true;
            errorMessage = null;
            System.out.println("=== DAOs INICIALIZADOS COM SUCESSO ===");
        } catch (Exception e) {
            initialized = false;
            errorMessage = "Erro ao conectar com o banco de dados: " + e.getMessage();
            System.err.println("=== ERRO AO INICIALIZAR DAOs ===");
            throw e;
        }
    }
    
    private void handleInitializationError(Exception e) {
        initialized = false;
        totalUsers = 0;
        totalProducts = 0;
        expiredProducts = 0;
        errorMessage = "Sistema temporariamente indisponível. Tente novamente.";
        
        // Adicionar mensagem para o usuário se o contexto JSF estiver disponível
        if (FacesContext.getCurrentInstance() != null) {
            addMessage("Erro ao carregar dados do sistema: " + e.getMessage(), 
                      FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadStatistics() {
        if (!initialized) {
            System.out.println("=== DAOs NÃO INICIALIZADOS - PULANDO CARREGAMENTO ===");
            return;
        }
        
        try {
            // Carregar total de usuários com verificação de nulo
            List<User> users = userDAO.findAll();
            totalUsers = (users != null) ? users.size() : 0;
            System.out.println("=== TOTAL DE USUÁRIOS: " + totalUsers + " ===");
            
            
            List<Product> products = productDAO.findAll();
            totalProducts = (products != null) ? products.size() : 0;
            System.out.println("=== TOTAL DE PRODUTOS: " + totalProducts + " ===");
            
            
            List<Product> expired = productDAO.findExpiredProducts();
            expiredProducts = (expired != null) ? expired.size() : 0;
            System.out.println("=== PRODUTOS VENCIDOS: " + expiredProducts + " ===");
            
            
            errorMessage = null;
            
        } catch (DAOException e) {
            System.err.println("=== ERRO AO CARREGAR ESTATÍSTICAS ===");
            e.printStackTrace();
            handleStatisticsError(e);
        } catch (Exception e) {
            System.err.println("=== ERRO INESPERADO AO CARREGAR ESTATÍSTICAS ===");
            e.printStackTrace();
            handleStatisticsError(e);
        }
    }
    
    private void handleStatisticsError(Exception e) {
        if (totalUsers < 0) totalUsers = 0;
        if (totalProducts < 0) totalProducts = 0;
        if (expiredProducts < 0) expiredProducts = 0;
        
        errorMessage = "Erro ao carregar estatísticas: " + e.getMessage();
        
       
        if (FacesContext.getCurrentInstance() != null) {
            addMessage("Erro ao atualizar estatísticas. Dados podem estar desatualizados.", 
                      FacesMessage.SEVERITY_WARN);
        }
    }
    
    
    public void refreshStatistics() {
        try {
            if (!initialized) {
                initializeDAOs();
            }
            loadStatistics();
            addMessage("Estatísticas atualizadas com sucesso!", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            System.err.println("=== ERRO AO ATUALIZAR ESTATÍSTICAS ===");
            e.printStackTrace();
            addMessage("Erro ao atualizar estatísticas: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    
    public void retryInitialization() {
        init();
        if (initialized) {
            addMessage("Sistema reinicializado com sucesso!", FacesMessage.SEVERITY_INFO);
        }
    }
    
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        if (FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(severity, message, message));
        }
    }
    
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public int getTotalProducts() {
        return totalProducts;
    }
    
    public int getExpiredProducts() {
        return expiredProducts;
    }
    
    // Getter para status dos produtos (para exibição visual)
    public String getProductStatus() {
        if (expiredProducts > 0) {
            return "warning";
        }
        return "success";
    }
    

    
    // Getter para verificar se o sistema está inicializado
    public boolean isInitialized() {
        return initialized;
    }
    
   
    public String getErrorMessage() {
        return errorMessage;
    }
    
    
    public boolean getHasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
    
    // Método alternativo para compatibilidade com JSF
    public boolean isHasError() {
        return getHasError();
    }
    
    
    public String getSystemStatus() {
        if (!initialized) {
            return "ERRO";
        } else if (getHasError()) {
            return "ATENÇÃO";
        } else {
            return "OK";
        }
    }
    
    // Getter para classe CSS do status do sistema
    public String getSystemStatusClass() {
        String status = getSystemStatus();
        switch (status) {
            case "OK":
                return "status-ok";
            case "ATENÇÃO":
                return "status-warning";
            case "ERRO":
                return "status-error";
            default:
                return "status-ok";
        }
    }
    
    // Getter para classe CSS dos produtos vencidos
    public String getExpiredProductsClass() {
        return expiredProducts > 0 ? "expired-danger" : "expired-safe";
    }
    
    // Getter para classe CSS do badge de produtos vencidos
    public String getExpiredProductsBadgeClass() {
        return expiredProducts > 0 ? "status-error" : "status-ok";
    }
    
    // Getter para texto de status dos produtos vencidos
    public String getExpiredProductsStatus() {
        return expiredProducts > 0 ? "ATENÇÃO" : "OK";
    }
}
