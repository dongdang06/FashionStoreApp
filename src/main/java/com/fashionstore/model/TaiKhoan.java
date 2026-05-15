package com.fashionstore.model;

import java.util.Date;

public class TaiKhoan {
    private String maTaiKhoan;
    private String maNV; // Khoá ngoại → NHANVIEN
    private String userName;
    private String passWord;
    private Date ngayTao;
    private String trangThai;
    private String vaiTro;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan, String maNV, String userName,
            String passWord, Date ngayTao, String trangThai, String vaiTro) {
        this.maTaiKhoan = maTaiKhoan;
        this.maNV = maNV;
        this.userName = userName;
        this.passWord = passWord;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.vaiTro = vaiTro;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String v) {
        this.maTaiKhoan = v;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String v) {
        this.maNV = v;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String v) {
        this.userName = v;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String v) {
        this.passWord = v;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date v) {
        this.ngayTao = v;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String v) {
        this.trangThai = v;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String v) {
        this.vaiTro = v;
    }
}