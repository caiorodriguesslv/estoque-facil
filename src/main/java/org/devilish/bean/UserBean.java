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
                addMessage("Usuário salvo com sucesso!", FacesMessage.SEVERITY_INFO);
            } else {
                userDAO.update(user);
                addMessage("Usuário atualizado com sucesso!", FacesMessage.SEVERITY_INFO);
            }
            user = new User();
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
            // Criar uma cópia para edição
            this.user = new User();
            this.user.setId(selectedUser.getId());
            this.user.setName(selectedUser.getName());
            this.user.setEmail(selectedUser.getEmail());
            addMessage("Usuário carregado para edição: " + selectedUser.getName(), FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            System.err.println("=== ERRO AO EDITAR USUÁRIO ===");
            e.printStackTrace();
            addMessage("Erro ao carregar usuário para edição: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void search() {
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            try {
                User foundUser = userDAO.findByEmail(searchEmail);
                if (foundUser != null) {
                    users = Arrays.asList(foundUser);
                } else {
                    addMessage("Usuário não encontrado", FacesMessage.SEVERITY_WARN);
                    loadUsers();
                }
            } catch (DAOException e) {
                addMessage("Erro na busca: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            loadUsers();
        }
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
        searchEmail = null;
    }
    
    public void cancel() {
        user = new User();
        searchEmail = null;
    }
    
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public String getSearchEmail() { return searchEmail; }
    public void setSearchEmail(String searchEmail) { this.searchEmail = searchEmail; }
}

