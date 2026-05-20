package com.fashionstore.model;

public class BienTheSanPham {
	private String maBienThe;
	private String maSP;
	private String mauSac;
	private String kichThuoc;
	private long giaBan;
	private int soLuongTon;

	public BienTheSanPham() {
	}

	public BienTheSanPham(String maBienThe, String maSP,
			String mauSac, String kichThuoc, long giaBan, int soLuongTon) {
		this.maBienThe = maBienThe;
		this.maSP = maSP;
		this.mauSac = mauSac;
		this.kichThuoc = kichThuoc;
		this.giaBan = giaBan;
		this.soLuongTon = soLuongTon;
	}

	public String getMaBienThe() {
		return maBienThe;
	}

	public void setMaBienThe(String maBienThe) {
		this.maBienThe = maBienThe;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getMauSac() {
		return mauSac;
	}

	public void setMauSac(String mauSac) {
		this.mauSac = mauSac;
	}

	public String getKichThuoc() {
		return kichThuoc;
	}

	public void setKichThuoc(String kichThuoc) {
		this.kichThuoc = kichThuoc;
	}

	public long getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(long giaBan) {
		this.giaBan = giaBan;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public void setSoLuongTon(int soLuongTon) {
		this.soLuongTon = soLuongTon;
	}
}
