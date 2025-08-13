package com.gym.services;

import com.gym.dao.UserDAO;
import com.gym.entities.*;
import com.gym.util.Log;
import com.gym.util.PasswordUtil;

import java.util.Optional;
import java.util.logging.Logger;

public class UserService {
    private final UserDAO userDAO;
    private final Logger log = Log.get();

    public UserService(UserDAO userDAO){ this.userDAO = userDAO; }

    public Optional<User> login(String username, String plainPassword){
        Optional<User> found = userDAO.findByUsername(username);
        if (found.isPresent() && PasswordUtil.check(plainPassword, found.get().getHashedPassword())){
            log.info("Login success for user: " + username);
            return found;
        }
        log.warning("Login failed for user: " + username);
        return Optional.empty();
    }

    public User register(String username, String plainPassword, String email,
                         String phone, String address, Role role){
        String hash = PasswordUtil.hash(plainPassword);
        User user = switch (role) {
            case ADMIN   -> new Admin(0, username, hash, email, phone, address);
            case TRAINER -> new Trainer(0, username, hash, email, phone, address);
            case MEMBER  -> new Member(0, username, hash, email, phone, address);
        };
        var created = userDAO.create(user);
        log.info("Registered new " + role + " username=" + username + " id=" + created.getId());
        return created;
    }
}