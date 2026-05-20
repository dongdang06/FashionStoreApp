package com.fashionstore.model;

public class ChiTietPhieuXuat {
    private String maPhieuTra;
    private String maBienThe;
    private int soLuong;
    private long giaXuat;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(String maPhieuTra, String maBienThe, int soLuong, long giaXuat) {
        this.maPhieuTra = maPhieuTra;
        this.maBienThe = maBienThe;
        this.soLuong = soLuong;
        this.giaXuat = giaXuat;
    }

    public String getMaPhieuTra() { return maPhieuTra; }
    public void setMaPhieuTra(String maPhieuTra) { this.maPhieuTra = maPhieuTra; }

    public String getMaBienThe() { return maBienThe; }
    public void setMaBienThe(String maBienThe) { this.maBienThe = maBienThe; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getGiaXuat() { return giaXuat; }
    public void setGiaXuat(long giaXuat) { this.giaXuat = giaXuat; }

    /** Thành tiền = SoLuong x GiaXuat */
    public long getThanhTien() {
        return soLuong * giaXuat;
    }
}
