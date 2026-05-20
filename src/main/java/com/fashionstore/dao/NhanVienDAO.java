 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.NhanVien;
import com.fashionstore.util.MockData;

public class NhanVienDAO {
	public int countDangLamViec() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.countActiveEmployees();
		}
		String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE TrangThaiLamViec = 'Dang lam viec'";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.countActiveEmployees();
			}
		}
		return 0;
	}

	public List<NhanVien> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return MockData.getNhanVienList();
		}
		String sql = "SELECT MaNV, HoTen, Email, SDT, ChucVu, NgayVaoLam, TrangThaiLamViec FROM NHANVIEN ORDER BY MaNV";
		List<NhanVien> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				NhanVien nv = new NhanVien(
						rs.getString("MaNV"),
						rs.getString("HoTen"),
						rs.getString("Email"),
						rs.getString("SDT"),
						rs.getString("ChucVu"),
						rs.getDate("NgayVaoLam"),
						rs.getString("TrangThaiLamViec"));
				results.add(nv);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return MockData.getNhanVienList();
			}
		}
		return results;
	}
}

