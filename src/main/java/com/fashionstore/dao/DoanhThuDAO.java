 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fashionstore.util.MockData;

public class DoanhThuDAO {
	public long getCurrentMonthRevenue() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getCurrentMonthRevenue();
		}
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(SYSDATE, 'MM')";
		return queryLongValue(sql, MockData.getCurrentMonthRevenue());
	}

	public long getLastMonthRevenue() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getLastMonthRevenue();
		}
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(ADD_MONTHS(SYSDATE, -1), 'MM')";
		return queryLongValue(sql, MockData.getLastMonthRevenue());
	}

	private long queryLongValue(String sql, long fallbackValue) {
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return fallbackValue;
			}
		}
		return 0L;
	}
}

