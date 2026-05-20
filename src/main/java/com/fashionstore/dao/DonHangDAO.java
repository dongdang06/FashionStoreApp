package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.ChiTietDonHang;
import com.fashionstore.model.DonHang;
import com.fashionstore.model.DonHangSummary;

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
    public boolean saveDonHang(DonHang dh, List<ChiTietDonHang> chiTietList, String phuongThucTT) {
        String maDH = com.fashionstore.util.MaGenerator.nextMaDH();
        dh.setMaDH(maDH);
        String sqlDH = "INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV) "
                + "VALUES (?, SYSDATE, 0, ?, ?, ?, ?, ?)";
        String sqlCT = "INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Insert DONHANG (TongTienDH = 0, trigger sẽ tính lại)
            try (PreparedStatement stmtDH = conn.prepareStatement(sqlDH)) {
                stmtDH.setString(1, maDH);
                if (dh.getMaKH() != null) {
                    stmtDH.setString(2, dh.getMaKH());
                } else {
                    stmtDH.setNull(2, java.sql.Types.VARCHAR);
                }
                stmtDH.setString(3, dh.getMaKM());
                stmtDH.setInt(4, dh.getDiemSuDung());
                stmtDH.setInt(5, dh.getDiemNhanDuoc());
                stmtDH.setString(6, dh.getMaNV());
                stmtDH.executeUpdate();
            }

            // 2. Insert từng CHITIETDONHANG (trigger sẽ kiểm tra tồn kho + trừ tồn)
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

            // 3. Tự động tạo HOADON (để đơn hàng được đánh dấu là "Da thanh toan")
            String sqlHD = "INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV) "
                    + "VALUES (?, ?, SYSDATE, ?, ?, ?, ?)";
            String maHD = com.fashionstore.util.MaGenerator.nextMaHD();
            long tongTien = chiTietList.stream().mapToLong(ct -> ct.getSoLuong() * ct.getGiaBanLucMua()).sum();
            // Trừ giảm giá từ điểm tích luỹ (10 điểm = 1,000 VND → 1 điểm = 100 VND)
            long giamGiaDiem = (long) dh.getDiemSuDung() * 100;
            long tongTienHD = Math.max(0, tongTien - giamGiaDiem);
            try (PreparedStatement stmtHD = conn.prepareStatement(sqlHD)) {
                stmtHD.setString(1, maHD);
                stmtHD.setString(2, maDH);
                stmtHD.setLong(3, tongTienHD);
                stmtHD.setString(4, phuongThucTT != null ? phuongThucTT : "Tien mat");
                stmtHD.setNull(5, java.sql.Types.VARCHAR);
                stmtHD.setString(6, dh.getMaNV() != null ? dh.getMaNV() : "NV001");
                stmtHD.executeUpdate();
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
    // Các method đọc dữ liệu
    // -------------------------------------------------------
    public int countOrdersToday() {
        String sql = "SELECT COUNT(*) FROM DONHANG WHERE TRUNC(NgayMua) = TRUNC(SYSDATE)";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<DonHangSummary> getRecentOrders(int limit) {
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
        }
        return results;
    }
}
