 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.PhieuNhapKho;
import com.fashionstore.util.MockData;

public class PhieuNhapKhoDAO {
	public List<PhieuNhapKho> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getPhieuNhapList();
		}
		String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP ORDER BY MaPN";
		List<PhieuNhapKho> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				PhieuNhapKho pn = new PhieuNhapKho(
						rs.getString("MaPN"),
						rs.getDate("NgayNhap"),
						rs.getLong("TongGiaTri"),
						rs.getString("MaNCC"),
						rs.getString("MaNV"));
				results.add(pn);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getPhieuNhapList();
			}
		}
		return results;
	}
}

