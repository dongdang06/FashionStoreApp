package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.PhieuNhapKho;
import com.fashionstore.util.MockData;

public class PhieuNhapKhoDAO {

    /**
     * Lấy tất cả phiếu nhập kho.
     */
    public List<PhieuNhapKho> getAll() {
        if (DBConnection.getInstance().isMockMode()) {
            return MockData.getPhieuNhapList();
        }
        String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP ORDER BY MaPN";
        List<PhieuNhapKho> results = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new PhieuNhapKho(
                        rs.getString("MaPN"),
                        rs.getDate("NgayNhap"),
                        rs.getLong("TongGiaTri"),
                        rs.getString("MaNCC"),
                        rs.getString("MaNV")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DBConnection.getInstance().isMockMode()) {
                return MockData.getPhieuNhapList();
            }
        }
        return results;
    }

    /**
     * Lưu phiếu nhập + chi tiết trong một transaction.
     * Triggers tự động:
     *   - Cộng SoLuongTon    (trg_CapNhatSoLuongTon_KhiNhapHang)
     *   - Tính TongGiaTri    (trg_TinhTongGiaTriPhieuNhap)
     */
    public boolean save(PhieuNhapKho pn, List<ChiTietPhieuNhap> chiTietList) {
        if (DBConnection.getInstance().isMockMode()) {
            return true;
        }
        String sqlPN = "INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV) "
                + "VALUES (seq_PhieuNhap.NEXTVAL, SYSDATE, 0, ?, ?)";
        String sqlGetMaPN = "SELECT seq_PhieuNhap.CURRVAL FROM DUAL";
        String sqlCT = "INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Insert PHIEUNHAP
            try (PreparedStatement stmtPN = conn.prepareStatement(sqlPN)) {
                stmtPN.setString(1, pn.getMaNCC());
                stmtPN.setString(2, pn.getMaNV());
                stmtPN.executeUpdate();
            }

            // 2. Lấy MaPN từ sequence
            String maPN;
            try (PreparedStatement stmtSeq = conn.prepareStatement(sqlGetMaPN);
                 ResultSet rs = stmtSeq.executeQuery()) {
                rs.next();
                maPN = rs.getString(1);
                pn.setMaPN(maPN);
            }

            // 3. Insert từng CHITIETPHIEUNHAP (trigger cộng tồn kho)
            try (PreparedStatement stmtCT = conn.prepareStatement(sqlCT)) {
                for (ChiTietPhieuNhap ct : chiTietList) {
                    stmtCT.setString(1, maPN);
                    stmtCT.setString(2, ct.getMaBienThe());
                    stmtCT.setInt(3, ct.getSoLuongNhap());
                    stmtCT.setLong(4, ct.getGiaNhap());
                    stmtCT.addBatch();
                }
                stmtCT.executeBatch();
            }

            conn.commit();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }
}
