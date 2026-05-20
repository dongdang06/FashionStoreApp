package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.ChiTietDonHang;
import com.fashionstore.model.DonHang;
import com.fashionstore.model.DonHangSummary;
import com.fashionstore.util.MockData;

public class DonHangDAO {

    // -------------------------------------------------------
    // Tạo đơn hàng + chi tiết trong một transaction
    // -------------------------------------------------------
    /**
     * Lưu đơn hàng và toàn bộ chi tiết vào DB.
     * Triggers sẽ tự động:
     *   - Kiểm tra tồn kho (trg_KiemTraTonKho_TruocKhiBan)
     *   - Trừ tồn kho       (trg_CapNhatSoLuongTon_KhiBanHang)
     *   - Tính TongTienDH   (trg_TinhTongTienDonHang)
     *   - Cập nhật điểm KH  (trg_CapNhatDiemTichLuy_KhiDatHang)
     * @return true nếu thành công
     */
    public boolean saveDonHang(DonHang dh, List<ChiTietDonHang> chiTietList) {
        if (DBConnection.getInstance().isMockMode()) {
            return true; // mock: luôn thành công
        }
        String sqlDH = "INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV) "
                + "VALUES (seq_DonHang.NEXTVAL, SYSDATE, 0, ?, ?, ?, ?, ?)";
        String sqlGetMaDH = "SELECT seq_DonHang.CURRVAL FROM DUAL";
        String sqlCT = "INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Insert DONHANG (TongTienDH = 0, trigger sẽ tính lại)
            try (PreparedStatement stmtDH = conn.prepareStatement(sqlDH)) {
                stmtDH.setString(1, dh.getMaKH());
                stmtDH.setString(2, dh.getMaKM());
                stmtDH.setInt(3, dh.getDiemSuDung());
                stmtDH.setInt(4, dh.getDiemNhanDuoc());
                stmtDH.setString(5, dh.getMaNV());
                stmtDH.executeUpdate();
            }

            // 2. Lấy MaDH vừa được tạo bởi sequence
            String maDH;
            try (PreparedStatement stmtSeq = conn.prepareStatement(sqlGetMaDH);
                 ResultSet rs = stmtSeq.executeQuery()) {
                rs.next();
                maDH = rs.getString(1);
                dh.setMaDH(maDH);
            }

            // 3. Insert từng CHITIETDONHANG (trigger sẽ kiểm tra tồn kho + trừ tồn)
            try (PreparedStatement stmtCT = conn.prepareStatement(sqlCT)) {
                for (ChiTietDonHang ct : chiTietList) {
                    stmtCT.setString(1, maDH);
                    stmtCT.setString(2, ct.getMaBienThe());
                    stmtCT.setInt(3, ct.getSoLuong());
                    stmtCT.setLong(4, ct.getGiaBanLucMua());
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

    // -------------------------------------------------------
    // Các method đọc dữ liệu (giữ nguyên)
    // -------------------------------------------------------
    public int countOrdersToday() {
        if (DBConnection.getInstance().isMockMode()) {
            return MockData.countOrdersToday();
        }
        String sql = "SELECT COUNT(*) FROM DONHANG WHERE TRUNC(NgayMua) = TRUNC(SYSDATE)";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DBConnection.getInstance().isMockMode()) {
                return MockData.countOrdersToday();
            }
        }
        return 0;
    }

    public List<DonHangSummary> getRecentOrders(int limit) {
        if (DBConnection.getInstance().isMockMode()) {
            return MockData.getRecentOrders(limit);
        }
        String sql = "SELECT dh.MaDH, nv.HoTen, dh.TongTienDH, "
                + "CASE WHEN hd.MaHD IS NULL THEN N'Cho thanh toan' "
                + "ELSE N'Da thanh toan' END AS TrangThai "
                + "FROM DONHANG dh "
                + "LEFT JOIN HOADON hd ON hd.MaDH = dh.MaDH "
                + "LEFT JOIN NHANVIEN nv ON nv.MaNV = dh.MaNV "
                + "ORDER BY dh.NgayMua DESC "
                + "FETCH FIRST ? ROWS ONLY";
        List<DonHangSummary> results = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new DonHangSummary(
                            rs.getString("MaDH"),
                            rs.getString("HoTen"),
                            rs.getLong("TongTienDH"),
                            rs.getString("TrangThai")));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (DBConnection.getInstance().isMockMode()) {
                return MockData.getRecentOrders(limit);
            }
        }
        return results;
    }
}
