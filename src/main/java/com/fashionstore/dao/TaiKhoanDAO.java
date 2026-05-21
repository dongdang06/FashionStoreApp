package com.fashionstore.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.model.TaiKhoan;

import oracle.jdbc.OracleTypes;

public class TaiKhoanDAO {
    /**
     * Đăng nhập sử dụng Stored Procedure PROC_DangNhap.
     * Proc trả về SYS_REFCURSOR chứa thông tin tài khoản và trạng thái đăng nhập.
     */
    public TaiKhoan login(String username, String password) {
        String call = "{ CALL PROC_DangNhap(?, ?, ?, ?) }";
        try (Connection conn = DBConnection.getInstance().getConnection();
                CallableStatement stmt = conn.prepareCall(call)) {
            // IN parameters
            stmt.setString(1, username);
            stmt.setString(2, password);
            // OUT parameters
            stmt.registerOutParameter(3, OracleTypes.CURSOR);  // p_Cursor
            stmt.registerOutParameter(4, java.sql.Types.VARCHAR); // p_Status

            stmt.execute();

            String status = stmt.getString(4);

            // Chỉ trả về TaiKhoan khi status là SUCCESS hoặc BLOCKED
            // (BLOCKED vẫn cần trả về để DangNhapFrame hiển thị thông báo "tài khoản bị khóa")
            if ("SUCCESS".equals(status) || "BLOCKED".equals(status)) {
                try (ResultSet rs = (ResultSet) stmt.getObject(3)) {
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
            }
            // WRONG_PASS, NOT_FOUND → trả về null
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        String sql = "UPDATE TAIKHOAN SET PassWord = ? WHERE UserName = ? AND PassWord = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.setString(3, currentPassword);
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
