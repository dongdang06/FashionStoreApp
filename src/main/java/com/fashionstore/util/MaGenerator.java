package com.fashionstore.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.dao.DBConnection;

/**
 * Tien ich sinh ma theo dinh dang: tien to + so thu tu co padding.
 * Vi du: NV001, NCC01, SP001, DM01, KM001, BT001, HD001, PN001, PT001.
 */
public class MaGenerator {

    /**
     * Sinh ma tiep theo bang Oracle sequence.
     * Neu chua cai sequence trong DB, fallback ve cach cu max+1 de app van chay
     * duoc tren database local hien co.
     */
    public static String generateNextMa(String tableName, String columnName, String sequenceName,
            String prefix, int digits) {
        Long sequenceValue = getNextSequenceValue(sequenceName);
        if (sequenceValue != null) {
            return format(prefix, digits, sequenceValue);
        }

        return generateNextMaFromTable(tableName, columnName, prefix, digits);
    }

    private static Long getNextSequenceValue(String sequenceName) {
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String generateNextMaFromTable(String tableName, String columnName, String prefix, int digits) {
        String sql = "SELECT " + columnName + " FROM " + tableName;
        int maxId = 0;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString(1);
                if (id != null && id.startsWith(prefix)) {
                    try {
                        int number = Integer.parseInt(id.substring(prefix.length()));
                        maxId = Math.max(maxId, number);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return format(prefix, digits, maxId + 1L);
    }


    private static String format(String prefix, int digits, long value) {
        return String.format("%s%0" + digits + "d", prefix, value);
    }

    public static String nextMaNV() {
        return generateNextMa("NHANVIEN", "MaNV", "SEQ_NHANVIEN", "NV", 3);
    }

    public static String nextMaNCC() {
        return generateNextMa("NHACUNGCAP", "MaNCC", "SEQ_NHACUNGCAP", "NCC", 2);
    }

    public static String nextMaSP() {
        return generateNextMa("SANPHAM", "MaSP", "SEQ_SANPHAM", "SP", 3);
    }

    public static String nextMaDM() {
        return generateNextMa("DANHMUC", "MaDM", "SEQ_DANHMUC", "DM", 2);
    }

    public static String nextMaKM() {
        return generateNextMa("KHUYENMAI", "MaKM", "SEQ_KHUYENMAI", "KM", 3);
    }

    public static String nextMaBienThe() {
        return generateNextMa("BIENTHESANPHAM", "MaBienThe", "SEQ_BIENTHESANPHAM", "BT", 3);
    }

    public static String nextMaHD() {
        return generateNextMa("HOADON", "MaHD", "SEQ_HOADON", "HD", 3);
    }

    public static String nextMaDH() {
        return generateNextMa("DONHANG", "MaDH", "SEQ_DONHANG", "DH", 3);
    }

    public static String nextMaPN() {
        return generateNextMa("PHIEUNHAP", "MaPN", "SEQ_PHIEUNHAP", "PN", 3);
    }

    public static String nextMaPhieuTra() {
        return generateNextMa("PHIEUXUATTRA", "MaPhieuTra", "SEQ_PHIEUXUATTRA", "PT", 3);
    }

    public static String nextMaKH() {
        return generateNextMa("KHACHHANG", "MaKH", "SEQ_KHACHHANG", "KH", 3);
    }
}
