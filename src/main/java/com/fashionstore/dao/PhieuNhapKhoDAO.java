package com.fashionstore.dao;

import java.sql.*;
import java.util.*;

import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.PhieuNhapKho;

public class PhieuNhapKhoDAO {

	public List<PhieuNhapKho> getAll() {
		String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP ORDER BY MaPN";
		List<PhieuNhapKho> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) { results.add(mapReceipt(rs)); }
		} catch (Exception ex) { ex.printStackTrace(); }
		return results;
	}

	public List<PhieuNhapKho> search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) return getAll();
		String sql = "SELECT DISTINCT pn.MaPN, pn.NgayNhap, pn.TongGiaTri, pn.MaNCC, pn.MaNV "
				+ "FROM PHIEUNHAP pn LEFT JOIN CHITIETPHIEUNHAP ct ON pn.MaPN = ct.MaPN "
				+ "WHERE LOWER(pn.MaPN) LIKE ? OR LOWER(pn.MaNCC) LIKE ? "
				+ "OR LOWER(pn.MaNV) LIKE ? OR LOWER(ct.MaBienThe) LIKE ? "
				+ "OR TO_CHAR(pn.NgayNhap, 'DD/MM/YYYY') LIKE ? ORDER BY pn.MaPN";
		List<PhieuNhapKho> results = new ArrayList<>();
		String value = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 1; i <= 5; i++) stmt.setString(i, value);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) results.add(mapReceipt(rs));
			}
		} catch (Exception ex) { ex.printStackTrace(); }
		return results;
	}

	public PhieuNhapKho getById(String maPN) {
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getById(conn, maPN);
		} catch (Exception ex) {
			throw new RuntimeException("Khong the tai phieu nhap kho: " + ex.getMessage(), ex);
		}
	}

	public List<ChiTietPhieuNhap> getDetails(String maPN) {
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getDetails(conn, maPN);
		} catch (Exception ex) {
			throw new RuntimeException("Khong the tai chi tiet phieu nhap: " + ex.getMessage(), ex);
		}
	}

	public void create(PhieuNhapKho receipt) {
		validateReceipt(receipt);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				if (exists(conn, "PHIEUNHAP", "MaPN", receipt.getMaPN()))
					throw new IllegalArgumentException("Ma phieu nhap da ton tai.");
				validateReferences(conn, receipt);
				insertHeader(conn, receipt);
				insertDetails(conn, receipt.getChiTietList());
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the them phieu nhap kho", ex); }
	}

	public void update(PhieuNhapKho receipt) {
		validateReceipt(receipt);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				PhieuNhapKho current = getById(conn, receipt.getMaPN());
				if (current == null) throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
				validateReferences(conn, receipt);
				List<ChiTietPhieuNhap> oldDetails = getDetails(conn, receipt.getMaPN());
				validateNhapStockAfterChange(conn, oldDetails, receipt.getChiTietList());
				deleteDetails(conn, receipt.getMaPN());
				updateHeader(conn, receipt);
				insertDetails(conn, receipt.getChiTietList());
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the cap nhat phieu nhap kho", ex); }
	}

	public void delete(String maPN) {
		if (maPN == null || maPN.trim().isEmpty())
			throw new IllegalArgumentException("Ma phieu nhap la bat buoc.");
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				List<ChiTietPhieuNhap> oldDetails = getDetails(conn, maPN);
				validateNhapStockAfterChange(conn, oldDetails, new ArrayList<>());
				deleteDetails(conn, maPN);
				try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM PHIEUNHAP WHERE MaPN = ?")) {
					stmt.setString(1, maPN);
					if (stmt.executeUpdate() == 0) throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
				}
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the xoa phieu nhap kho", ex); }
	}

	private PhieuNhapKho mapReceipt(ResultSet rs) throws SQLException {
		return new PhieuNhapKho(rs.getString("MaPN"), rs.getDate("NgayNhap"),
				rs.getLong("TongGiaTri"), rs.getString("MaNCC"), rs.getString("MaNV"));
	}

	private PhieuNhapKho getById(Connection conn, String maPN) throws SQLException {
		String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP WHERE MaPN = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPN);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) return null;
				PhieuNhapKho receipt = mapReceipt(rs);
				receipt.setChiTietList(getDetails(conn, maPN));
				return receipt;
			}
		}
	}

	private List<ChiTietPhieuNhap> getDetails(Connection conn, String maPN) throws SQLException {
		String sql = "SELECT MaPN, MaBienThe, SoLuongNhap, GiaNhap FROM CHITIETPHIEUNHAP WHERE MaPN = ? ORDER BY MaBienThe";
		List<ChiTietPhieuNhap> details = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPN);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) details.add(new ChiTietPhieuNhap(
						rs.getString("MaPN"), rs.getString("MaBienThe"), rs.getInt("SoLuongNhap"), rs.getLong("GiaNhap")));
			}
		}
		return details;
	}

	private void insertHeader(Connection conn, PhieuNhapKho r) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV) VALUES (?, ?, 0, ?, ?)")) {
			stmt.setString(1, r.getMaPN()); stmt.setDate(2, new java.sql.Date(r.getNgayNhap().getTime()));
			stmt.setString(3, r.getMaNCC()); stmt.setString(4, r.getMaNV()); stmt.executeUpdate();
		}
	}

	private void updateHeader(Connection conn, PhieuNhapKho r) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE PHIEUNHAP SET NgayNhap = ?, MaNCC = ?, MaNV = ? WHERE MaPN = ?")) {
			stmt.setDate(1, new java.sql.Date(r.getNgayNhap().getTime()));
			stmt.setString(2, r.getMaNCC()); stmt.setString(3, r.getMaNV()); stmt.setString(4, r.getMaPN()); stmt.executeUpdate();
		}
	}

	private void insertDetails(Connection conn, List<ChiTietPhieuNhap> details) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES (?, ?, ?, ?)")) {
			for (ChiTietPhieuNhap d : details) {
				stmt.setString(1, d.getMaPN()); stmt.setString(2, d.getMaBienThe());
				stmt.setInt(3, d.getSoLuongNhap()); stmt.setLong(4, d.getGiaNhap()); stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	private void deleteDetails(Connection conn, String maPN) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM CHITIETPHIEUNHAP WHERE MaPN = ?")) {
			stmt.setString(1, maPN); stmt.executeUpdate();
		}
	}

	private void validateReferences(Connection conn, PhieuNhapKho receipt) throws SQLException {
		if (!exists(conn, "NHACUNGCAP", "MaNCC", receipt.getMaNCC()))
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		if (!exists(conn, "NHANVIEN", "MaNV", receipt.getMaNV()))
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		for (ChiTietPhieuNhap d : receipt.getChiTietList())
			if (!exists(conn, "BIENTHESANPHAM", "MaBienThe", d.getMaBienThe()))
				throw new IllegalArgumentException("Bien the san pham khong ton tai: " + d.getMaBienThe());
	}

	private boolean exists(Connection conn, String table, String column, String value) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?")) {
			stmt.setString(1, value);
			try (ResultSet rs = stmt.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
		}
	}

	private void validateNhapStockAfterChange(Connection conn, List<ChiTietPhieuNhap> oldDetails, List<ChiTietPhieuNhap> newDetails) throws SQLException {
		Map<String, Integer> oldMap = new HashMap<>(), newMap = new HashMap<>();
		for (ChiTietPhieuNhap d : oldDetails) oldMap.merge(d.getMaBienThe(), d.getSoLuongNhap(), Integer::sum);
		for (ChiTietPhieuNhap d : newDetails) newMap.merge(d.getMaBienThe(), d.getSoLuongNhap(), Integer::sum);
		Set<String> variants = new HashSet<>(); variants.addAll(oldMap.keySet()); variants.addAll(newMap.keySet());
		for (String maBT : variants) {
			int finalStock = getStock(conn, maBT) - oldMap.getOrDefault(maBT, 0) + newMap.getOrDefault(maBT, 0);
			if (finalStock < 0) throw new IllegalArgumentException("Ton kho se am sau khi cap nhat bien the " + maBT + ".");
		}
	}

	private int getStock(Connection conn, String maBienThe) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT SoLuongTon FROM BIENTHESANPHAM WHERE MaBienThe = ?")) {
			stmt.setString(1, maBienThe);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) throw new IllegalArgumentException("Bien the san pham khong ton tai: " + maBienThe);
				return rs.getInt("SoLuongTon");
			}
		}
	}

	private void validateReceipt(PhieuNhapKho receipt) {
		if (receipt == null) throw new IllegalArgumentException("Phieu nhap khong hop le.");
		receipt.setMaPN(require(receipt.getMaPN(), "Ma phieu nhap"));
		receipt.setMaNCC(require(receipt.getMaNCC(), "Ma nha cung cap"));
		receipt.setMaNV(require(receipt.getMaNV(), "Ma nhan vien"));
		if (receipt.getNgayNhap() == null) receipt.setNgayNhap(new java.util.Date());
		List<ChiTietPhieuNhap> details = receipt.getChiTietList();
		if (details.isEmpty()) throw new IllegalArgumentException("Phieu nhap phai co it nhat mot san pham.");
		Set<String> variants = new HashSet<>(); long total = 0;
		for (ChiTietPhieuNhap d : details) {
			d.setMaPN(receipt.getMaPN()); d.setMaBienThe(require(d.getMaBienThe(), "Ma bien the"));
			if (!variants.add(d.getMaBienThe())) throw new IllegalArgumentException("Bien the bi trung: " + d.getMaBienThe());
			if (d.getSoLuongNhap() <= 0) throw new IllegalArgumentException("So luong nhap phai lon hon 0.");
			if (d.getGiaNhap() <= 0) throw new IllegalArgumentException("Gia nhap phai lon hon 0.");
			total += d.getThanhTien();
		}
		receipt.setTongGiaTri(total);
	}

	private String require(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(fieldName + " la bat buoc.");
		return value.trim();
	}

	private void rollback(Connection conn) { try { conn.rollback(); } catch (SQLException ignored) {} }

	private RuntimeException asRuntime(String msg, Exception ex) {
		return ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(msg + ": " + ex.getMessage(), ex);
	}
}
