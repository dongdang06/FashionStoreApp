package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.model.TaiKhoan;

public class TaiKhoanDAO {
    public TaiKhoan login(String username, String password) {
        if (DBConnection.getInstance().isMockMode()) {
            return getMockAccount(username, password);
        }
        String sql = "SELECT * FROM TAIKHOAN WHERE UserName = ? AND PassWord = ? AND TrangThai = 'Hoat dong'";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getString("MaTaiKhoan"),
                            rs.getString("MaNV"),
                            rs.getString("UserName"),
                            rs.getString("PassWord"),
                            rs.getDate("NgayTao"),
                            rs.getString("TrangThai"),
                            rs.getString("VaiTro")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DBConnection.getInstance().isMockMode()) {
                return getMockAccount(username, password);
            }
        }
        return null;
    }

    private TaiKhoan getMockAccount(String username, String password) {
        if ("quanly".equalsIgnoreCase(username) && "123".equals(password)) {
            return new TaiKhoan("TK-01", "NV-01", "quanly", "123", new java.util.Date(), "Hoat dong", "Quan ly");
        }
        if ("banhang".equalsIgnoreCase(username) && "123".equals(password)) {
            return new TaiKhoan("TK-02", "NV-02", "banhang", "123", new java.util.Date(), "Hoat dong", "Nhan vien ban hang");
        }
        if ("kho".equalsIgnoreCase(username) && "123".equals(password)) {
            return new TaiKhoan("TK-03", "NV-03", "kho", "123", new java.util.Date(), "Hoat dong", "Nhan vien kho");
        }
        if ("ketoan".equalsIgnoreCase(username) && "123".equals(password)) {
            return new TaiKhoan("TK-04", "NV-04", "ketoan", "123", new java.util.Date(), "Hoat dong", "Nhan vien ke toan");
        }
        return null;
    }
}
