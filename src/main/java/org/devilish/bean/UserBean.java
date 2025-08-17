package org.devilish.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;
import org.devilish.dao.UserDAO;
import org.devilish.impl.UserDAOImpl;
import org.devilish.entity.User;
import org.devilish.exceptions.BusinessException;
import org.devilish.exceptions.DAOException;

@ManagedBean
@ViewScoped
public class UserBean {
    private UserDAO userDAO;
    private User user;
    private List<User> users;
    private String searchEmail;
    private String searchName;
    private boolean editMode = false;
    private String editingUserName;
    
    @PostConstruct
    public void init() {
        System.out.println("=== INICIALIZANDO USER BEAN ===");
        try {
            userDAO = new UserDAOImpl();
            System.out.println("=== USER DAO CRIADO COM SUCESSO ===");
            user = new User();
            loadUsers();
            System.out.println("=== USER BEAN INICIALIZADO - Total usuários: " + (users != null ? users.size() : "null") + " ===");
        } catch (Exception e) {
            System.err.println("=== ERRO AO INICIALIZAR USER BEAN ===");
            e.printStackTrace();
        }
    }
    
    public void save() {
        try {
            if (user.getId() == null) {
                userDAO.save(user);
                addMessage("Usuário cadastrado com sucesso!", FacesMessage.SEVERITY_INFO);
            } else {
                userDAO.update(user);
                addMessage("Usuário '" + user.getName() + "' atualizado com sucesso!", FacesMessage.SEVERITY_INFO);
            }
            clear(); // Limpar e sair do modo de edição
            loadUsers();
        } catch (BusinessException e) {
            addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (DAOException e) {
            addMessage("Erro ao salvar usuário: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void delete(Long id) {
        try {
            System.out.println("=== DELETANDO USUÁRIO ID: " + id + " ===");
            if (id == null) {
                addMessage("ID do usuário não pode ser nulo", FacesMessage.SEVERITY_ERROR);
                return;
            }
            userDAO.delete(id);
            addMessage("Usuário removido com sucesso!", FacesMessage.SEVERITY_INFO);
            loadUsers();
        } catch (DAOException e) {
            System.err.println("=== ERRO DAO AO DELETAR USUÁRIO ===");
            e.printStackTrace();
            addMessage("Erro ao remover usuário: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            System.err.println("=== ERRO GERAL AO DELETAR USUÁRIO ===");
            e.printStackTrace();
            addMessage("Erro inesperado ao remover usuário", FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void edit(User selectedUser) {
        try {
            System.out.println("=== EDITANDO USUÁRIO: " + (selectedUser != null ? selectedUser.getName() + " (ID: " + selectedUser.getId() + ")" : "NULL") + " ===");
            if (selectedUser == null) {
                addMessage("Usuário selecionado não pode ser nulo", FacesMessage.SEVERITY_ERROR);
                return;
            }
            // Entrar no modo de edição
            this.editMode = true;
            this.editingUserName = selectedUser.getName();
            
            // Criar uma cópia para edição
            this.user = new User();
            this.user.setId(selectedUser.getId());
            this.user.setName(selectedUser.getName());
            this.user.setEmail(selectedUser.getEmail());
            
            addMessage("Editando usuário: " + selectedUser.getName(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            System.err.println("=== ERRO AO EDITAR USUÁRIO ===");
            e.printStackTrace();
            addMessage("Erro ao carregar usuário para edição: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void search() {
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            try {
                System.out.println("=== BUSCANDO USUÁRIO POR EMAIL: " + searchEmail + " ===");
                User foundUser = userDAO.findByEmail(searchEmail.trim());
                if (foundUser != null) {
                    users = Arrays.asList(foundUser);
                    addMessage("Usuário encontrado: " + foundUser.getName(), FacesMessage.SEVERITY_INFO);
                    System.out.println("=== USUÁRIO ENCONTRADO: " + foundUser.getName() + " ===");
                } else {
                    users = Arrays.asList(); // Lista vazia
                    addMessage("Nenhum usuário encontrado com o email: " + searchEmail, FacesMessage.SEVERITY_WARN);
                    System.out.println("=== USUÁRIO NÃO ENCONTRADO ===");
                }
            } catch (DAOException e) {
                System.err.println("=== ERRO NA BUSCA ===");
                e.printStackTrace();
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
                loadUsers(); // Em caso de erro, mostra todos
            }
        } else {
            addMessage("Digite um e-mail para buscar", FacesMessage.SEVERITY_WARN);
        }
    }
    
    public void searchByName() {
        if (searchName != null && !searchName.trim().isEmpty()) {
            try {
                System.out.println("=== BUSCANDO USUÁRIO POR NOME: " + searchName + " ===");
                List<User> foundUsers = userDAO.findByNameContaining(searchName.trim());
                if (foundUsers != null && !foundUsers.isEmpty()) {
                    users = foundUsers;
                    addMessage("Encontrados " + foundUsers.size() + " usuário(s) com o nome: " + searchName, FacesMessage.SEVERITY_INFO);
                    System.out.println("=== ENCONTRADOS " + foundUsers.size() + " USUÁRIOS ===");
                } else {
                    users = Arrays.asList(); // Lista vazia
                    addMessage("Nenhum usuário encontrado com o nome: " + searchName, FacesMessage.SEVERITY_WARN);
                    System.out.println("=== NENHUM USUÁRIO ENCONTRADO ===");
                }
            } catch (DAOException e) {
                System.err.println("=== ERRO NA BUSCA POR NOME ===");
                e.printStackTrace();
                addMessage("Erro na busca por nome: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
                loadUsers(); // Em caso de erro, mostra todos
            }
        } else {
            addMessage("Digite um nome para buscar", FacesMessage.SEVERITY_WARN);
        }
    }
    
    public void showAll() {
        clearFilters();
        loadUsers();
        addMessage("Mostrando todos os usuários", FacesMessage.SEVERITY_INFO);
    }
    
    public void clearFilters() {
        searchEmail = null;
        searchName = null;
        System.out.println("=== FILTROS LIMPOS ===");
    }
    
    private void loadUsers() {
        try {
            System.out.println("=== CARREGANDO USUÁRIOS ===");
            users = userDAO.findAll();
            System.out.println("=== USUÁRIOS CARREGADOS: " + (users != null ? users.size() : "null") + " ===");
            if (users != null) {
                users.forEach(u -> System.out.println("Usuário: " + u.getName() + " - " + u.getEmail()));
            }
        } catch (DAOException e) {
            System.err.println("=== ERRO DAO AO CARREGAR USUÁRIOS ===");
            e.printStackTrace();
            addMessage("Erro ao carregar usuários: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            System.err.println("=== ERRO GERAL AO CARREGAR USUÁRIOS ===");
            e.printStackTrace();
        }
    }
    
    private void addMessage(String message, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, message));
    }
    
    public void clear() {
        user = new User();
        addMessage("Formulário limpo", FacesMessage.SEVERITY_INFO);
    }
    
    
    // Getters e Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public String getSearchEmail() { return searchEmail; }
    public void setSearchEmail(String searchEmail) { this.searchEmail = searchEmail; }
    
    public String getSearchName() { return searchName; }
    public void setSearchName(String searchName) { this.searchName = searchName; }
    public boolean isEditMode() { return editMode; }
    public void setEditMode(boolean editMode) { this.editMode = editMode; }
    public String getEditingUserName() { return editingUserName; }
    public void setEditingUserName(String editingUserName) { this.editingUserName = editingUserName; }
}

