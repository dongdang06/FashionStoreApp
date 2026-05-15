package com.fashionstore.model;

public class ChiTietDonHang {
	private String maDH;
	private String maBienThe;
	private int soLuongMua;
	private long donGia;
	private long thanhTien;

	public ChiTietDonHang() {
	}

	public ChiTietDonHang(String maDH, String maBienThe, int soLuongMua, long donGia, long thanhTien) {
		this.maDH = maDH;
		this.maBienThe = maBienThe;
		this.soLuongMua = soLuongMua;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public String getMaDH() { return maDH; }
	public void setMaDH(String maDH) { this.maDH = maDH; }

	public String getMaBienThe() { return maBienThe; }
	public void setMaBienThe(String maBienThe) { this.maBienThe = maBienThe; }

	public int getSoLuongMua() { return soLuongMua; }
	public void setSoLuongMua(int soLuongMua) { this.soLuongMua = soLuongMua; }

	public long getDonGia() { return donGia; }
	public void setDonGia(long donGia) { this.donGia = donGia; }

	public long getThanhTien() { return thanhTien; }
	public void setThanhTien(long thanhTien) { this.thanhTien = thanhTien; }
}
