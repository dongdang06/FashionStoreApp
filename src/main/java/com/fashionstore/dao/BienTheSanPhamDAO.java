 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.util.MockData;

public class BienTheSanPhamDAO {
	public List<BienTheSanPham> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getBienTheList();
		}
		String sql = "SELECT MaBienThe, MaSP, MaQR, MauSac, KichThuoc, GiaBan, SoLuongTon "
				+ "FROM BIENTHESANPHAM ORDER BY MaBienThe";
		List<BienTheSanPham> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				BienTheSanPham item = new BienTheSanPham(
						rs.getString("MaBienThe"),
						rs.getString("MaSP"),
						rs.getString("MaQR"),
						rs.getString("MauSac"),
						rs.getString("KichThuoc"),
						rs.getLong("GiaBan"),
						rs.getInt("SoLuongTon"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getBienTheList();
			}
		}
		return results;
	}
}

