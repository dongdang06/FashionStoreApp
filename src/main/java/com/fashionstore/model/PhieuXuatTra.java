package com.fashionstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhieuXuatTra {
	private String maPhieuTra;
	private String maNCC;
	private String maNV;
	private Date ngayTra;
	private String lyDo;
	private List<ChiTietPhieuXuat> chiTietList = new ArrayList<>();

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

	public PhieuXuatTra(String maPhieuTra, String maNCC, String maNV,
			Date ngayTra, String lyDo, List<ChiTietPhieuXuat> chiTietList) {
		this(maPhieuTra, maNCC, maNV, ngayTra, lyDo);
		setChiTietList(chiTietList);
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

	public List<ChiTietPhieuXuat> getChiTietList() {
		return chiTietList;
	}

	public void setChiTietList(List<ChiTietPhieuXuat> chiTietList) {
		this.chiTietList = chiTietList == null ? new ArrayList<>() : new ArrayList<>(chiTietList);
	}
}
