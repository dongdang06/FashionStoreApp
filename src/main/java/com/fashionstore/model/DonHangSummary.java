package com.fashionstore.model;

import java.util.Date;

public class DonHangSummary {
    private final String maDH;
    private final Date ngayMua;
    private final String nhanVien;
    private final long tongTien;
    private final String trangThai;

    public DonHangSummary(String maDH, Date ngayMua, String nhanVien, long tongTien, String trangThai) {
        this.maDH = maDH;
        this.ngayMua = ngayMua;
        this.nhanVien = nhanVien;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaDH() {
        return maDH;
    }

    public Date getNgayMua() {
        return ngayMua;
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
