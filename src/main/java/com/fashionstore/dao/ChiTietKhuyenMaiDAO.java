package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.ChiTietKhuyenMai;

public class ChiTietKhuyenMaiDAO {

    public List<ChiTietKhuyenMai> getByMaKM(String maKM) {
        List<ChiTietKhuyenMai> list = new ArrayList<>();
        if (DBConnection.getInstance().isMockMode()) {
            return list;
        }
        String sql = "SELECT MaKM, MaBienThe, GiaKhuyenMai FROM CHITIETKHUYENMAI WHERE MaKM = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKM);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietKhuyenMai(
                            rs.getString("MaKM"),
                            rs.getString("MaBienThe"),
                            rs.getLong("GiaKhuyenMai")
                    ));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean save(ChiTietKhuyenMai ct) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String sql = "INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ct.getMaKM());
            stmt.setString(2, ct.getMaBienThe());
            stmt.setLong(3, ct.getGiaKhuyenMai());
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public boolean delete(String maKM, String maBienThe) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String sql = "DELETE FROM CHITIETKHUYENMAI WHERE MaKM = ? AND MaBienThe = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKM);
            stmt.setString(2, maBienThe);
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public boolean deleteByMaKM(String maKM) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String sql = "DELETE FROM CHITIETKHUYENMAI WHERE MaKM = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKM);
            return stmt.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
