package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.KhachHang;

public class KhachHangDAO {

    public List<KhachHang> getAllCustomers() {
        List<KhachHang> list = new ArrayList<>();
        if (DBConnection.getInstance().isMockMode()) {
            list.add(new KhachHang("KH001", "Nguyen Van Anh", "0901234567", 150));
            list.add(new KhachHang("KH002", "Tran Thi Binh", "0912345678", 230));
            list.add(new KhachHang("KH003", "Le Van Cuong", "0987654321", 0));
            return list;
        }

        String sql = "SELECT MaKH, HoTen, SDT, DiemTichLuy FROM KHACHHANG ORDER BY MaKH DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new KhachHang(
                        rs.getString("MaKH"),
                        rs.getString("HoTen"),
                        rs.getString("SDT"),
                        rs.getInt("DiemTichLuy")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean saveCustomer(KhachHang kh) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String maKH = com.fashionstore.util.MaGenerator.nextMaKH();
        kh.setMaKH(maKH);

        String sql = "INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            stmt.setString(2, kh.getHoTen());
            stmt.setString(3, kh.getSdt());
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public boolean updateCustomer(KhachHang kh) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String sql = "UPDATE KHACHHANG SET HoTen = ?, SDT = ? WHERE MaKH = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSdt());
            stmt.setString(3, kh.getMaKH());
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public KhachHang getCustomerBySdt(String sdt) {
        if (DBConnection.getInstance().isMockMode()) {
            if ("0901234567".equals(sdt)) return new KhachHang("KH001", "Nguyen Van Anh", "0901234567", 150);
            return null;
        }
        String sql = "SELECT MaKH, HoTen, SDT, DiemTichLuy FROM KHACHHANG WHERE SDT = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                            rs.getString("MaKH"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getInt("DiemTichLuy")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public KhachHang getCustomerByMa(String maKH) {
        if (DBConnection.getInstance().isMockMode()) {
            if ("KH001".equals(maKH)) return new KhachHang("KH001", "Nguyen Van Anh", "0901234567", 150);
            return null;
        }
        String sql = "SELECT MaKH, HoTen, SDT, DiemTichLuy FROM KHACHHANG WHERE MaKH = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                            rs.getString("MaKH"),
                            rs.getString("HoTen"),
                            rs.getString("SDT"),
                            rs.getInt("DiemTichLuy")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
