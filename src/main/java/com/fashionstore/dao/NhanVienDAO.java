 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.NhanVien;

public class NhanVienDAO {
	public int countDangLamViec() {
		String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE TrangThaiLamViec = 'Dang lam viec'";
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

	public List<NhanVien> getAll() {
		String sql = "SELECT nv.MaNV, nv.HoTen, nv.Email, nv.SDT, nv.ChucVu, nv.NgayVaoLam, nv.TrangThaiLamViec, tk.VaiTro "
				+ "FROM NHANVIEN nv LEFT JOIN TAIKHOAN tk ON nv.MaNV = tk.MaNV ORDER BY nv.MaNV";
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
						rs.getString("TrangThaiLamViec"),
						rs.getString("VaiTro"));
				results.add(nv);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}

	public String getHoTenByMaNV(String maNV) {
		if (maNV == null || maNV.trim().isEmpty()) {
			return null;
		}
		String sql = "SELECT HoTen FROM NHANVIEN WHERE MaNV = ?";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maNV.trim());
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("HoTen");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean add(NhanVien nv) {
		String call = "{ CALL PROC_Them_NhanVien(?, ?, ?, ?, ?, ?, ?) }";
		try (Connection conn = DBConnection.getInstance().getConnection();
				java.sql.CallableStatement stmt = conn.prepareCall(call)) {
			// IN parameters
			stmt.setString(1, nv.getMaNV());
			stmt.setString(2, nv.getHoTen());
			stmt.setString(3, nv.getEmail());
			stmt.setString(4, nv.getSdt());
			stmt.setString(5, nv.getChucVu());
			stmt.setString(6, nv.getVaiTro());
			// OUT parameter
			stmt.registerOutParameter(7, java.sql.Types.VARCHAR);

			stmt.execute();

			String result = stmt.getString(7);
			if ("SUCCESS".equals(result)) {
				return true;
			} else {
				System.err.println("PROC_Them_NhanVien error: " + result);
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean update(NhanVien nv) {
		String sqlNV = "UPDATE NHANVIEN SET HoTen = ?, Email = ?, SDT = ?, ChucVu = ?, TrangThaiLamViec = ? WHERE MaNV = ?";
		String sqlTK = "UPDATE TAIKHOAN SET VaiTro = ?, TrangThai = ? WHERE MaNV = ?";
		
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);

			String accountStatus = "Dang lam viec".equals(nv.getTrangThaiLamViec()) ? "Hoat dong" : "Bi khoa";
			validateLastActiveManager(conn, nv.getMaNV(), nv.getVaiTro(), accountStatus);
			
			// 1. Update NHANVIEN
			try (PreparedStatement stmtNV = conn.prepareStatement(sqlNV)) {
				stmtNV.setString(1, nv.getHoTen());
				stmtNV.setString(2, nv.getEmail());
				stmtNV.setString(3, nv.getSdt());
				stmtNV.setString(4, nv.getChucVu());
				stmtNV.setString(5, nv.getTrangThaiLamViec());
				stmtNV.setString(6, nv.getMaNV());
				stmtNV.executeUpdate();
			}
			
			// 2. Update TAIKHOAN
			try (PreparedStatement stmtTK = conn.prepareStatement(sqlTK)) {
				stmtTK.setString(1, nv.getVaiTro());
				stmtTK.setString(2, accountStatus);
				stmtTK.setString(3, nv.getMaNV());
				stmtTK.executeUpdate();
			}
			
			conn.commit();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (conn != null) {
				try { conn.rollback(); } catch (Exception ignored) {}
			}
			if (ex instanceof IllegalStateException) {
				throw (IllegalStateException) ex;
			}
			return false;
		} finally {
			if (conn != null) {
				try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
			}
		}
	}

	private void validateLastActiveManager(Connection conn, String maNV, String newRole, String newStatus)
			throws java.sql.SQLException {
		if (!isActiveManager(conn, maNV) || countActiveManagers(conn) > 1) {
			return;
		}
		boolean stillActiveManager = "Quan ly".equalsIgnoreCase(newRole)
				&& "Hoat dong".equalsIgnoreCase(newStatus);
		if (!stillActiveManager) {
			throw new IllegalStateException(
					"Khong the doi quyen hoac khoa tai khoan quan ly cuoi cung cua he thong.");
		}
	}

	private boolean isActiveManager(Connection conn, String maNV) throws java.sql.SQLException {
		String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE MaNV = ? AND VaiTro = 'Quan ly' AND TrangThai = 'Hoat dong'";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maNV);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private int countActiveManagers(Connection conn) throws java.sql.SQLException {
		String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE VaiTro = 'Quan ly' AND TrangThai = 'Hoat dong'";
		try (PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}
}

