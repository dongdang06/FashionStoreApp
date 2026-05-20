package com.fashionstore.model;

public class ChiTietPhieuNhap {
	private String maPN;
	private String maBienThe;
	private int soLuongNhap;
	private long giaNhap;

	public ChiTietPhieuNhap() {
	}

	public ChiTietPhieuNhap(String maPN, String maBienThe, int soLuongNhap, long giaNhap) {
		this.maPN = maPN;
		this.maBienThe = maBienThe;
		this.soLuongNhap = soLuongNhap;
		this.giaNhap = giaNhap;
	}

	public String getMaPN() {
		return maPN;
	}

	public void setMaPN(String maPN) {
		this.maPN = maPN;
	}

	public String getMaBienThe() {
		return maBienThe;
	}

	public void setMaBienThe(String maBienThe) {
		this.maBienThe = maBienThe;
	}

	public int getSoLuongNhap() {
		return soLuongNhap;
	}

	public void setSoLuongNhap(int soLuongNhap) {
		this.soLuongNhap = soLuongNhap;
	}

	public long getGiaNhap() {
		return giaNhap;
	}

	public void setGiaNhap(long giaNhap) {
		this.giaNhap = giaNhap;
	}

	public long getThanhTien() {
		return soLuongNhap * giaNhap;
	}
}
