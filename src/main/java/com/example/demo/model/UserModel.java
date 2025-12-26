package com.example.demo.model;

import com.example.demo.dao.UserDAO;
import com.example.demo.exception.UserEmailExsist;
import com.example.demo.exception.UserNotFound;
import com.example.demo.tm.User;

public class UserModel {

    private static final UserDAO userDAO = new UserDAO();
    private static User currentUser;

    public static boolean login(User user) throws UserNotFound {
        User authenticatedUser = userDAO.authenticate(user.getEmail(), user.getPassword());
        if (authenticatedUser != null) {
            currentUser = authenticatedUser;
            return true;
        }
        throw new UserNotFound("Invalid email or password.");
    }

    public static void registerUser(User user) throws UserEmailExsist {
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new UserEmailExsist("Email already exists.");
        }
        userDAO.create(user);
    }

    public static User getUserCurrentUser() {
        return currentUser;
    }
}
