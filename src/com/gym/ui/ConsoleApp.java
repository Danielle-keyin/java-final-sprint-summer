package com.gym.ui;

import com.gym.dao.MerchandiseDAO;
import com.gym.dao.WorkoutClassDAO;
import com.gym.dao.jdbc.PostgresMembershipDAO;
import com.gym.dao.jdbc.PostgresMerchandiseDAO;
import com.gym.dao.jdbc.PostgresUserDAO;
import com.gym.dao.jdbc.PostgresWorkoutClassDAO;
import com.gym.entities.Role;
import com.gym.entities.User;
import com.gym.entities.WorkoutClass;
import com.gym.services.MembershipService;
import com.gym.services.MerchandiseService;
import com.gym.services.UserService;
import com.gym.util.Log;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class ConsoleApp {
	private final Scanner in = new Scanner(System.in);
	private final Logger log = Log.get();

	// private final UserService users =
	// new UserService(new InMemoryUserDAO());
	// private final MembershipService memberships =
	// new MembershipService(new InMemoryMembershipDAO());
	// private final MerchandiseService merch =
	// new MerchandiseService(new InMemoryMerchandiseDAO());

	private final UserService users = new UserService(new PostgresUserDAO());
	private final MembershipService memberships = new MembershipService(new PostgresMembershipDAO());
	private final MerchandiseService merch = new MerchandiseService(new PostgresMerchandiseDAO());
	private final WorkoutClassDAO workoutClasses = new PostgresWorkoutClassDAO();

	public void start() {
		while (true) {
			System.out.println("\n=== Gym System ===");
			System.out.println("1) Register");
			System.out.println("2) Login");
			System.out.println();
			System.out.println("0) Exit");
			System.out.print("Choose: ");
			String choice = this.in.nextLine().trim();
			switch (choice) {
				case "1" -> registerFlow();
				case "2" -> loginFlow();
				case "0" -> {
					System.out.println("Bye!");
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void registerFlow() {
		System.out.println("\n-- Register --");
		System.out.print("Username: ");
		String username = this.in.nextLine();
		System.out.print("Password: ");
		String password = this.in.nextLine();
		System.out.print("Email: ");
		String email = this.in.nextLine();
		System.out.print("Phone: ");
		String phone = this.in.nextLine();
		System.out.print("Address: ");
		String address = this.in.nextLine();
		Role role = readRole();
		var created = this.users.register(username, password, email, phone, address, role);
		System.out.println("Registered as " + created.getRole() + " (id " + created.getId() + ")");
		this.log.info("New user registered: " + created.getUsername() + "(#" + created.getId() + ")" + "as"
				+ created.getRole());
	}

	private void loginFlow() {
		System.out.println("\n-- Login --");
		System.out.print("Username: ");
		String username = this.in.nextLine();
		System.out.print("Password: ");
		String password = this.in.nextLine();
		var userOpt = this.users.login(username, password);
		if (userOpt.isEmpty()) {
			System.out.println("Invalid credentials.");
			this.log.warning("Login failure for: " + username);
			return;
		}
		var user = userOpt.get();
		this.log.info("Login success for: " + user.getUsername() + " (id " + user.getId() + ") as " + user.getRole());
		switch (user.getRole()) {
			case ADMIN -> adminMenu(user);
			case TRAINER -> trainerMenu(user);
			case MEMBER -> memberMenu(user);
			default -> throw new IllegalArgumentException("Unexpected value: " + user.getRole());
		}
	}

	private Role readRole() {
		while (true) {
			System.out.print("Role (ADMIN/TRAINER/MEMBER): ");
			String r = this.in.nextLine().trim().toUpperCase();
			try {
				return Role.valueOf(r);
			} catch (Exception e) {
				System.out.println("Try again.");
			}
		}
	}

	private void adminMenu(User user) {
		while (true) {
			System.out.println("\n-- Admin Menu --");
			System.out.println("1) View all users");
			System.out.println("2) View membership revenue");
			System.out.println("3) Add merchandise item");
			System.out.println("4) Edit merchandise item");
			System.out.println("5) View stock + total value");
			System.out.println("6) Delete user by ID");
			System.out.println();
			System.out.println("0) Logout");
			System.out.print("Choose: ");
			String c = this.in.nextLine();
			switch (c) {
				case "1" -> {
					usersList();
					this.log.info("Admin "+user.getUsername()+" listed users");
				}
				case "2" -> {
					System.out.println("Total revenue: $" + this.memberships.totalRevenue());
					this.log.info("Admin "+user.getUsername()+" viewed total membership revenue");
				}
				case "3" -> addMerch(user);
				case "4" -> updateMerch(user);
				case "5" -> {
					this.merch.list().forEach(System.out::println);
					System.out.println("Stock value: $" + this.merch.stockValue());
					this.log.info("Admin "+user.getUsername()+" viewed stock list");
				}
				case "6" -> deleteUserFlow(user);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid option selected, Please try again.");
			}
		}
	}

	private void trainerMenu(User user) {
		while (true) {
			System.out.println("\n-- Trainer Menu --");
			System.out.println("1) Workout classes menu");
			System.out.println("2) Purchase membership");
			System.out.println("3) View merchandise");
			System.out.println();
			System.out.println("0) Logout");
			System.out.print("Choose: ");
			String c = this.in.nextLine();
			switch (c) {
				case "1" -> trainerClassesMenu(user);
				case "2" -> buyMembership(user);
				case "3" -> {
					this.merch.list().forEach(System.out::println);
					this.log.info("Trainer " + user.getUsername() + " viewed merchandise list");
				}
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void trainerClassesMenu(User user) {
		while (true) {
			System.out.println("\n-- Trainer Classes Menu --");
			System.out.println("1) View my workout classes");
			System.out.println("2) Add workout class");
			System.out.println("3) Update workout class");
			System.out.println("4) Delete workout class");
			System.out.println();
			System.out.println("0) Back to Trainer Menu");
			System.out.print("Choose: ");
			String c = this.in.nextLine();
			switch (c) {
				case "1" -> {
					this.workoutClasses.findByTrainer(user.getId()).forEach(System.out::println);
					this.log.info("Trainer " + user.getUsername() + " viewed their workout classes");
				}
				case "2" -> addWorkoutClass(user);
				case "3" -> updateWorkoutClass(user);
				case "4" -> deleteWorkoutClass(user);
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void memberMenu(User user) {
		while (true) {
			System.out.println("\n-- Member Menu --");
			System.out.println("1) Browse workout classes");
			System.out.println("2) Purchase membership");
			System.out.println("3) View my total membership spend");
			System.out.println("4) View merchandise");
			System.out.println();
			System.out.println("0) Logout");
			System.out.print("Choose: ");
			String c = this.in.nextLine();
			switch (c) {
				case "1" -> {
					this.workoutClasses.findAll().forEach(System.out::println);
					this.log.info("Member " + user.getUsername() + " viewed all workout classes");
				}
				case "2" -> buyMembership(user);
				case "3" -> {
					System.out.println("You spent: $" + this.memberships.totalSpentByMember(user.getId()));
					this.log.info("Member " + user.getUsername() + " viewed their total membership spend");
				}
				case "4" -> {
					this.merch.list().forEach(System.out::println);
					this.log.info("Member " + user.getUsername() + " viewed merchandise list");
				}
				case "0" -> {
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void usersList() {
		System.out.println("\n-- Users List --");
		List<User> usersList = this.users.findAll();
		if (usersList.isEmpty()) {
			System.out.println("No users found.");
			return;
		}
		System.out.println("ID | Username | Role | Email | Phone | Address");
		for (User u : usersList) {
			System.out.printf("%d | %s | %s | %s | %s | %s%n",
					u.getId(), u.getUsername(), u.getRole(),
					u.getEmail(), u.getPhone(), u.getAddress());
		}
		System.out.println("Total users: " + usersList.size());
	}

	private void buyMembership(User user) {
		System.out.print("Membership type: ");
		String type = this.in.nextLine();
		System.out.print("Cost: ");
		BigDecimal cost = null;
		while (cost == null) {
			try {
				cost = new BigDecimal(this.in.nextLine());
			} catch (NumberFormatException e) {
				System.out.print("Invalid cost. Please enter a valid decimal value: ");
			}
		}
		System.out.print("Description: ");
		String desc = this.in.nextLine();
		if (this.memberships.purchase(user.getId(), type, cost, desc)) {
			System.out.println("Purchased!");
			this.log.info("User "+user.getUsername()+" (#" + user.getId() + ") purchased membership " + type + " $" + cost);
		} else {
			System.out.println("Failed to purchase.");
		}
	}

	private void addMerch(User user) {
		System.out.print("Name: ");
		String name = this.in.nextLine();
		System.out.print("Type: ");
		String type = this.in.nextLine();
		System.out.print("Price: ");
		BigDecimal price = null;
		while (price == null) {
			try {
				price = new BigDecimal(this.in.nextLine());
			} catch (NumberFormatException e) {
				System.out.print("Invalid price. Please enter a valid decimal value: ");
			}
		}
		System.out.print("Qty: ");
		Integer qty = null;
		while (qty == null) {
			try {
				qty = Integer.parseInt(this.in.nextLine());
				if (qty < 0) {
					System.out.print("Quantity cannot be negative. Please enter a valid quantity: ");
				}
			} catch (NumberFormatException e) {
				System.out.print("Invalid quantity. Please enter a valid integer: ");
			}
		}

		var item = new MerchandiseDAO.Item(0, name, type, price, qty);
		this.merch.add(item);
		System.out.println("Added.");
		this.log.info("Admin "+user.getUsername()+" (#" + user.getId() + ") added merchandise item: " + item);
	}

	private void updateMerch(User user) {
		System.out.print("Enter the ID of the merchandise item to update: ");
		int itemId;
		try {
			itemId = Integer.parseInt(this.in.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid item ID.");
			return;
		}

		var existingItem = this.merch.get(itemId);
		if (existingItem == null) {
			System.out.println("Item not found.");
			return;
		}

		System.out.println("Current item: " + existingItem);

		Map<String, String> updates = new HashMap<>();

		while (true) {
			System.out.println("1) Update name (current: " + existingItem.getName() + ")");
			System.out.println("2) Update type (current: " + existingItem.getType() + ")");
			System.out.println("3) Update price (current: $" + existingItem.getPrice() + ")");
			System.out.println("4) Update quantity (current: " + existingItem.getQuantity() + ")");
			System.out.println();
			System.out.println("0) Finish updates");
			System.out.println("q) Discard changes and exit");

			System.out.print("Choose: ");

			String choice = this.in.nextLine();
			switch (choice) {
				case "1" -> {
					System.out.print("New name: ");
					String newName = this.in.nextLine();
					updates.put("name", newName);
				}
				case "2" -> {
					System.out.print("New type: ");
					String newType = this.in.nextLine();
					updates.put("type", newType);
				}
				case "3" -> {
					System.out.print("New price: ");
					String newPrice = this.in.nextLine();
					try {
						BigDecimal bigDecimal = new BigDecimal(newPrice);
						updates.put("price", bigDecimal.toString());
					} catch (NumberFormatException e) {
						System.out.println("Invalid price. Change not recorded.");
					}
				}
				case "4" -> {
					System.out.print("New quantity: ");
					String newQty = this.in.nextLine();
					try {
						int q = Integer.parseInt(newQty);
						if (q < 0)
							throw new NumberFormatException();
						updates.put("quantity", newQty);
					} catch (NumberFormatException e) {
						System.out.println("Invalid quantity. Change not recorded.");
					}
				}
				case "q" -> {
					System.out.println("Changes discarded.");
					return;
				}
				case "0" -> {
					var updatedItem = new MerchandiseDAO.Item(
							existingItem.getId(),
							updates.getOrDefault("name", existingItem.getName()),
							updates.getOrDefault("type", existingItem.getType()),
							updates.containsKey("price") ? new BigDecimal(updates.get("price"))
									: existingItem.getPrice(),
							updates.containsKey("quantity") ? Integer.parseInt(updates.get("quantity"))
									: existingItem.getQuantity());

					if (this.merch.update(updatedItem) != null) {
						System.out.println("Item updated: " + updatedItem);
						this.log.info("Admin " + user.getUsername() + " (#" + user.getId() + ") updated merchandise item: " + updatedItem);
					} else {
						System.out.println("Failed to update item.");
					}
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}
	}

	private void addWorkoutClass(User user) {
		System.out.print("Class type: ");
		String type = this.in.nextLine();
		System.out.print("Description: ");
		String desc = this.in.nextLine();

		var c = new WorkoutClass(0, type, desc, user.getId());
		var added = this.workoutClasses.addClass(c); // <- use service method

		if (added != null) {
			System.out.println("Class added: " + added);
			this.log.info("Trainer "+user.getUsername()+" (#" + user.getId() + ") added class: " + added);
		} else {
			System.out.println("Failed to add class.");
		}
	}

	private void updateWorkoutClass(User user) {
		System.out.print("Enter the ID of the class to update: ");
		int classId;
		try {
			classId = Integer.parseInt(this.in.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid class ID.");
			return;
		}

		var classes = this.workoutClasses.findByTrainer(user.getId());
		var classOpt = classes.stream().filter(c -> c.getId() == classId).findFirst();
		if (classOpt.isEmpty()) {
			System.out.println("Class not found or you do not have permission to update it.");
			return;
		}
		var existingClass = classOpt.get();

		System.out.println("Current class: " + existingClass);

		Map<String, String> updates = new HashMap<>();

		while (true) {
			System.out.println("1) Update type (current: " + existingClass.getType() + ")");
			System.out.println("2) Update description (current: " + existingClass.getDescription() + ")");
			System.out.println();
			System.out.println("0) Finish updates");
			System.out.println("q) Discard changes and exit");

			System.out.print("Choose: ");

			String choice = this.in.nextLine();
			switch (choice) {
				case "1" -> {
					System.out.print("New type: ");
					String newType = this.in.nextLine();
					updates.put("type", newType);
				}
				case "2" -> {
					System.out.print("New description: ");
					String newDesc = this.in.nextLine();
					updates.put("description", newDesc);
				}
				case "q" -> {
					System.out.println("Changes discarded.");
					return;
				}
				case "0" -> {
					var updatedClass = new WorkoutClass(existingClass.getId(),
							updates.getOrDefault("type", existingClass.getType()),
							updates.getOrDefault("description", existingClass.getDescription()),
							existingClass.getTrainerId());

					if (this.workoutClasses.updateClass(updatedClass) != null) {
						System.out.println("Class updated: " + updatedClass);
						this.log.info("Trainer " + user.getUsername() + " (#" + user.getId() + ") updated class: " + updatedClass);
					} else {
						System.out.println("Failed to update class.");
					}
					return;
				}
				default -> System.out.println("Invalid choice.");
			}
		}

	}

	private void deleteWorkoutClass(User user) {
		System.out.print("Enter the ID of the class to delete: ");
		int classId;
		try {
			classId = Integer.parseInt(this.in.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid class ID.");
			return;
		}

		var classes = this.workoutClasses.findByTrainer(user.getId());
		var classOpt = classes.stream().filter(c -> c.getId() == classId).findFirst();
		if (classOpt.isEmpty()) {
			System.out.println("Class not found or you do not have permission to delete it.");
			return;
		}

		if (this.workoutClasses.removeClass(classId) != null) {
			System.out.println("Class deleted.");
			this.log.info("Trainer " + user.getUsername() + " (#" + user.getId() + ") deleted class with ID: " + classId);
		} else {
			System.out.println("Failed to delete class.");
		}
	}

	// Added: full delete-user flow for Admin (menu option 6)
	private void deleteUserFlow(User actingAdmin){
		usersList();
		System.out.print("Enter user ID to delete: ");
		String raw = this.in.nextLine().trim();
		int targetId;
		try { targetId = Integer.parseInt(raw); }
		catch (NumberFormatException e){ System.out.println("Invalid ID."); return; }

		if (targetId == actingAdmin.getId()){
			System.out.println("You can’t delete yourself while logged in.");
			return;
		}

		// Prevent deleting the last remaining admin
		var all = this.users.findAll();
		var targetOpt = all.stream().filter(u -> u.getId() == targetId).findFirst();
		if (targetOpt.isEmpty()){ System.out.println("User not found."); return; }
		var target = targetOpt.get();
		long adminCount = all.stream().filter(u -> u.getRole() == Role.ADMIN).count();
		if (target.getRole() == Role.ADMIN && adminCount <= 1){
			System.out.println("Cannot delete the last remaining ADMIN.");
			return;
		}

		System.out.print("Type YES to confirm delete: ");
		if (!"YES".equalsIgnoreCase(this.in.nextLine().trim())){
			System.out.println("Canceled.");
			return;
		}

		boolean ok = this.users.deleteUser(targetId);
		System.out.println(ok ? "Deleted." : "Delete failed.");
		if (ok) {
			this.log.info("Admin " + actingAdmin.getUsername() + " (#" + actingAdmin.getId() + ") deleted user #" + targetId);
		} else {
			this.log.warning("Admin " + actingAdmin.getUsername() + " failed to delete user #" + targetId);
		}
	}
}
