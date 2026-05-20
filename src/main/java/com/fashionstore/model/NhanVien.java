package com.fashionstore.model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String email;
    private String sdt;
    private String trangThaiLamViec; // 'Dang lam viec' | 'Da nghi viec'
    private String vaiTro;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String email,
            String sdt, String trangThaiLamViec) {
        this(maNV, hoTen, email, sdt, trangThaiLamViec, "");
    }

    public NhanVien(String maNV, String hoTen, String email,
            String sdt, String trangThaiLamViec, String vaiTro) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.email = email;
        this.sdt = sdt;
        this.trangThaiLamViec = trangThaiLamViec;
        this.vaiTro = vaiTro != null ? vaiTro : "";
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTrangThaiLamViec() {
        return trangThaiLamViec;
    }

    public void setTrangThaiLamViec(String tt) {
        this.trangThaiLamViec = tt;
    }

    public String getVaiTro() {
        return vaiTro != null ? vaiTro : "";
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    @Override
    public String toString() {
        return "[" + maNV + "] " + hoTen;
    }
}