package com.gym.dao;

import com.gym.entities.*;
import java.util.*;

public interface UserDAO {
    Optional<User> findByUsername(String username);
    User create(User user);                  // returns user with id set
    List<User> findAll();
    boolean deleteById(int userId);
}