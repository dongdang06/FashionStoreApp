package com.fashionstore.model;

import java.util.Date;

public class HoaDon {
	private String maHD;
	private String maDH;
	private Date ngayXuat;
	private long tongTienHD;
	private String phuongThucTT;
	private String ghiChu;
	private String maNV;

	public HoaDon() {
	}

	public HoaDon(String maHD, String maDH, Date ngayXuat, long tongTienHD,
			String phuongThucTT, String ghiChu, String maNV) {
		this.maHD = maHD;
		this.maDH = maDH;
		this.ngayXuat = ngayXuat;
		this.tongTienHD = tongTienHD;
		this.phuongThucTT = phuongThucTT;
		this.ghiChu = ghiChu;
		this.maNV = maNV;
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public String getMaDH() {
		return maDH;
	}

	public void setMaDH(String maDH) {
		this.maDH = maDH;
	}

	public Date getNgayXuat() {
		return ngayXuat;
	}

	public void setNgayXuat(Date ngayXuat) {
		this.ngayXuat = ngayXuat;
	}

	public long getTongTienHD() {
		return tongTienHD;
	}

	public void setTongTienHD(long tongTienHD) {
		this.tongTienHD = tongTienHD;
	}

	public String getPhuongThucTT() {
		return phuongThucTT;
	}

	public void setPhuongThucTT(String phuongThucTT) {
		this.phuongThucTT = phuongThucTT;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
}
