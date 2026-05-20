package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.model.TaiKhoan;

public class TaiKhoanDAO {
    public TaiKhoan login(String username, String password) {
        String sql = "SELECT * FROM TAIKHOAN WHERE UserName = ? AND PassWord = ?";
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
        }
        return null;
    }
}
