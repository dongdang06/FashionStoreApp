package com.fashionstore.model;

import java.util.Date;

public class HoaDonSummary {
    private final String maHD;
    private final String maDH;
    private final Date ngayXuat;
    private final long tongTien;
    private final String phuongThucTT;
    private final String nhanVien;

    public HoaDonSummary(String maHD, String maDH, Date ngayXuat, long tongTien,
            String phuongThucTT, String nhanVien) {
        this.maHD = maHD;
        this.maDH = maDH;
        this.ngayXuat = ngayXuat;
        this.tongTien = tongTien;
        this.phuongThucTT = phuongThucTT;
        this.nhanVien = nhanVien;
    }

    public String getMaHD() {
        return maHD;
    }

    public String getMaDH() {
        return maDH;
    }

    public Date getNgayXuat() {
        return ngayXuat;
    }

    public long getTongTien() {
        return tongTien;
    }

    public String getPhuongThucTT() {
        return phuongThucTT;
    }

    public String getNhanVien() {
        return nhanVien;
    }
}
