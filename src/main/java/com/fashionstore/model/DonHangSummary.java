package com.fashionstore.model;

public class DonHangSummary {
    private final String maDH;
    private final String nhanVien;
    private final long tongTien;
    private final String trangThai;

    public DonHangSummary(String maDH, String nhanVien, long tongTien, String trangThai) {
        this.maDH = maDH;
        this.nhanVien = nhanVien;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaDH() {
        return maDH;
    }

    public String getNhanVien() {
        return nhanVien;
    }

    public long getTongTien() {
        return tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }
}
