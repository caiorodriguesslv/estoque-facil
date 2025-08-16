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
        userDAO = new UserDAOImpl();
        user = new User();
        loadUsers();
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
            userDAO.delete(id);
            addMessage("Usuário removido com sucesso!", FacesMessage.SEVERITY_INFO);
            loadUsers();
        } catch (DAOException e) {
            addMessage("Erro ao remover usuário: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    
    public void edit(User user) {
        this.user = user;
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
            users = userDAO.findAll();
        } catch (DAOException e) {
            addMessage("Erro ao carregar usuários: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
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

