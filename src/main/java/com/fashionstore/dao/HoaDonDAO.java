 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.HoaDonSummary;
import com.fashionstore.util.MockData;

public class HoaDonDAO {
	public List<HoaDonSummary> getRecentInvoices(int limit) {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getRecentInvoices(limit);
		}
		String sql = "SELECT hd.MaHD, hd.MaDH, hd.NgayXuat, hd.TongTienHD, "
				+ "hd.PhuongThucTT, nv.HoTen "
				+ "FROM HOADON hd "
				+ "LEFT JOIN NHANVIEN nv ON nv.MaNV = hd.MaNV "
				+ "ORDER BY hd.NgayXuat DESC "
				+ "FETCH FIRST ? ROWS ONLY";
		List<HoaDonSummary> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					HoaDonSummary summary = new HoaDonSummary(
							rs.getString("MaHD"),
							rs.getString("MaDH"),
							rs.getDate("NgayXuat"),
							rs.getLong("TongTienHD"),
							rs.getString("PhuongThucTT"),
							rs.getString("HoTen"));
					results.add(summary);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getRecentInvoices(limit);
			}
		}
		return results;
	}
}

