package com.fashionstore.model;

public class NhaCungCap {
	private String maNCC;
	private String tenNCC;
	private String sdt;
	private String email;
	private String diaChi;
	private String trangThaiNCC;

	public NhaCungCap() {
	}

	public NhaCungCap(String maNCC, String tenNCC, String sdt, String email,
			String diaChi, String trangThaiNCC) {
		this.maNCC = maNCC;
		this.tenNCC = tenNCC;
		this.sdt = sdt;
		this.email = email;
		this.diaChi = diaChi;
		this.trangThaiNCC = trangThaiNCC;
	}

	public String getMaNCC() {
		return maNCC;
	}

	public void setMaNCC(String maNCC) {
		this.maNCC = maNCC;
	}

	public String getTenNCC() {
		return tenNCC;
	}

	public void setTenNCC(String tenNCC) {
		this.tenNCC = tenNCC;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getTrangThaiNCC() {
		return trangThaiNCC;
	}

	public void setTrangThaiNCC(String trangThaiNCC) {
		this.trangThaiNCC = trangThaiNCC;
	}
}
