 
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
		String sqlNV = "INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, NgayVaoLam, TrangThaiLamViec) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String sqlTK = "INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, VaiTro, NgayTao, TrangThai) VALUES (?, ?, ?, ?, ?, SYSDATE, 'Hoat dong')";
		
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			// 1. Insert into NHANVIEN
			try (PreparedStatement stmtNV = conn.prepareStatement(sqlNV)) {
				stmtNV.setString(1, nv.getMaNV());
				stmtNV.setString(2, nv.getHoTen());
				stmtNV.setString(3, nv.getEmail());
				stmtNV.setString(4, nv.getSdt());
				stmtNV.setString(5, nv.getChucVu());
				stmtNV.setDate(6, new java.sql.Date(nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().getTime() : System.currentTimeMillis()));
				stmtNV.setString(7, nv.getTrangThaiLamViec());
				stmtNV.executeUpdate();
			}
			
			// 2. Sinh MaTaiKhoan va Insert into TAIKHOAN
			String nextTK = getNextMaTaiKhoan(conn);
			try (PreparedStatement stmtTK = conn.prepareStatement(sqlTK)) {
				stmtTK.setString(1, nextTK);
				stmtTK.setString(2, nv.getMaNV());
				stmtTK.setString(3, nv.getMaNV());
				stmtTK.setString(4, "123456"); // Mật khẩu mặc định
				stmtTK.setString(5, nv.getVaiTro());
				stmtTK.executeUpdate();
			}
			
			conn.commit();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (conn != null) {
				try { conn.rollback(); } catch (Exception ignored) {}
			}
			return false;
		} finally {
			if (conn != null) {
				try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
			}
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

	private String getNextMaTaiKhoan(Connection conn) throws java.sql.SQLException {
		String sql = "SELECT MaTaiKhoan FROM TAIKHOAN";
		int maxId = 0;
		try (PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String idStr = rs.getString(1);
				if (idStr != null && idStr.startsWith("TK")) {
					try {
						int num = Integer.parseInt(idStr.substring(2));
						if (num > maxId) {
							maxId = num;
						}
					} catch (NumberFormatException ignored) {}
				}
			}
		}
		return String.format("TK%03d", maxId + 1);
	}
}

