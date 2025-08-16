package com.gym.services;

import com.gym.dao.UserDAO;
import com.gym.entities.*;
import com.gym.util.Log;
import com.gym.util.PasswordUtil;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserService {
	private final UserDAO userDAO;
	private final Logger log = Log.get();

	public UserService(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public Optional<User> login(String username, String plainPassword) {
		Optional<User> found = this.userDAO.findByUsername(username);
		if (found.isPresent() && PasswordUtil.check(plainPassword, found.get().getHashedPassword())) {
			this.log.info("Login success for user: " + username);
			return found;
		}
		this.log.warning("Login failed for user: " + username);
		return Optional.empty();
	}

	public User register(String username, String plainPassword, String email, String phone, String address, Role role) {
		String hash = PasswordUtil.hash(plainPassword);
		User user = switch (role) {
			case ADMIN -> new Admin(0, username, hash, email, phone, address);
			case TRAINER -> new Trainer(0, username, hash, email, phone, address);
			case MEMBER -> new Member(0, username, hash, email, phone, address);
		};
		var created = this.userDAO.create(user);
		this.log.info("Registered new " + role + " username=" + username + " id=" + created.getId());
		return created;
	}
	
	public Optional<User> findByUsername(String username) {
		return this.userDAO.findByUsername(username);
	}

	public List<User> findAll() {
		return this.userDAO.findAll();
	}

	// ✅ Added: Admin delete by ID (with logging)
	public boolean deleteUser(int userId) {
		boolean ok = userDAO.deleteById(userId);
		if (ok) log.info("Deleted user id=" + userId);
		else    log.warning("Delete failed for user id=" + userId);
		return ok;
	}
}
