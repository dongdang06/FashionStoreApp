 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamDAO {
	public List<BienTheSanPham> getAll() {
		String sql = "SELECT MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon "
				+ "FROM BIENTHESANPHAM ORDER BY MaBienThe";
		List<BienTheSanPham> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				BienTheSanPham item = new BienTheSanPham(
						rs.getString("MaBienThe"),
						rs.getString("MaSP"),
						rs.getString("MauSac"),
						rs.getString("KichThuoc"),
						rs.getLong("GiaBan"),
						rs.getInt("SoLuongTon"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
	public boolean insert(BienTheSanPham bt) {
		String sql = "INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, bt.getMaBienThe());
			stmt.setString(2, bt.getMaSP());
			stmt.setString(3, bt.getMauSac());
			stmt.setString(4, bt.getKichThuoc());
			stmt.setLong(5, bt.getGiaBan());
			stmt.setInt(6, bt.getSoLuongTon());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean update(BienTheSanPham bt) {
		String sql = "UPDATE BIENTHESANPHAM SET MaSP = ?, MauSac = ?, KichThuoc = ?, GiaBan = ?, SoLuongTon = ? "
				+ "WHERE MaBienThe = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, bt.getMaSP());
			stmt.setString(2, bt.getMauSac());
			stmt.setString(3, bt.getKichThuoc());
			stmt.setLong(4, bt.getGiaBan());
			stmt.setInt(5, bt.getSoLuongTon());
			stmt.setString(6, bt.getMaBienThe());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean delete(String maBienThe) {
		String sql = "DELETE FROM BIENTHESANPHAM WHERE MaBienThe = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maBienThe);
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
