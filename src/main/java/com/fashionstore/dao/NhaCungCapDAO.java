 
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

	public boolean insert(NhaCungCap ncc) {
		String sql = "INSERT INTO NHACUNGCAP (MaNCC, TenNCC, SDT, Email, DiaChi, TrangThaiNCC) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, ncc.getMaNCC());
			stmt.setString(2, ncc.getTenNCC());
			stmt.setString(3, ncc.getSdt());
			stmt.setString(4, ncc.getEmail());
			stmt.setString(5, ncc.getDiaChi());
			stmt.setString(6, ncc.getTrangThaiNCC());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean update(NhaCungCap ncc) {
		String sql = "UPDATE NHACUNGCAP SET TenNCC = ?, SDT = ?, Email = ?, DiaChi = ?, TrangThaiNCC = ? "
				+ "WHERE MaNCC = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, ncc.getTenNCC());
			stmt.setString(2, ncc.getSdt());
			stmt.setString(3, ncc.getEmail());
			stmt.setString(4, ncc.getDiaChi());
			stmt.setString(5, ncc.getTrangThaiNCC());
			stmt.setString(6, ncc.getMaNCC());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean delete(String maNCC) {
		String sql = "DELETE FROM NHACUNGCAP WHERE MaNCC = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maNCC);
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
