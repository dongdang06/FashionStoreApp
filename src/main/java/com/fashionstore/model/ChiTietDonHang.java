package com.fashionstore.model;

public class ChiTietDonHang {
    private String maDH;
    private String maBienThe;
    private int soLuong;        // khớp với cột SoLuong trong DB
    private long giaBanLucMua;  // khớp với cột GiaBanLucMua trong DB

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(String maDH, String maBienThe, int soLuong, long giaBanLucMua) {
        this.maDH = maDH;
        this.maBienThe = maBienThe;
        this.soLuong = soLuong;
        this.giaBanLucMua = giaBanLucMua;
    }

    public String getMaDH() { return maDH; }
    public void setMaDH(String maDH) { this.maDH = maDH; }

    public String getMaBienThe() { return maBienThe; }
    public void setMaBienThe(String maBienThe) { this.maBienThe = maBienThe; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public long getGiaBanLucMua() { return giaBanLucMua; }
    public void setGiaBanLucMua(long giaBanLucMua) { this.giaBanLucMua = giaBanLucMua; }

    /** Tính thành tiền — không lưu DB, tính từ client */
    public long getThanhTien() {
        return soLuong * giaBanLucMua;
    }
}
