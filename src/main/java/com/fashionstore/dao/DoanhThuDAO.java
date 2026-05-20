 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fashionstore.model.BaoCaoDoanhThu;

public class DoanhThuDAO {
	public long getCurrentMonthRevenue() {
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(SYSDATE, 'MM')";
		return queryLongValue(sql);
	}

	public long getLastMonthRevenue() {
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(ADD_MONTHS(SYSDATE, -1), 'MM')";
		return queryLongValue(sql);
	}

	public List<BaoCaoDoanhThu> getBaoCaoDoanhThu(String criteria, Date tuNgay, Date denNgay) {
		List<BaoCaoDoanhThu> list = new ArrayList<>();
		
		// Chuẩn hóa thời gian bắt đầu và kết thúc
		java.util.Calendar cal = java.util.Calendar.getInstance();
		if (tuNgay != null) {
			cal.setTime(tuNgay);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			tuNgay = cal.getTime();
		} else {
			cal.set(1970, 0, 1, 0, 0, 0);
			tuNgay = cal.getTime();
		}

		if (denNgay != null) {
			cal.setTime(denNgay);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
			cal.set(java.util.Calendar.MINUTE, 59);
			cal.set(java.util.Calendar.SECOND, 59);
			cal.set(java.util.Calendar.MILLISECOND, 999);
			denNgay = cal.getTime();
		} else {
			denNgay = new Date();
		}

		String sql;
		if ("Theo sản phẩm".equals(criteria)) {
			sql = "SELECT "
					+ "    sp.TenSP AS TieuChi, "
					+ "    SUM(ctdh.SoLuong) AS SoLuong, "
					+ "    SUM(ctdh.SoLuong * ctdh.GiaBanLucMua) AS TongDoanhThu, "
					+ "    SUM(ctdh.SoLuong * NVL(ctpn.GiaNhap, ctdh.GiaBanLucMua * 0.6)) AS TongChiPhi "
					+ "FROM HOADON hd "
					+ "JOIN CHITIETDONHANG ctdh ON hd.MaDH = ctdh.MaDH "
					+ "JOIN BIENTHESANPHAM bt ON ctdh.MaBienThe = bt.MaBienThe "
					+ "JOIN SANPHAM sp ON bt.MaSP = sp.MaSP "
					+ "LEFT JOIN ( "
					+ "    SELECT MaBienThe, AVG(GiaNhap) AS GiaNhap "
					+ "    FROM CHITIETPHIEUNHAP "
					+ "    GROUP BY MaBienThe "
					+ ") ctpn ON ctdh.MaBienThe = ctpn.MaBienThe "
					+ "WHERE hd.NgayXuat >= ? AND hd.NgayXuat <= ? "
					+ "GROUP BY sp.TenSP "
					+ "ORDER BY TongDoanhThu DESC";
		} else {
			// Mặc định "Theo ngày"
			sql = "SELECT "
					+ "    TO_CHAR(hd.NgayXuat, 'dd/MM/yyyy') AS TieuChi, "
					+ "    COUNT(hd.MaHD) AS SoLuong, "
					+ "    SUM(hd.TongTienHD) AS TongDoanhThu, "
					+ "    SUM(cost.TotalCost) AS TongChiPhi "
					+ "FROM HOADON hd "
					+ "JOIN ( "
					+ "    SELECT ctdh.MaDH, SUM(ctdh.SoLuong * NVL(ctpn.GiaNhap, ctdh.GiaBanLucMua * 0.6)) AS TotalCost "
					+ "    FROM CHITIETDONHANG ctdh "
					+ "    LEFT JOIN ( "
					+ "        SELECT MaBienThe, AVG(GiaNhap) AS GiaNhap "
					+ "        FROM CHITIETPHIEUNHAP "
					+ "        GROUP BY MaBienThe "
					+ "    ) ctpn ON ctdh.MaBienThe = ctpn.MaBienThe "
					+ "    GROUP BY ctdh.MaDH "
					+ ") cost ON hd.MaDH = cost.MaDH "
					+ "WHERE hd.NgayXuat >= ? AND hd.NgayXuat <= ? "
					+ "GROUP BY TO_CHAR(hd.NgayXuat, 'dd/MM/yyyy'), TRUNC(hd.NgayXuat) "
					+ "ORDER BY TRUNC(hd.NgayXuat)";
		}

		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					BaoCaoDoanhThu bc = new BaoCaoDoanhThu(
							rs.getString("TieuChi"),
							rs.getInt("SoLuong"),
							rs.getLong("TongDoanhThu"),
							rs.getLong("TongChiPhi"));
					list.add(bc);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	private long queryLongValue(String sql) {
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0L;
	}
}

