package com.example.demo.util;

import com.example.demo.tm.User;

public class SessionManager {

    private SessionManager() {
    }

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearSession() {
        currentUser = null;
    }
}
