 
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

	public boolean insert(DanhMucSanPham dm) {
		String sql = "INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, dm.getMaDM());
			stmt.setString(2, dm.getTenDM());
			stmt.setString(3, dm.getMaDMCha());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean update(DanhMucSanPham dm) {
		String sql = "UPDATE DANHMUC SET TenDM = ?, MaDMCha = ? WHERE MaDM = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, dm.getTenDM());
			stmt.setString(2, dm.getMaDMCha());
			stmt.setString(3, dm.getMaDM());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean delete(String maDM) {
		String sql = "DELETE FROM DANHMUC WHERE MaDM = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maDM);
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
