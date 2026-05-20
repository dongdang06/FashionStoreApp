package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.KhuyenMai;

public class KhuyenMaiDAO {
	public List<KhuyenMai> getAll() {
		String sql = "SELECT MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM "
				+ "FROM KHUYENMAI ORDER BY MaKM";
		List<KhuyenMai> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				KhuyenMai km = new KhuyenMai(
						rs.getString("MaKM"),
						rs.getString("TenKM"),
						rs.getDate("NgayBatDau"),
						rs.getDate("NgayKetThuc"),
						rs.getLong("MucGiamToiDa"),
						rs.getString("TrangThaiKM"));
				results.add(km);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}

	public boolean save(KhuyenMai km) {
		String sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, km.getMaKM());
			stmt.setString(2, km.getTenKM());
			stmt.setDate(3, km.getNgayBatDau() != null ? new java.sql.Date(km.getNgayBatDau().getTime()) : null);
			stmt.setDate(4, km.getNgayKetThuc() != null ? new java.sql.Date(km.getNgayKetThuc().getTime()) : null);
			stmt.setLong(5, km.getMucGiamToiDa());
			stmt.setString(6, km.getTrangThaiKM() != null && !km.getTrangThaiKM().isEmpty() ? km.getTrangThaiKM() : "Dang dien ra");
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean update(KhuyenMai km) {
		String sql = "UPDATE KHUYENMAI SET TenKM = ?, NgayBatDau = ?, NgayKetThuc = ?, MucGiamToiDa = ?, TrangThaiKM = ? WHERE MaKM = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, km.getTenKM());
			stmt.setDate(2, km.getNgayBatDau() != null ? new java.sql.Date(km.getNgayBatDau().getTime()) : null);
			stmt.setDate(3, km.getNgayKetThuc() != null ? new java.sql.Date(km.getNgayKetThuc().getTime()) : null);
			stmt.setLong(4, km.getMucGiamToiDa());
			stmt.setString(5, km.getTrangThaiKM());
			stmt.setString(6, km.getMaKM());
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public boolean delete(String maKM) {
		String sql = "DELETE FROM KHUYENMAI WHERE MaKM = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maKM);
			return stmt.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public KhuyenMai getById(String maKM) {
		String sql = "SELECT MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM FROM KHUYENMAI WHERE MaKM = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maKM);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new KhuyenMai(
							rs.getString("MaKM"),
							rs.getString("TenKM"),
							rs.getDate("NgayBatDau"),
							rs.getDate("NgayKetThuc"),
							rs.getLong("MucGiamToiDa"),
							rs.getString("TrangThaiKM"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
