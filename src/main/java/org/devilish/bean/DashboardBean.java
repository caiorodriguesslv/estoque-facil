package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.devilish.dao.UserDAO;
import org.devilish.dao.ProductDAO;
import org.devilish.impl.UserDAOImpl;
import org.devilish.impl.ProductDAOImpl;
import org.devilish.exceptions.DAOException;
import java.util.List;
import java.io.Serializable;
import org.devilish.entity.User;
import org.devilish.entity.Product;

@ManagedBean
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private transient UserDAO userDAO;
    private transient ProductDAO productDAO;
    
    private int totalUsers = 0;
    private int totalProducts = 0;
    private int expiredProducts = 0;
    private boolean initialized = false;
    private boolean dataLoaded = false;
    private String errorMessage;
    
    @PostConstruct
    public void init() {
        // Apenas inicializa os DAOs, não carrega dados
        try {
            initializeDAOs();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }
    
    /**
     * Método lazy para carregar dados apenas quando necessário
     * É chamado automaticamente pelos getters quando os dados são acessados
     */
    private void loadDataIfNeeded() {
        if (!initialized) {
            return;
        }
        
        if (!dataLoaded) {
            try {
                loadStatistics();
                dataLoaded = true;
            } catch (Exception e) {
                handleStatisticsError(e);
            }
        }
    }
    
    private void initializeDAOs() {
        try {
            userDAO = new UserDAOImpl();
            productDAO = new ProductDAOImpl();
            initialized = true;
            errorMessage = null;
        } catch (Exception e) {
            initialized = false;
            errorMessage = "Erro ao conectar com o banco de dados: " + e.getMessage();
            throw e;
        }
    }
    
    private void handleInitializationError(Exception e) {
        initialized = false;
        totalUsers = 0;
        totalProducts = 0;
        expiredProducts = 0;
        errorMessage = "Sistema temporariamente indisponível. Tente novamente.";
        
        if (FacesContext.getCurrentInstance() != null) {
            addMessage("Erro ao carregar dados do sistema: " + e.getMessage(), 
                      FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadStatistics() {
        if (!initialized) {
            return;
        }
        
        try {
            List<User> users = userDAO.findAll();
            totalUsers = (users != null) ? users.size() : 0;
            
            List<Product> products = productDAO.findAll();
            totalProducts = (products != null) ? products.size() : 0;
            
            List<Product> expired = productDAO.findExpiredProducts();
            expiredProducts = (expired != null) ? expired.size() : 0;
            
            errorMessage = null;
            
        } catch (DAOException e) {
            handleStatisticsError(e);
        } catch (Exception e) {
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
    
    /**
     * Força o recarregamento dos dados
     */
    public void refreshStatistics() {
        try {
            if (!initialized) {
                initializeDAOs();
            }
            dataLoaded = false; // Força recarregamento
            loadStatistics();
            dataLoaded = true;
            addMessage("Estatísticas atualizadas com sucesso!", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            addMessage("Erro ao atualizar estatísticas: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void retryInitialization() {
        init();
        if (initialized) {
            dataLoaded = false; // Reseta flag para forçar carregamento
            addMessage("Sistema reinicializado com sucesso!", FacesMessage.SEVERITY_INFO);
        }
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        if (FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(severity, message, message));
        }
    }
    
    // Getters com inicialização lazy
    public int getTotalUsers() {
        loadDataIfNeeded();
        return totalUsers;
    }
    
    public int getTotalProducts() {
        loadDataIfNeeded();
        return totalProducts;
    }
    
    public int getExpiredProducts() {
        loadDataIfNeeded();
        return expiredProducts;
    }
    
    public String getProductStatus() {
        loadDataIfNeeded();
        if (expiredProducts > 0) {
            return "warning";
        }
        return "success";
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isDataLoaded() {
        return dataLoaded;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean getHasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
    
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
    
    public String getExpiredProductsClass() {
        loadDataIfNeeded();
        return expiredProducts > 0 ? "expired-danger" : "expired-safe";
    }
    
    public String getExpiredProductsBadgeClass() {
        loadDataIfNeeded();
        return expiredProducts > 0 ? "status-error" : "status-ok";
    }
    
    public String getExpiredProductsStatus() {
        loadDataIfNeeded();
        return expiredProducts > 0 ? "ATENÇÃO" : "OK";
    }
    
    // Métodos de navegação para as rotas
    public String goToUsers() {
        return "users?faces-redirect=true";
    }
    
    public String goToProducts() {
        return "products?faces-redirect=true";
    }
    
    public String goToPayments() {
        return "payments?faces-redirect=true";
    }
    
    public String goToDashboard() {
        return "index?faces-redirect=true";
    }
}
