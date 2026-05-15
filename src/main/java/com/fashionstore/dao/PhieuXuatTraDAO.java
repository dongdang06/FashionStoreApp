 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.PhieuXuatTra;
import com.fashionstore.util.MockData;

public class PhieuXuatTraDAO {
	public List<PhieuXuatTra> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getPhieuXuatList();
		}
		String sql = "SELECT MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo "
				+ "FROM PHIEUXUATTRA ORDER BY MaPhieuTra";
		List<PhieuXuatTra> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				PhieuXuatTra px = new PhieuXuatTra(
						rs.getString("MaPhieuTra"),
						rs.getString("MaNCC"),
						rs.getString("MaNV"),
						rs.getDate("NgayTra"),
						rs.getString("LyDo"));
				results.add(px);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getPhieuXuatList();
			}
		}
		return results;
	}
}

