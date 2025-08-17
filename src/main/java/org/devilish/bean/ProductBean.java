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
        try {
            if (searchCode != null && !searchCode.trim().isEmpty()) {
                System.out.println("=== BUSCANDO PRODUTO COM CÓDIGO: " + searchCode + " ===");
                
                // Primeiro tenta busca exata
                Product foundProduct = productDAO.findByCode(searchCode.trim());
                if (foundProduct != null) {
                    products = java.util.Arrays.asList(foundProduct);
                    addMessage("Produto encontrado!", FacesMessage.SEVERITY_INFO);
                    System.out.println("=== PRODUTO ENCONTRADO: " + foundProduct.getCode() + " ===");
                } else {
                    // Se não encontrou com busca exata, tenta busca parcial
                    List<Product> partialResults = productDAO.findByCodeContaining(searchCode.trim());
                    if (partialResults != null && !partialResults.isEmpty()) {
                        products = partialResults;
                        addMessage("Encontrados " + partialResults.size() + " produto(s) com código similar", FacesMessage.SEVERITY_INFO);
                        System.out.println("=== BUSCA PARCIAL - ENCONTRADOS: " + partialResults.size() + " ===");
                    } else {
                        addMessage("Nenhum produto encontrado com o código '" + searchCode + "'", FacesMessage.SEVERITY_WARN);
                        System.out.println("=== NENHUM PRODUTO ENCONTRADO ===");
                        loadProducts(); // Recarrega todos os produtos
                    }
                }
            } else {
                System.out.println("=== CARREGANDO TODOS OS PRODUTOS ===");
                loadProducts(); // Se campo vazio, mostra todos
            }
            loadExpiredProducts(); // Sempre recarrega produtos vencidos
        } catch (DAOException e) {
            System.err.println("=== ERRO DAO NA BUSCA: " + e.getMessage() + " ===");
            e.printStackTrace();
            addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            loadProducts(); // Em caso de erro, recarrega todos
        } catch (Exception e) {
            System.err.println("=== ERRO GERAL NA BUSCA: " + e.getMessage() + " ===");
            e.printStackTrace();
            addMessage("Erro inesperado na busca", FacesMessage.SEVERITY_ERROR);
            loadProducts(); // Em caso de erro, recarrega todos
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
    
    public void clearSearch() {
        searchCode = null;
        loadProducts(); // Recarrega todos os produtos
        addMessage("Busca limpa. Exibindo todos os produtos.", FacesMessage.SEVERITY_INFO);
    }
    
    public void onSearchKeyUp() {
        // Método para capturar eventos de teclado
        // Pode ser usado para implementar busca em tempo real no futuro
        System.out.println("=== CAMPO DE BUSCA ALTERADO: " + searchCode + " ===");
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
