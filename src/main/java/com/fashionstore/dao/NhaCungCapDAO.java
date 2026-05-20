 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.NhaCungCap;

public class NhaCungCapDAO {
	public List<NhaCungCap> getAll() {
		String sql = "SELECT MaNCC, TenNCC, SDT, Email, DiaChi, TrangThaiNCC "
				+ "FROM NHACUNGCAP ORDER BY MaNCC";
		List<NhaCungCap> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				NhaCungCap item = new NhaCungCap(
						rs.getString("MaNCC"),
						rs.getString("TenNCC"),
						rs.getString("SDT"),
						rs.getString("Email"),
						rs.getString("DiaChi"),
						rs.getString("TrangThaiNCC"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}
