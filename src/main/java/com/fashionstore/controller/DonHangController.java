 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.DonHangDAO;
import com.fashionstore.model.DonHangSummary;

public class DonHangController {
	private final DonHangDAO donHangDAO = new DonHangDAO();

	public List<DonHangSummary> getRecentOrders(int limit) {
		return donHangDAO.getRecentOrders(limit);
	}

	public int getOrdersTodayCount() {
		return donHangDAO.countOrdersToday();
	}

	public com.fashionstore.model.DonHang getById(String maDH) {
		return donHangDAO.getById(maDH);
	}

	public List<com.fashionstore.model.ChiTietDonHang> getDetails(String maDH) {
		return donHangDAO.getDetails(maDH);
	}

	public List<Object[]> getDetailsForDisplay(String maDH) {
		return donHangDAO.getDetailsForDisplay(maDH);
	}

	public String getPhuongThucTT(String maDH) {
		return donHangDAO.getPhuongThucTT(maDH);
	}
}

