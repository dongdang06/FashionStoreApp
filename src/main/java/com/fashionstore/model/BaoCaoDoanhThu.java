package com.fashionstore.model;

public class BaoCaoDoanhThu {
    private String tieuChi; // Có thể là "Ngày" (VD: 15/05) hoặc "Tên sản phẩm"
    private int soLuong;    // Số lượng đơn hàng (nếu theo ngày) hoặc Số lượng bán (nếu theo SP)
    private long tongTien;  // Tổng doanh thu
    private long tongChiPhi; // Tổng chi phí
    private long tongLoiNhuan; // Tổng lợi nhuận

    public BaoCaoDoanhThu() {
    }

    public BaoCaoDoanhThu(String tieuChi, int soLuong, long tongTien, long tongChiPhi) {
        this.tieuChi = tieuChi;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
        this.tongChiPhi = tongChiPhi;
        this.tongLoiNhuan = tongTien - tongChiPhi;
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
        this.tongLoiNhuan = this.tongTien - this.tongChiPhi;
    }

    public long getTongChiPhi() {
        return tongChiPhi;
    }

    public void setTongChiPhi(long tongChiPhi) {
        this.tongChiPhi = tongChiPhi;
        this.tongLoiNhuan = this.tongTien - this.tongChiPhi;
    }

    public long getTongLoiNhuan() {
        return tongLoiNhuan;
    }
}
