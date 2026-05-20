package com.fashionstore.model;

public class ChiTietKhuyenMai {
    private String maKM;
    private String maBienThe;
    private long giaKhuyenMai;

    public ChiTietKhuyenMai() {
    }

    public ChiTietKhuyenMai(String maKM, String maBienThe, long giaKhuyenMai) {
        this.maKM = maKM;
        this.maBienThe = maBienThe;
        this.giaKhuyenMai = giaKhuyenMai;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(String maBienThe) {
        this.maBienThe = maBienThe;
    }

    public long getGiaKhuyenMai() {
        return giaKhuyenMai;
    }

    public void setGiaKhuyenMai(long giaKhuyenMai) {
        this.giaKhuyenMai = giaKhuyenMai;
    }
}
