package com.fashionstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhieuNhapKho {
	private String maPN;
	private Date ngayNhap;
	private long tongGiaTri;
	private String maNCC;
	private String maNV;
	private List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();

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

	public PhieuNhapKho(String maPN, Date ngayNhap, long tongGiaTri,
			String maNCC, String maNV, List<ChiTietPhieuNhap> chiTietList) {
		this(maPN, ngayNhap, tongGiaTri, maNCC, maNV);
		setChiTietList(chiTietList);
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

	public List<ChiTietPhieuNhap> getChiTietList() {
		return chiTietList;
	}

	public void setChiTietList(List<ChiTietPhieuNhap> chiTietList) {
		this.chiTietList = chiTietList == null ? new ArrayList<>() : new ArrayList<>(chiTietList);
	}
}
