package com.fashionstore.model;

public class NhanVien {
    private int id;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String vaiTro;   // QUAN_LY, NV_KHO, NV_BAN_HANG, NV_KE_TOAN
    private String trangThai; // DANG_LAM, DA_NGHI
    private String taiKhoan;
    private String matKhau;

    public NhanVien() {}

    public NhanVien(int id, String hoTen, String soDienThoai, String email,
                    String vaiTro, String trangThai, String taiKhoan, String matKhau) {
        this.id = id;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    @Override
    public String toString() { return hoTen; }
}