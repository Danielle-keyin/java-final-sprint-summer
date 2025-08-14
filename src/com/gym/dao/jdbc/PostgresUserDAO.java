package com.gym.dao.jdbc;

import com.gym.dao.UserDAO;
import com.gym.entities.User;
import java.util.*;

public class PostgresUserDAO implements UserDAO {
    public PostgresUserDAO() {}

    @Override public Optional<User> findByUsername(String username) { throw new UnsupportedOperationException("DB impl pending"); }
    @Override public User create(User user) { throw new UnsupportedOperationException("DB impl pending"); }
    @Override public List<User> findAll() { throw new UnsupportedOperationException("DB impl pending"); }
    @Override public boolean deleteById(int userId) { throw new UnsupportedOperationException("DB impl pending"); }
}
