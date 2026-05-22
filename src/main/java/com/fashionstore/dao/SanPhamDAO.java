 
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
	public boolean existsByName(String name, String excludeMaSP) {
		if (name == null) {
			return false;
		}
		String sql;
		if (excludeMaSP == null) {
			sql = "SELECT COUNT(*) FROM SANPHAM WHERE LOWER(TenSP) = LOWER(?)";
		} else {
			sql = "SELECT COUNT(*) FROM SANPHAM WHERE LOWER(TenSP) = LOWER(?) AND MaSP != ?";
		}
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, name.trim());
			if (excludeMaSP != null) {
				stmt.setString(2, excludeMaSP);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean insert(SanPham sp) {
		if (existsByName(sp.getTenSP(), null)) {
			throw new IllegalArgumentException("Tên sản phẩm đã tồn tại.");
		}
		String sql = "INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, sp.getMaSP());
			stmt.setString(2, sp.getMaDM());
			stmt.setString(3, sp.getTenSP());
			stmt.setString(4, sp.getTrangThaiKD());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean update(SanPham sp) {
		if (existsByName(sp.getTenSP(), sp.getMaSP())) {
			throw new IllegalArgumentException("Tên sản phẩm đã tồn tại.");
		}
		String sql = "UPDATE SANPHAM SET MaDM = ?, TenSP = ?, TrangThaiKD = ? WHERE MaSP = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, sp.getMaDM());
			stmt.setString(2, sp.getTenSP());
			stmt.setString(3, sp.getTrangThaiKD());
			stmt.setString(4, sp.getMaSP());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean delete(String maSP) {
		String sql = "DELETE FROM SANPHAM WHERE MaSP = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maSP);
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
