package com.example.demo.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9 -]{7,15}$");

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return !isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isPositiveNumber(String num) {
        try {
            double val = Double.parseDouble(num);
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static BigDecimal parseCurrency(String amount) {
        try {
            return new BigDecimal(amount);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
