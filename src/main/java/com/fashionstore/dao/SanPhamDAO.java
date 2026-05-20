 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.SanPham;

public class SanPhamDAO {
	public int countDangBan() {
		String sql = "SELECT COUNT(*) FROM SANPHAM WHERE TrangThaiKD = 'Dang ban'";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public List<SanPham> getAll() {
		String sql = "SELECT MaSP, MaDM, TenSP, TrangThaiKD FROM SANPHAM ORDER BY MaSP";
		List<SanPham> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				SanPham item = new SanPham(
						rs.getString("MaSP"),
						rs.getString("MaDM"),
						rs.getString("TenSP"),
						rs.getString("TrangThaiKD"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}
