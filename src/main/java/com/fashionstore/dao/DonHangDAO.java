 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.DonHangSummary;
import com.fashionstore.util.MockData;

public class DonHangDAO {
	public int countOrdersToday() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.countOrdersToday();
		}
		String sql = "SELECT COUNT(*) FROM DONHANG WHERE TRUNC(NgayMua) = TRUNC(SYSDATE)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.countOrdersToday();
			}
		}
		return 0;
	}

	public List<DonHangSummary> getRecentOrders(int limit) {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getRecentOrders(limit);
		}
		String sql = "SELECT dh.MaDH, nv.HoTen, dh.TongTienDH, "
				+ "CASE WHEN hd.MaHD IS NULL THEN N'Cho thanh toan' "
				+ "ELSE N'Da thanh toan' END AS TrangThai "
				+ "FROM DONHANG dh "
				+ "LEFT JOIN HOADON hd ON hd.MaDH = dh.MaDH "
				+ "LEFT JOIN NHANVIEN nv ON nv.MaNV = dh.MaNV "
				+ "ORDER BY dh.NgayMua DESC "
				+ "FETCH FIRST ? ROWS ONLY";
		List<DonHangSummary> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					DonHangSummary summary = new DonHangSummary(
							rs.getString("MaDH"),
							rs.getString("HoTen"),
							rs.getLong("TongTienDH"),
							rs.getString("TrangThai"));
					results.add(summary);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getRecentOrders(limit);
			}
		}
		return results;
	}
}

