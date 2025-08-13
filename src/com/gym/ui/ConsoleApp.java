package com.gym.ui;

import com.gym.dao.*;
import com.gym.dao.mock.*;
import com.gym.entities.*;
import com.gym.services.*;
import com.gym.util.Log;
import com.gym.dao.mock.InMemoryUserDAO;
import com.gym.dao.mock.InMemoryMembershipDAO;
import com.gym.dao.mock.InMemoryMerchandiseDAO;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

public class ConsoleApp {
    private final Scanner in = new Scanner(System.in);
    private final Logger log = Log.get();

    
private final UserService users =
    new UserService(new InMemoryUserDAO());
private final MembershipService memberships =
    new MembershipService(new InMemoryMembershipDAO());
private final MerchandiseService merch =
    new MerchandiseService(new InMemoryMerchandiseDAO());

    public void start(){
        while (true) {
            System.out.println("\n=== Gym System ===");
            System.out.println("1) Register");
            System.out.println("2) Login");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> registerFlow();
                case "2" -> loginFlow();
                case "0" -> { System.out.println("Bye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void registerFlow(){
        System.out.println("\n-- Register --");
        System.out.print("Username: "); String u = in.nextLine();
        System.out.print("Password: "); String pw = in.nextLine();
        System.out.print("Email: "); String e = in.nextLine();
        System.out.print("Phone: "); String p = in.nextLine();
        System.out.print("Address: "); String a = in.nextLine();
        Role role = readRole();
        var created = users.register(u, pw, e, p, a, role);
        System.out.println("Registered as " + created.getRole() + " (id " + created.getId() + ")");
    }

    private void loginFlow(){
        System.out.println("\n-- Login --");
        System.out.print("Username: "); String u = in.nextLine();
        System.out.print("Password: "); String pw = in.nextLine();
        var userOpt = users.login(u, pw);
        if (userOpt.isEmpty()){
            System.out.println("Invalid credentials.");
            return;
        }
        var user = userOpt.get();
        switch (user.getRole()){
            case ADMIN -> adminMenu(user);
            case TRAINER -> trainerMenu(user);
            case MEMBER -> memberMenu(user);
        }
    }

    private Role readRole(){
        while (true){
            System.out.print("Role (ADMIN/TRAINER/MEMBER): ");
            String r = in.nextLine().trim().toUpperCase();
            try { return Role.valueOf(r); }
            catch (Exception e){ System.out.println("Try again."); }
        }
    }

    private void adminMenu(User user){
        while (true){
            System.out.println("\n-- Admin Menu --");
            System.out.println("1) View all users");
            System.out.println("2) View membership revenue");
            System.out.println("3) Add merchandise item");
            System.out.println("4) View stock + total value");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = in.nextLine();
            switch (c){
                case "1" -> { usersList(); log.info("Admin listed users"); }
                case "2" -> System.out.println("Total revenue: $" + memberships.totalRevenue());
                case "3" -> addMerch();
                case "4" -> { merch.list().forEach(System.out::println);
                              System.out.println("Stock value: $" + merch.stockValue()); }
                case "0" -> { return; }
                default -> System.out.println("Nope.");
            }
        }
    }

    private void trainerMenu(User user){
        while (true){
            System.out.println("\n-- Trainer Menu --");
            System.out.println("1) Purchase membership");
            System.out.println("2) View merchandise");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = in.nextLine();
            switch (c){
                case "1" -> buyMembership(user.getId());
                case "2" -> merch.list().forEach(System.out::println);
                case "0" -> { return; }
                default -> System.out.println("Nope.");
            }
        }
    }

    private void memberMenu(User user){
        while (true){
            System.out.println("\n-- Member Menu --");
            System.out.println("1) Browse workout classes (stub)");
            System.out.println("2) Purchase membership");
            System.out.println("3) View my total membership spend");
            System.out.println("4) View merchandise");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            String c = in.nextLine();
            switch (c){
                case "1" -> System.out.println("(Classes view coming from DB layer later)");
                case "2" -> buyMembership(user.getId());
                case "3" -> System.out.println("You spent: $" + memberships.totalSpentByMember(user.getId()));
                case "4" -> merch.list().forEach(System.out::println);
                case "0" -> { return; }
                default -> System.out.println("Nope.");
            }
        }
    }

    private void usersList(){
        System.out.println("(TODO: expose list from service/DAO for Admin)");
    }

    private void buyMembership(int userId){
        System.out.print("Membership type: ");
        String type = in.nextLine();
        System.out.print("Cost: ");
        BigDecimal cost = new BigDecimal(in.nextLine());
        System.out.print("Description: ");
        String desc = in.nextLine();
        if (memberships.purchase(userId, type, cost, desc)) {
            System.out.println("Purchased!");
            Log.get().info("User " + userId + " purchased membership " + type + " $" + cost);
        } else {
            System.out.println("Failed to purchase.");
        }
    }

    private void addMerch(){
        System.out.print("Name: "); String name = in.nextLine();
        System.out.print("Type: "); String type = in.nextLine();
        System.out.print("Price: "); var price = new BigDecimal(in.nextLine());
        System.out.print("Qty: "); int qty = Integer.parseInt(in.nextLine());
        var item = new MerchandiseDAO.Item(0, name, type, price, qty);
        merch.add(item);
        System.out.println("Added.");
    }
}