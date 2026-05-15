package com.fashionstore.model;

public class DanhMucSanPham {
	private String maDM;
	private String tenDM;
	private String maDMCha;

	public DanhMucSanPham() {
	}

	public DanhMucSanPham(String maDM, String tenDM, String maDMCha) {
		this.maDM = maDM;
		this.tenDM = tenDM;
		this.maDMCha = maDMCha;
	}

	public String getMaDM() {
		return maDM;
	}

	public void setMaDM(String maDM) {
		this.maDM = maDM;
	}

	public String getTenDM() {
		return tenDM;
	}

	public void setTenDM(String tenDM) {
		this.tenDM = tenDM;
	}

	public String getMaDMCha() {
		return maDMCha;
	}

	public void setMaDMCha(String maDMCha) {
		this.maDMCha = maDMCha;
	}
}
