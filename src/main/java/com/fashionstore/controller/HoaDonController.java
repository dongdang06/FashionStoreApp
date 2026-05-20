 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.HoaDonDAO;
import com.fashionstore.model.HoaDonSummary;

public class HoaDonController {
	private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

	public List<HoaDonSummary> getRecentInvoices(int limit) {
		return hoaDonDAO.getRecentInvoices(limit);
	}

	public String getInvoiceHTML(String maHD) {
		return hoaDonDAO.getInvoiceHTML(maHD);
	}
}

