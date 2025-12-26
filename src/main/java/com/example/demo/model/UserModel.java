package com.example.demo.model;

import com.example.demo.exception.UserEmailExsist;
import com.example.demo.exception.UserNotFound;
import com.example.demo.tm.User;

import java.util.ArrayList;
import java.util.List;

public class UserModel {

    private static List<User> users = new ArrayList<>();
    private static User currentUser;

    public static boolean login(User user) throws UserNotFound {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail()) && u.getPassword().equals(user.getPassword())) {
                currentUser = u;
                return true;
            }
        }
        throw new UserNotFound("Invalid email or password.");
    }

    public static void registerUser(User user) throws UserEmailExsist {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new UserEmailExsist("Email already exists.");
            }
        }
        users.add(user);
    }

    public static User getUserCurrentUser() {
        return currentUser;
    }
}
