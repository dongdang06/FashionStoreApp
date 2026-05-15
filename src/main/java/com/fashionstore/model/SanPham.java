package com.fashionstore.model;

public class SanPham {
	private String maSP;
	private String maDM;
	private String tenSP;
	private String trangThaiKD;

	public SanPham() {
	}

	public SanPham(String maSP, String maDM, String tenSP, String trangThaiKD) {
		this.maSP = maSP;
		this.maDM = maDM;
		this.tenSP = tenSP;
		this.trangThaiKD = trangThaiKD;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getMaDM() {
		return maDM;
	}

	public void setMaDM(String maDM) {
		this.maDM = maDM;
	}

	public String getTenSP() {
		return tenSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}

	public String getTrangThaiKD() {
		return trangThaiKD;
	}

	public void setTrangThaiKD(String trangThaiKD) {
		this.trangThaiKD = trangThaiKD;
	}
}
