package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {
    public boolean validateLogin(String username, String password) {
        if (DBConnection.getInstance().isMockMode()) {
            return "admin".equalsIgnoreCase(username) && "admin".equals(password);
        }
        String sql = "SELECT 1 FROM TAIKHOAN WHERE UserName = ? AND PassWord = ? AND TrangThai = 'Hoat dong'";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DBConnection.getInstance().isMockMode()) {
                return "admin".equalsIgnoreCase(username) && "admin".equals(password);
            }
        }
        return false;
    }
}
