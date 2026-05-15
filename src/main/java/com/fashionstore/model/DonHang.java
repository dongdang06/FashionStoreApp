package com.fashionstore.model;

import java.util.Date;

public class DonHang {
	private String maDH;
	private Date ngayMua;
	private long tongTienDH;
	private String maKH;
	private String maKM;
	private int diemSuDung;
	private int diemNhanDuoc;
	private String maNV;

	public DonHang() {
	}

	public DonHang(String maDH, Date ngayMua, long tongTienDH, String maKH,
			String maKM, int diemSuDung, int diemNhanDuoc, String maNV) {
		this.maDH = maDH;
		this.ngayMua = ngayMua;
		this.tongTienDH = tongTienDH;
		this.maKH = maKH;
		this.maKM = maKM;
		this.diemSuDung = diemSuDung;
		this.diemNhanDuoc = diemNhanDuoc;
		this.maNV = maNV;
	}

	public String getMaDH() {
		return maDH;
	}

	public void setMaDH(String maDH) {
		this.maDH = maDH;
	}

	public Date getNgayMua() {
		return ngayMua;
	}

	public void setNgayMua(Date ngayMua) {
		this.ngayMua = ngayMua;
	}

	public long getTongTienDH() {
		return tongTienDH;
	}

	public void setTongTienDH(long tongTienDH) {
		this.tongTienDH = tongTienDH;
	}

	public String getMaKH() {
		return maKH;
	}

	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public int getDiemSuDung() {
		return diemSuDung;
	}

	public void setDiemSuDung(int diemSuDung) {
		this.diemSuDung = diemSuDung;
	}

	public int getDiemNhanDuoc() {
		return diemNhanDuoc;
	}

	public void setDiemNhanDuoc(int diemNhanDuoc) {
		this.diemNhanDuoc = diemNhanDuoc;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
}
