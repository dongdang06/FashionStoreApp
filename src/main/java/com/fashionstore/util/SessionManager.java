package com.fashionstore.util;

import com.fashionstore.model.TaiKhoan;

public class SessionManager {
    private static TaiKhoan currentUser;

    public static TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(TaiKhoan user) {
        currentUser = user;
    }

    public static boolean hasPermission(String... allowedRoles) {
        if (currentUser == null || currentUser.getVaiTro() == null) {
            return false;
        }
        String currentRole = currentUser.getVaiTro();
        // Quản lý (Admin) luôn có quyền
        if ("Quan ly".equalsIgnoreCase(currentRole) || "Admin".equalsIgnoreCase(currentRole)) {
            return true;
        }
        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(currentRole)) {
                return true;
            }
        }
        return false;
    }
}
