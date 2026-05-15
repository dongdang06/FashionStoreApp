package com.fashionstore.model;

public class BaoCaoDoanhThu {
    private String tieuChi; // Có thể là "Ngày" (VD: 15/05) hoặc "Tên sản phẩm"
    private int soLuong;    // Số lượng đơn hàng (nếu theo ngày) hoặc Số lượng bán (nếu theo SP)
    private long tongTien;  // Tổng doanh thu

    public BaoCaoDoanhThu() {
    }

    public BaoCaoDoanhThu(String tieuChi, int soLuong, long tongTien) {
        this.tieuChi = tieuChi;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
    }

    public String getTieuChi() {
        return tieuChi;
    }

    public void setTieuChi(String tieuChi) {
        this.tieuChi = tieuChi;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public long getTongTien() {
        return tongTien;
    }

    public void setTongTien(long tongTien) {
        this.tongTien = tongTien;
    }
}
