 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.KhuyenMai;
import com.fashionstore.util.MockData;

public class KhuyenMaiDAO {
	public List<KhuyenMai> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getKhuyenMaiList();
		}
		String sql = "SELECT MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM "
				+ "FROM KHUYENMAI ORDER BY MaKM";
		List<KhuyenMai> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				KhuyenMai km = new KhuyenMai(
						rs.getString("MaKM"),
						rs.getString("TenKM"),
						rs.getDate("NgayBatDau"),
						rs.getDate("NgayKetThuc"),
						rs.getLong("MucGiamToiDa"),
						rs.getString("TrangThaiKM"));
				results.add(km);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getKhuyenMaiList();
			}
		}
		return results;
	}
}

