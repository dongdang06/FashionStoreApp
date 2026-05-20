 
package com.fashionstore.controller;

import com.fashionstore.dao.DoanhThuDAO;
import com.fashionstore.dao.NhanVienDAO;
import com.fashionstore.dao.SanPhamDAO;
import com.fashionstore.model.DashboardStats;
import com.fashionstore.model.BaoCaoDoanhThu;
import java.util.ArrayList;
import java.util.List;

public class DoanhThuController {
	private final DoanhThuDAO doanhThuDAO = new DoanhThuDAO();
	private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
	private final DonHangController donHangController = new DonHangController();

	public DashboardStats getDashboardStats() {
		long currentRevenue = doanhThuDAO.getCurrentMonthRevenue();
		long lastRevenue = doanhThuDAO.getLastMonthRevenue();
		int ordersToday = donHangController.getOrdersTodayCount();
		int sellingProducts = sanPhamDAO.countDangBan();
		int activeEmployees = nhanVienDAO.countDangLamViec();

		return new DashboardStats(currentRevenue, lastRevenue, ordersToday,
				sellingProducts, activeEmployees);
	}

	public List<BaoCaoDoanhThu> getBaoCaoDoanhThu(String criteria, java.util.Date tuNgay, java.util.Date denNgay) {
		return doanhThuDAO.getBaoCaoDoanhThu(criteria, tuNgay, denNgay);
	}
}

