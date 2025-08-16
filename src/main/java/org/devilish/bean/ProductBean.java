package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.devilish.dao.ProductDAO;
import org.devilish.impl.ProductDAOImpl;
import org.devilish.entity.Product;
import org.devilish.exceptions.BusinessException;
import org.devilish.exceptions.DAOException;
import java.util.List;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Arrays;


@ManagedBean
@ViewScoped
public class ProductBean {
    
    private ProductDAO productDAO;
    private Product product;
    private List<Product> products;
    private List<Product> expiredProducts;
    private String searchCode;
    private LocalDate searchExpiryDate;
    private boolean editMode = false;
    
    @PostConstruct
    public void init() {
        productDAO = new ProductDAOImpl();
        product = new Product();
        loadProducts();
        loadExpiredProducts();
    }
    
    public void save() {
        try {
            if (product.getCode() == null || product.getCode().trim().isEmpty()) {
                addMessage("Código do produto é obrigatório!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            if (!editMode && productDAO.existsByCode(product.getCode())) {
                addMessage("Produto com este código já existe!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            if (editMode) {
                productDAO.update(product);
                addMessage("Produto atualizado com sucesso!", FacesMessage.SEVERITY_INFO);
            } else {
                productDAO.save(product);
                addMessage("Produto salvo com sucesso!", FacesMessage.SEVERITY_INFO);
            }
            
            product = new Product();
            editMode = false;
            loadProducts();
            loadExpiredProducts();
        } catch (BusinessException e) {
            addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (DAOException e) {
            addMessage("Erro ao salvar produto: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    

    
    public void delete(String code) {
        try {
            productDAO.delete(code);
            addMessage("Produto removido com sucesso!", FacesMessage.SEVERITY_INFO);
            loadProducts();
            loadExpiredProducts();
        } catch (DAOException e) {
            addMessage("Erro ao remover produto: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void searchByCode() {
        if (searchCode != null && !searchCode.trim().isEmpty()) {
            try {
                Product foundProduct = productDAO.findByCode(searchCode);
                if (foundProduct != null) {
                    products = Arrays.asList(foundProduct);
                } else {
                    addMessage("Produto não encontrado", FacesMessage.SEVERITY_WARN);
                    loadProducts();
                }
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadProducts();
        }
    }
    
    public void searchByExpiryDate() {
        if (searchExpiryDate != null) {
            try {
                products = productDAO.findByExpiryDate(searchExpiryDate);
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadProducts();
        }
    }
    
    public void updateQuantity(String code, BigDecimal newQuantity) {
        try {
            productDAO.updateQuantity(code, newQuantity);
            addMessage("Quantidade atualizada com sucesso!", FacesMessage.SEVERITY_INFO);
            loadProducts();
        } catch (BusinessException e) {
            addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (DAOException e) {
            addMessage("Erro ao atualizar quantidade: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadProducts() {
        try {
            products = productDAO.findAll();
        } catch (DAOException e) {
            addMessage("Erro ao carregar produtos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void loadExpiredProducts() {
        try {
            expiredProducts = productDAO.findExpiredProducts();
        } catch (DAOException e) {
            addMessage("Erro ao carregar produtos vencidos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, message));
    }
    
    public void clear() {
        product = new Product();
        searchCode = null;
        searchExpiryDate = null;
        editMode = false;
    }
    
    public void cancel() {
        product = new Product();
        searchCode = null;
        searchExpiryDate = null;
        editMode = false;
    }
    
    public void edit(Product product) {
        this.product = product;
        this.editMode = true;
    }
    
   
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public List<Product> getExpiredProducts() { return expiredProducts; }
    public String getSearchCode() { return searchCode; }
    public void setSearchCode(String searchCode) { this.searchCode = searchCode; }
    public LocalDate getSearchExpiryDate() { return searchExpiryDate; }
    public void setSearchExpiryDate(LocalDate searchExpiryDate) { this.searchExpiryDate = searchExpiryDate; }
    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
}

