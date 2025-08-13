package com.gym.dao.mock;

import com.gym.dao.UserDAO;
import com.gym.entities.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryUserDAO implements UserDAO {
    private final Map<String, User> byUsername = new HashMap<>();
    private final AtomicInteger idSeq = new AtomicInteger(1);

    @Override
    public Optional<User> findByUsername(String username){
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public User create(User user){
        int id = idSeq.getAndIncrement();
        User created = switch (user.getRole()){
            case ADMIN -> new Admin(  id, user.getUsername(), user.getHashedPassword(),
                                      user.getEmail(), user.getPhone(), user.getAddress());
            case TRAINER -> new Trainer(id, user.getUsername(), user.getHashedPassword(),
                                        user.getEmail(), user.getPhone(), user.getAddress());
            case MEMBER -> new Member( id, user.getUsername(), user.getHashedPassword(),
                                       user.getEmail(), user.getPhone(), user.getAddress());
        };
        byUsername.put(created.getUsername(), created);
        return created;
    }

    @Override
    public List<User> findAll(){ return new ArrayList<>(byUsername.values()); }

    @Override
    public boolean deleteById(int userId){
        String key = byUsername.keySet().stream()
            .filter(u -> byUsername.get(u).getId() == userId)
            .findFirst().orElse(null);
        if (key == null) return false;
        byUsername.remove(key);
        return true;
    }
}