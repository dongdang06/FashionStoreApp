 
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
}

