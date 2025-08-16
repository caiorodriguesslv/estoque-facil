package org.devilish.dao;

import org.devilish.entity.User;
import java.util.List;

public interface UserDAO {
    void save(User user);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
    boolean existsByEmail(String email);
}
