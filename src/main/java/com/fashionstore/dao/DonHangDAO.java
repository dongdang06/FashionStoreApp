package com.fashionstore.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
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
        // Tránh Deadlock bằng cách sắp xếp chi tiết đơn hàng theo MaBienThe tăng dần
        if (chiTietList != null) {
            chiTietList.sort((ct1, ct2) -> ct1.getMaBienThe().compareTo(ct2.getMaBienThe()));
        }

        String maDH = com.fashionstore.util.MaGenerator.nextMaDH();
        dh.setMaDH(maDH);

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Khóa dòng dữ liệu Khách hàng và kiểm tra điểm tích luỹ thực tế
            if (dh.getMaKH() != null && dh.getDiemSuDung() > 0) {
                String sqlLockKH = "SELECT DiemTichLuy FROM KHACHHANG WHERE MaKH = ? FOR UPDATE";
                try (PreparedStatement stmtLockKH = conn.prepareStatement(sqlLockKH)) {
                    stmtLockKH.setString(1, dh.getMaKH());
                    try (ResultSet rs = stmtLockKH.executeQuery()) {
                        if (rs.next()) {
                            int diemHienCo = rs.getInt("DiemTichLuy");
                            if (dh.getDiemSuDung() > diemHienCo) {
                                throw new java.sql.SQLException("Khách hàng không đủ điểm tích lũy! (Hiện có: " 
                                        + diemHienCo + " điểm, yêu cầu dùng: " + dh.getDiemSuDung() + " điểm)");
                            }
                        } else {
                            throw new java.sql.SQLException("Không tìm thấy khách hàng với mã: " + dh.getMaKH());
                        }
                    }
                }
            }

            // 2. Khóa dòng dữ liệu Biến thể Sản phẩm và kiểm tra số lượng tồn kho trước khi bán
            if (chiTietList != null) {
                String sqlLockBT = "SELECT SoLuongTon FROM BIENTHESANPHAM WHERE MaBienThe = ? FOR UPDATE";
                try (PreparedStatement stmtLockBT = conn.prepareStatement(sqlLockBT)) {
                    for (ChiTietDonHang ct : chiTietList) {
                        stmtLockBT.setString(1, ct.getMaBienThe());
                        try (ResultSet rs = stmtLockBT.executeQuery()) {
                            if (rs.next()) {
                                int tonKho = rs.getInt("SoLuongTon");
                                if (ct.getSoLuong() > tonKho) {
                                    throw new java.sql.SQLException("Sản phẩm mã " + ct.getMaBienThe() 
                                            + " không đủ số lượng tồn kho! (Tồn thực tế: " + tonKho + ", yêu cầu bán: " + ct.getSoLuong() + ")");
                                }
                            } else {
                                throw new java.sql.SQLException("Không tìm thấy sản phẩm biến thể: " + ct.getMaBienThe());
                            }
                        }
                    }
                }
            }

            // 3. Gọi PROC_KhoiTaoDonHang
            String callHeader = "{ CALL PROC_KhoiTaoDonHang(?, ?, ?, ?, ?) }";
            try (CallableStatement stmt = conn.prepareCall(callHeader)) {
                stmt.setString(1, maDH);
                stmt.setString(2, dh.getMaKH());
                stmt.setString(3, dh.getMaNV());
                stmt.setString(4, dh.getMaKM());
                stmt.registerOutParameter(5, Types.VARCHAR);
                stmt.execute();
                String res = stmt.getString(5);
                if ("ERR_DUP_MADH".equals(res)) {
                    throw new java.sql.SQLException("Mã đơn hàng đã tồn tại.");
                } else if (!"SUCCESS".equals(res)) {
                    throw new java.sql.SQLException("Lỗi khởi tạo đơn hàng: " + res);
                }
            }

            // 4. Gọi PROC_Them_CTDH cho từng chi tiết
            String callDetail = "{ CALL PROC_Them_CTDH(?, ?, ?, ?) }";
            try (CallableStatement stmt = conn.prepareCall(callDetail)) {
                for (ChiTietDonHang ct : chiTietList) {
                    stmt.setString(1, maDH);
                    stmt.setString(2, ct.getMaBienThe());
                    stmt.setInt(3, ct.getSoLuong());
                    stmt.registerOutParameter(4, Types.VARCHAR);
                    stmt.execute();
                    String res = stmt.getString(4);
                    if (res != null && res.startsWith("LỖI:")) {
                        throw new java.sql.SQLException("Lỗi thêm chi tiết đơn hàng: " + res);
                    } else if (!"SUCCESS".equals(res)) {
                        throw new java.sql.SQLException("Lỗi thêm chi tiết đơn hàng: " + res);
                    }
                }
            }

            // 5. Gọi PROC_XacNhanThanhToan
            String callPay = "{ CALL PROC_XacNhanThanhToan(?, ?, ?, ?, ?, ?, ?) }";
            String maHD = com.fashionstore.util.MaGenerator.nextMaHD();
            try (CallableStatement stmt = conn.prepareCall(callPay)) {
                stmt.setString(1, maHD);
                stmt.setString(2, maDH);
                stmt.setInt(3, dh.getDiemSuDung());
                stmt.setString(4, phuongThucTT != null ? phuongThucTT : "Tien mat");
                stmt.setNull(5, Types.VARCHAR); // Ghi chú
                stmt.setString(6, dh.getMaNV() != null ? dh.getMaNV() : "NV001");
                stmt.registerOutParameter(7, Types.VARCHAR);
                stmt.execute();
                String res = stmt.getString(7);
                if ("ERR_DUP_MAHD".equals(res)) {
                    throw new java.sql.SQLException("Mã hóa đơn đã tồn tại.");
                } else if (!"SUCCESS".equals(res)) {
                    throw new java.sql.SQLException("Lỗi xác nhận thanh toán: " + res);
                }
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
        String sql = "SELECT dh.MaDH, dh.NgayMua, nv.HoTen, (dh.TongTienDH - NVL(dh.DiemSuDung, 0) * 100) AS TongTienDH, "
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
                            rs.getTimestamp("NgayMua"),
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

    public DonHang getById(String maDH) {
        String sql = "SELECT MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV FROM DONHANG WHERE MaDH = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DonHang(
                        rs.getString("MaDH"),
                        rs.getTimestamp("NgayMua"),
                        rs.getLong("TongTienDH"),
                        rs.getString("MaKH"),
                        rs.getString("MaKM"),
                        rs.getInt("DiemSuDung"),
                        rs.getInt("DiemNhanDuoc"),
                        rs.getString("MaNV")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ChiTietDonHang> getDetails(String maDH) {
        String sql = "SELECT MaDH, MaBienThe, SoLuong, GiaBanLucMua FROM CHITIETDONHANG WHERE MaDH = ?";
        List<ChiTietDonHang> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietDonHang(
                        rs.getString("MaDH"),
                        rs.getString("MaBienThe"),
                        rs.getInt("SoLuong"),
                        rs.getLong("GiaBanLucMua")
                    ));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getDetailsForDisplay(String maDH) {
        String sql = "SELECT ct.MaBienThe, sp.TenSP, bt.MauSac, bt.KichThuoc, ct.SoLuong, ct.GiaBanLucMua, bt.GiaBan " +
                     "FROM CHITIETDONHANG ct " +
                     "JOIN BIENTHESANPHAM bt ON ct.MaBienThe = bt.MaBienThe " +
                     "JOIN SANPHAM sp ON bt.MaSP = sp.MaSP " +
                     "WHERE ct.MaDH = ?";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDH);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                        rs.getString("MaBienThe"),
                        rs.getString("TenSP"),
                        rs.getString("MauSac"),
                        rs.getString("KichThuoc"),
                        rs.getInt("SoLuong"),
                        rs.getLong("GiaBanLucMua"),
                        rs.getLong("GiaBan")
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String getPhuongThucTT(String maDH) {
        String sql = "SELECT PhuongThucTT FROM HOADON WHERE MaDH = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String pt = rs.getString("PhuongThucTT");
                    if ("Tien mat".equalsIgnoreCase(pt)) return "Tiền mặt";
                    if ("Chuyen khoan".equalsIgnoreCase(pt)) return "Chuyển khoản";
                    return pt;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Chưa thanh toán";
    }
}
