 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.DanhMucSanPham;

public class DanhMucSanPhamDAO {
	public List<DanhMucSanPham> getAll() {
		String sql = "SELECT MaDM, TenDM, MaDMCha FROM DANHMUC ORDER BY MaDM";
		List<DanhMucSanPham> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				DanhMucSanPham item = new DanhMucSanPham(
						rs.getString("MaDM"),
						rs.getString("TenDM"),
						rs.getString("MaDMCha"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}
