package com.fashionstore.model;

import java.util.Date;

public class PhieuNhapKho {
	private String maPN;
	private Date ngayNhap;
	private long tongGiaTri;
	private String maNCC;
	private String maNV;

	public PhieuNhapKho() {
	}

	public PhieuNhapKho(String maPN, Date ngayNhap, long tongGiaTri,
			String maNCC, String maNV) {
		this.maPN = maPN;
		this.ngayNhap = ngayNhap;
		this.tongGiaTri = tongGiaTri;
		this.maNCC = maNCC;
		this.maNV = maNV;
	}

	public String getMaPN() {
		return maPN;
	}

	public void setMaPN(String maPN) {
		this.maPN = maPN;
	}

	public Date getNgayNhap() {
		return ngayNhap;
	}

	public void setNgayNhap(Date ngayNhap) {
		this.ngayNhap = ngayNhap;
	}

	public long getTongGiaTri() {
		return tongGiaTri;
	}

	public void setTongGiaTri(long tongGiaTri) {
		this.tongGiaTri = tongGiaTri;
	}

	public String getMaNCC() {
		return maNCC;
	}

	public void setMaNCC(String maNCC) {
		this.maNCC = maNCC;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
}
