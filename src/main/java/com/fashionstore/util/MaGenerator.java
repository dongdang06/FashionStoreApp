package com.fashionstore.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.dao.DBConnection;

/**
 * Tiện ích tự động sinh mã cho các bảng trong hệ thống.
 * Quy tắc: Prefix + số thứ tự (padding zero).
 * Ví dụ: NV001, NCC01, SP001, DM01, KM001, BT001, HD001, PN001, PT001
 */
public class MaGenerator {

    /**
     * Sinh mã tiếp theo dựa trên prefix, bảng, cột và số chữ số.
     * @param tableName  Tên bảng (VD: "NHANVIEN")
     * @param columnName Tên cột mã (VD: "MaNV")
     * @param prefix     Tiền tố (VD: "NV")
     * @param digits     Số chữ số (VD: 3 -> 001, 002)
     * @return Mã mới (VD: "NV006")
     */
    public static String generateNextMa(String tableName, String columnName, String prefix, int digits) {
        if (DBConnection.getInstance().isMockMode()) {
            return generateNextMaFromList(tableName, columnName, prefix, digits);
        }

        String sql = "SELECT " + columnName + " FROM " + tableName;
        int maxId = 0;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String idStr = rs.getString(1);
                if (idStr != null && idStr.startsWith(prefix)) {
                    try {
                        int num = Integer.parseInt(idStr.substring(prefix.length()));
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return String.format("%s%0" + digits + "d", prefix, maxId + 1);
    }

    /**
     * Fallback cho mock mode: sinh mã dựa trên thời gian.
     */
    private static String generateNextMaFromList(String tableName, String columnName, String prefix, int digits) {
        int randomSuffix = (int) (System.currentTimeMillis() % Math.pow(10, digits));
        return String.format("%s%0" + digits + "d", prefix, randomSuffix);
    }

    // --- Các phương thức tiện ích cho từng bảng ---

    public static String nextMaNV() {
        return generateNextMa("NHANVIEN", "MaNV", "NV", 3);
    }

    public static String nextMaNCC() {
        return generateNextMa("NHACUNGCAP", "MaNCC", "NCC", 2);
    }

    public static String nextMaSP() {
        return generateNextMa("SANPHAM", "MaSP", "SP", 3);
    }

    public static String nextMaDM() {
        return generateNextMa("DANHMUC", "MaDM", "DM", 2);
    }

    public static String nextMaKM() {
        return generateNextMa("KHUYENMAI", "MaKM", "KM", 3);
    }

    public static String nextMaBienThe() {
        return generateNextMa("BIENTHESANPHAM", "MaBienThe", "BT", 3);
    }

    public static String nextMaHD() {
        return generateNextMa("HOADON", "MaHD", "HD", 3);
    }

    public static String nextMaPN() {
        return generateNextMa("PHIEUNHAP", "MaPN", "PN", 3);
    }

    public static String nextMaPhieuTra() {
        return generateNextMa("PHIEUXUATTRA", "MaPhieuTra", "PT", 3);
    }
}
