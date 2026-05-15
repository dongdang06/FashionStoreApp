package com.fashionstore.model;

import java.util.Date;

public class KhuyenMai {
	private String maKM;
	private String tenKM;
	private Date ngayBatDau;
	private Date ngayKetThuc;
	private long mucGiamToiDa;
	private String trangThaiKM;

	public KhuyenMai() {
	}

	public KhuyenMai(String maKM, String tenKM, Date ngayBatDau,
			Date ngayKetThuc, long mucGiamToiDa, String trangThaiKM) {
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.mucGiamToiDa = mucGiamToiDa;
		this.trangThaiKM = trangThaiKM;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public long getMucGiamToiDa() {
		return mucGiamToiDa;
	}

	public void setMucGiamToiDa(long mucGiamToiDa) {
		this.mucGiamToiDa = mucGiamToiDa;
	}

	public String getTrangThaiKM() {
		return trangThaiKM;
	}

	public void setTrangThaiKM(String trangThaiKM) {
		this.trangThaiKM = trangThaiKM;
	}
}
