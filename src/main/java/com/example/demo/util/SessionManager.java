package com.example.demo.util;

import lombok.Getter;
import lombok.Setter;
import com.example.demo.tm.User;

public class SessionManager {

    private SessionManager() {
    }

    @Getter
    @Setter
    private static User currentUser;

    public static void clearSession() {
        currentUser = null;
    }
}
