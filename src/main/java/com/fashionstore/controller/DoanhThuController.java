 
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

	public List<BaoCaoDoanhThu> getBaoCaoDoanhThu(String criteria) {
		List<BaoCaoDoanhThu> list = new ArrayList<>();
		if ("Theo sản phẩm".equals(criteria)) {
			list.add(new BaoCaoDoanhThu("Áo Thun Cổ Tròn Basic", 150, 15000000));
			list.add(new BaoCaoDoanhThu("Quần Jean Nam Ống Rộng", 120, 24000000));
			list.add(new BaoCaoDoanhThu("Áo Sơ Mi Trắng Dài Tay", 85, 12750000));
			list.add(new BaoCaoDoanhThu("Giày Sneaker Thể Thao", 45, 22500000));
			list.add(new BaoCaoDoanhThu("Mũ Lưỡi Trai Nữ", 210, 4200000));
		} else {
			// Mặc định "Theo ngày"
			list.add(new BaoCaoDoanhThu("10/05/2026", 15, 4500000));
			list.add(new BaoCaoDoanhThu("11/05/2026", 22, 6800000));
			list.add(new BaoCaoDoanhThu("12/05/2026", 18, 5100000));
			list.add(new BaoCaoDoanhThu("13/05/2026", 30, 9200000));
			list.add(new BaoCaoDoanhThu("14/05/2026", 12, 3600000));
			list.add(new BaoCaoDoanhThu("15/05/2026", 25, 7500000));
		}
		return list;
	}
}

