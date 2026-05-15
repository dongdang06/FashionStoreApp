package com.fashionstore.model;

import java.util.Date;

public class PhieuXuatTra {
	private String maPhieuTra;
	private String maNCC;
	private String maNV;
	private Date ngayTra;
	private String lyDo;

	public PhieuXuatTra() {
	}

	public PhieuXuatTra(String maPhieuTra, String maNCC, String maNV,
			Date ngayTra, String lyDo) {
		this.maPhieuTra = maPhieuTra;
		this.maNCC = maNCC;
		this.maNV = maNV;
		this.ngayTra = ngayTra;
		this.lyDo = lyDo;
	}

	public String getMaPhieuTra() {
		return maPhieuTra;
	}

	public void setMaPhieuTra(String maPhieuTra) {
		this.maPhieuTra = maPhieuTra;
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

	public Date getNgayTra() {
		return ngayTra;
	}

	public void setNgayTra(Date ngayTra) {
		this.ngayTra = ngayTra;
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}
}
