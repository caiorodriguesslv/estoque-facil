package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.time.LocalDate;
import org.devilish.dao.ProductDAO;
import org.devilish.impl.ProductDAOImpl;
import org.devilish.entity.Product;
import org.devilish.exceptions.BusinessException;
import org.devilish.exceptions.DAOException;

@ManagedBean
@ViewScoped
public class ProductBean {
    private ProductDAO productDAO;
    private Product product;
    private List<Product> products;
    private List<Product> expiredProducts;
    private String searchCode;
    private LocalDate searchExpiryDate;
    
    @PostConstruct
    public void init() {
        System.out.println("=== INICIALIZANDO PRODUCT BEAN ===");
        try {
            productDAO = new ProductDAOImpl();
            System.out.println("=== PRODUCT DAO CRIADO COM SUCESSO ===");
            product = new Product();
            loadProducts();
            loadExpiredProducts();
            System.out.println("=== PRODUCT BEAN INICIALIZADO - Total produtos: " + (products != null ? products.size() : "null") + " ===");
        } catch (Exception e) {
            System.err.println("=== ERRO AO INICIALIZAR PRODUCT BEAN ===");
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            if (product.getCode() == null || product.getCode().trim().isEmpty()) {
                addMessage("Código do produto é obrigatório!", FacesMessage.SEVERITY_ERROR);
                return;
            }
            
            // Verifica se é um novo produto ou atualização
            try {
                Product existing = productDAO.findByCode(product.getCode());
                if (existing != null) {
                    productDAO.update(product);
                    addMessage("Produto atualizado com sucesso!", FacesMessage.SEVERITY_INFO);
                } else {
                    productDAO.save(product);
                    addMessage("Produto salvo com sucesso!", FacesMessage.SEVERITY_INFO);
                }
            } catch (DAOException e) {
                // Se não encontrou, é um produto novo
                productDAO.save(product);
                addMessage("Produto salvo com sucesso!", FacesMessage.SEVERITY_INFO);
            }
            
            product = new Product();
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
    
    public void edit(Product product) {
        this.product = product;
    }
    
    public void search() {
        if (searchCode != null && !searchCode.trim().isEmpty()) {
            try {
                Product foundProduct = productDAO.findByCode(searchCode);
                if (foundProduct != null) {
                    products = java.util.Arrays.asList(foundProduct);
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
    
    private void loadProducts() {
        try {
            System.out.println("=== CARREGANDO PRODUTOS ===");
            products = productDAO.findAll();
            System.out.println("=== PRODUTOS CARREGADOS: " + (products != null ? products.size() : "null") + " ===");
            if (products != null) {
                products.forEach(p -> System.out.println("Produto: " + p.getCode() + " - " + p.getDescription()));
            }
        } catch (DAOException e) {
            System.err.println("=== ERRO DAO AO CARREGAR PRODUTOS ===");
            e.printStackTrace();
            addMessage("Erro ao carregar produtos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            System.err.println("=== ERRO GERAL AO CARREGAR PRODUTOS ===");
            e.printStackTrace();
        }
    }
    
    private void loadExpiredProducts() {
        try {
            System.out.println("=== CARREGANDO PRODUTOS VENCIDOS ===");
            expiredProducts = productDAO.findExpiredProducts();
            System.out.println("=== PRODUTOS VENCIDOS CARREGADOS: " + (expiredProducts != null ? expiredProducts.size() : "null") + " ===");
        } catch (DAOException e) {
            System.err.println("=== ERRO DAO AO CARREGAR PRODUTOS VENCIDOS ===");
            e.printStackTrace();
            addMessage("Erro ao carregar produtos vencidos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            System.err.println("=== ERRO GERAL AO CARREGAR PRODUTOS VENCIDOS ===");
            e.printStackTrace();
        }
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, message));
    }
    
    public void clear() {
        product = new Product();
        searchCode = null;
    }
    
    public void cancel() {
        product = new Product();
        searchCode = null;
    }
    
    // Getters e Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public List<Product> getExpiredProducts() { return expiredProducts; }
    public void setExpiredProducts(List<Product> expiredProducts) { this.expiredProducts = expiredProducts; }
    public String getSearchCode() { return searchCode; }
    public void setSearchCode(String searchCode) { this.searchCode = searchCode; }
    public LocalDate getSearchExpiryDate() { return searchExpiryDate; }
    public void setSearchExpiryDate(LocalDate searchExpiryDate) { this.searchExpiryDate = searchExpiryDate; }
}
