package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.devilish.dao.PaymentDAO;
import org.devilish.dao.ProductDAO;
import org.devilish.dao.UserDAO;
import org.devilish.impl.PaymentDAOImpl;
import org.devilish.impl.ProductDAOImpl;
import org.devilish.impl.UserDAOImpl;
import org.devilish.entity.Payment;
import org.devilish.entity.Product;
import org.devilish.entity.User;
import org.devilish.exceptions.BusinessException;
import org.devilish.exceptions.DAOException;

@ManagedBean
@ViewScoped
public class PaymentBean {

    private PaymentDAO paymentDAO;
    private ProductDAO productDAO;
    private UserDAO userDAO;
    
    private Payment payment;
    private List<Payment> payments;
    private List<Product> availableProducts;
    private List<User> availableUsers;
    
    private Long selectedUserId;
    private String selectedProductCode;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @PostConstruct
    public void init() {
        paymentDAO = new PaymentDAOImpl();
        productDAO = new ProductDAOImpl();
        userDAO = new UserDAOImpl();
        
        payment = new Payment();
        loadPayments();
        loadAvailableProducts();
        loadAvailableUsers();
    }
    
    public void save() {
        try {
            // Validar se produto e usuário foram selecionados
            if (selectedProductCode == null || selectedUserId == null) {
                addMessage("Produto e usuário são obrigatórios!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            // Buscar produto e usuário
            Product product = productDAO.findByCode(selectedProductCode);
            User user = userDAO.findById(selectedUserId);
            
            if (product == null || user == null) {
                addMessage("Produto ou usuário não encontrado!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            // Validar quantidade disponível
            if (product.getQuantity().compareTo(payment.getPaidQuantity()) < 0) {
                addMessage("Quantidade insuficiente em estoque!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            // Configurar relacionamentos
            payment.setProduct(product);
            payment.setUser(user);
            
            // Salvar pagamento
            paymentDAO.save(payment);
            
            // Atualizar quantidade do produto
            BigDecimal newQuantity = product.getQuantity().subtract(payment.getPaidQuantity());
            productDAO.updateQuantity(selectedProductCode, newQuantity);
            
            addMessage("Pagamento registrado com sucesso!", FacesMessage.SEVERITY_INFO);
            payment = new Payment();
            selectedProductCode = null;
            selectedUserId = null;
            loadPayments();
            loadAvailableProducts();
        } catch (BusinessException e) {
            addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (DAOException e) {
            addMessage("Erro ao registrar pagamento: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void delete(Long id) {
        try {
            paymentDAO.delete(id);
            addMessage("Pagamento removido com sucesso!", FacesMessage.SEVERITY_INFO);
            loadPayments();
        } catch (DAOException e) {
            addMessage("Erro ao remover pagamento: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void searchByUser() {
        if (selectedUserId != null) {
            try {
                payments = paymentDAO.findByUser(selectedUserId);
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadPayments();
        }
    }
    
    public void searchByProduct() {
        if (selectedProductCode != null) {
            try {
                payments = paymentDAO.findByProduct(selectedProductCode);
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadPayments();
        }
    }
    
    public void searchByDateRange() {
        if (startDate != null && endDate != null) {
            try {
                payments = paymentDAO.findByDateRange(startDate, endDate);
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadPayments();
        }
    }
    
    private void loadPayments() {
        try {
            payments = paymentDAO.findAll();
        } catch (DAOException e) {
            addMessage("Erro ao carregar pagamentos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadAvailableProducts() {
        try {
            availableProducts = productDAO.findAll();
        } catch (DAOException e) {
            addMessage("Erro ao carregar produtos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadAvailableUsers() {
        try {
            availableUsers = userDAO.findAll();
        } catch (DAOException e) {
            addMessage("Erro ao carregar usuários: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, message));
    }
    
    public void clear() {
        payment = new Payment();
        selectedUserId = null;
        selectedProductCode = null;
        startDate = null;
        endDate = null;
    }
    
    public void cancel() {
        payment = new Payment();
        selectedUserId = null;
        selectedProductCode = null;
        startDate = null;
        endDate = null;
    }
    
    
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<Product> getAvailableProducts() { return availableProducts; }
    public List<User> getAvailableUsers() { return availableUsers; }
    public Long getSelectedUserId() { return selectedUserId; }
    public void setSelectedUserId(Long selectedUserId) { this.selectedUserId = selectedUserId; }
    public String getSelectedProductCode() { return selectedProductCode; }
    public void setSelectedProductCode(String selectedProductCode) { this.selectedProductCode = selectedProductCode; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
