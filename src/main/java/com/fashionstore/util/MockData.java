package com.fashionstore.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.DanhMucSanPham;
import com.fashionstore.model.DonHangSummary;
import com.fashionstore.model.HoaDonSummary;
import com.fashionstore.model.KhuyenMai;
import com.fashionstore.model.NhaCungCap;
import com.fashionstore.model.NhanVien;
import com.fashionstore.model.PhieuNhapKho;
import com.fashionstore.model.PhieuXuatTra;
import com.fashionstore.model.SanPham;

public class MockData {
    private MockData() {
    }

    public static int countOrdersToday() {
        return 12;
    }

    public static int countSellingProducts() {
        return 128;
    }

    public static int countActiveEmployees() {
        return 14;
    }

    public static long getCurrentMonthRevenue() {
        return 128_500_000L;
    }

    public static long getLastMonthRevenue() {
        return 113_200_000L;
    }

    public static List<DonHangSummary> getRecentOrders(int limit) {
        List<DonHangSummary> items = new ArrayList<>();
        items.add(new DonHangSummary("DH-0041", "Tran Thi B", 1_250_000L, "Da thanh toan"));
        items.add(new DonHangSummary("DH-0040", "Le Van C", 890_000L, "Cho thanh toan"));
        items.add(new DonHangSummary("DH-0039", "Pham Minh D", 560_000L, "Da thanh toan"));
        return items.subList(0, Math.min(limit, items.size()));
    }

    public static List<HoaDonSummary> getRecentInvoices(int limit) {
        List<HoaDonSummary> items = new ArrayList<>();
        Date now = new Date();
        items.add(new HoaDonSummary("HD-0101", "DH-0041", now, 1_250_000L, "Tien mat", "Tran Thi B"));
        items.add(new HoaDonSummary("HD-0100", "DH-0039", new Date(now.getTime() - 86400000L),
                560_000L, "Chuyen khoan", "Pham Minh D"));
        return items.subList(0, Math.min(limit, items.size()));
    }

    public static List<SanPham> getSanPhamList() {
        List<SanPham> items = new ArrayList<>();
        items.add(new SanPham("SP-001", "DM-001", "Ao thun unisex", "Dang ban"));
        items.add(new SanPham("SP-002", "DM-002", "Quan jean slim", "Dang ban"));
        items.add(new SanPham("SP-003", "DM-003", "Vay midi", "Ngung ban"));
        return items;
    }

    public static List<DanhMucSanPham> getDanhMucList() {
        List<DanhMucSanPham> items = new ArrayList<>();
        items.add(new DanhMucSanPham("DM-001", "Ao", null));
        items.add(new DanhMucSanPham("DM-002", "Quan", null));
        items.add(new DanhMucSanPham("DM-003", "Vay", null));
        return items;
    }

    public static List<NhaCungCap> getNhaCungCapList() {
        List<NhaCungCap> items = new ArrayList<>();
        items.add(new NhaCungCap("NCC-01", "Cong ty May Viet", "0909000111",
                "contact@mayviet.com", "Quan 1", "Hoat dong"));
        items.add(new NhaCungCap("NCC-02", "Xuong Det An Phu", "0909000222",
                "info@anphu.vn", "Quan 7", "Hoat dong"));
        return items;
    }

    public static List<BienTheSanPham> getBienTheList() {
        List<BienTheSanPham> items = new ArrayList<>();
        items.add(new BienTheSanPham("BT-001", "SP-001", "Trang", "M", 250_000L, 30));
        items.add(new BienTheSanPham("BT-002", "SP-001", "Den", "L", 250_000L, 18));
        items.add(new BienTheSanPham("BT-003", "SP-002", "Xanh", "32", 420_000L, 10));
        return items;
    }

    public static List<NhanVien> getNhanVienList() {
        List<NhanVien> items = new ArrayList<>();
        items.add(new NhanVien("NV-01", "Nguyen Van A", "a@shop.vn", "0909123456", "Quan ly", new Date(), "Dang lam viec", "Quan ly"));
        items.add(new NhanVien("NV-02", "Tran Thi B", "b@shop.vn", "0909234567", "Nhan vien ban hang", new Date(), "Dang lam viec", "Nhan vien ban hang"));
        items.add(new NhanVien("NV-03", "Le Van C", "c@shop.vn", "0909345678", "Nhan vien kho", new Date(), "Da nghi viec", "Nhan vien kho"));
        return items;
    }

    public static List<KhuyenMai> getKhuyenMaiList() {
        List<KhuyenMai> items = new ArrayList<>();
        Date now = new Date();
        items.add(new KhuyenMai("KM-01", "Giam he", new Date(now.getTime() - 5L * 86400000L),
                new Date(now.getTime() + 10L * 86400000L), 150_000L, "Dang dien ra"));
        items.add(new KhuyenMai("KM-02", "Back to school", new Date(now.getTime() - 20L * 86400000L),
                new Date(now.getTime() - 2L * 86400000L), 100_000L, "Ket thuc"));
        return items;
    }

    public static List<PhieuNhapKho> getPhieuNhapList() {
        List<PhieuNhapKho> items = new ArrayList<>();
        Date now = new Date();
        items.add(new PhieuNhapKho("PN-01", new Date(now.getTime() - 2L * 86400000L), 3_500_000L,
                "NCC-01", "NV-01"));
        items.add(new PhieuNhapKho("PN-02", new Date(now.getTime() - 7L * 86400000L), 2_200_000L,
                "NCC-02", "NV-02"));
        return items;
    }

    public static List<PhieuXuatTra> getPhieuXuatList() {
        List<PhieuXuatTra> items = new ArrayList<>();
        Date now = new Date();
        items.add(new PhieuXuatTra("PT-01", "NCC-01", "NV-01",
                new Date(now.getTime() - 3L * 86400000L), "Loi mau"));
        items.add(new PhieuXuatTra("PT-02", "NCC-02", "NV-02",
                new Date(now.getTime() - 1L * 86400000L), "Sai kich thuoc"));
        return items;
    }
}
