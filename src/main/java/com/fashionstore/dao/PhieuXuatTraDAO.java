package com.fashionstore.dao;

import java.sql.*;
import java.util.*;

import com.fashionstore.model.ChiTietPhieuXuat;
import com.fashionstore.model.PhieuXuatTra;

public class PhieuXuatTraDAO {

	public List<PhieuXuatTra> getAll() {
		String sql = "SELECT MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo FROM PHIEUXUATTRA ORDER BY MaPhieuTra";
		List<PhieuXuatTra> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) results.add(mapReturn(rs));
		} catch (Exception ex) { ex.printStackTrace(); }
		return results;
	}

	public List<PhieuXuatTra> search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) return getAll();
		String sql = "SELECT DISTINCT px.MaPhieuTra, px.MaNCC, px.MaNV, px.NgayTra, px.LyDo "
				+ "FROM PHIEUXUATTRA px LEFT JOIN CHITIETPHIEUXUATTRA ct ON px.MaPhieuTra = ct.MaPhieuTra "
				+ "WHERE LOWER(px.MaPhieuTra) LIKE ? OR LOWER(px.MaNCC) LIKE ? "
				+ "OR LOWER(px.MaNV) LIKE ? OR LOWER(px.LyDo) LIKE ? "
				+ "OR LOWER(ct.MaBienThe) LIKE ? OR TO_CHAR(px.NgayTra, 'DD/MM/YYYY') LIKE ? "
				+ "ORDER BY px.MaPhieuTra";
		List<PhieuXuatTra> results = new ArrayList<>();
		String value = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 1; i <= 6; i++) stmt.setString(i, value);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) results.add(mapReturn(rs));
			}
		} catch (Exception ex) { ex.printStackTrace(); }
		return results;
	}

	public PhieuXuatTra getById(String maPhieuTra) {
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getById(conn, maPhieuTra);
		} catch (Exception ex) {
			throw new RuntimeException("Khong the tai phieu xuat tra: " + ex.getMessage(), ex);
		}
	}

	public List<ChiTietPhieuXuat> getDetails(String maPhieuTra) {
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getDetails(conn, maPhieuTra);
		} catch (Exception ex) {
			throw new RuntimeException("Khong the tai chi tiet phieu xuat tra: " + ex.getMessage(), ex);
		}
	}

	public void create(PhieuXuatTra returnNote) {
		validateReturn(returnNote);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				if (exists(conn, "PHIEUXUATTRA", "MaPhieuTra", returnNote.getMaPhieuTra()))
					throw new IllegalArgumentException("Ma phieu xuat tra da ton tai.");
				validateReferences(conn, returnNote);
				validateXuatStockAfterChange(conn, new ArrayList<>(), returnNote.getChiTietList());
				insertHeader(conn, returnNote);
				insertDetails(conn, returnNote.getChiTietList());
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the them phieu xuat tra", ex); }
	}

	public void update(PhieuXuatTra returnNote) {
		validateReturn(returnNote);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				PhieuXuatTra current = getById(conn, returnNote.getMaPhieuTra());
				if (current == null) throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
				validateReferences(conn, returnNote);
				List<ChiTietPhieuXuat> oldDetails = getDetails(conn, returnNote.getMaPhieuTra());
				validateXuatStockAfterChange(conn, oldDetails, returnNote.getChiTietList());
				deleteDetails(conn, returnNote.getMaPhieuTra());
				updateHeader(conn, returnNote);
				insertDetails(conn, returnNote.getChiTietList());
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the cap nhat phieu xuat tra", ex); }
	}

	public void delete(String maPhieuTra) {
		if (maPhieuTra == null || maPhieuTra.trim().isEmpty())
			throw new IllegalArgumentException("Ma phieu xuat tra la bat buoc.");
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				List<ChiTietPhieuXuat> oldDetails = getDetails(conn, maPhieuTra);
				deleteDetails(conn, maPhieuTra);
				try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM PHIEUXUATTRA WHERE MaPhieuTra = ?")) {
					stmt.setString(1, maPhieuTra);
					if (stmt.executeUpdate() == 0) throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
				}
				conn.commit();
			} catch (Exception ex) { rollback(conn); throw ex; }
		} catch (Exception ex) { throw asRuntime("Khong the xoa phieu xuat tra", ex); }
	}

	private PhieuXuatTra mapReturn(ResultSet rs) throws SQLException {
		return new PhieuXuatTra(rs.getString("MaPhieuTra"), rs.getString("MaNCC"),
				rs.getString("MaNV"), rs.getDate("NgayTra"), rs.getString("LyDo"));
	}

	private PhieuXuatTra getById(Connection conn, String maPhieuTra) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo FROM PHIEUXUATTRA WHERE MaPhieuTra = ?")) {
			stmt.setString(1, maPhieuTra);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) return null;
				PhieuXuatTra r = mapReturn(rs);
				r.setChiTietList(getDetails(conn, maPhieuTra));
				return r;
			}
		}
	}

	private List<ChiTietPhieuXuat> getDetails(Connection conn, String maPhieuTra) throws SQLException {
		List<ChiTietPhieuXuat> details = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement("SELECT MaPhieuTra, MaBienThe, SoLuong FROM CHITIETPHIEUXUATTRA WHERE MaPhieuTra = ? ORDER BY MaBienThe")) {
			stmt.setString(1, maPhieuTra);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) details.add(new ChiTietPhieuXuat(rs.getString("MaPhieuTra"), rs.getString("MaBienThe"), rs.getInt("SoLuong")));
			}
		}
		return details;
	}

	private void insertHeader(Connection conn, PhieuXuatTra r) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO PHIEUXUATTRA (MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo) VALUES (?, ?, ?, ?, ?)")) {
			stmt.setString(1, r.getMaPhieuTra()); stmt.setString(2, r.getMaNCC()); stmt.setString(3, r.getMaNV());
			stmt.setDate(4, new java.sql.Date(r.getNgayTra().getTime())); stmt.setString(5, r.getLyDo()); stmt.executeUpdate();
		}
	}

	private void updateHeader(Connection conn, PhieuXuatTra r) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("UPDATE PHIEUXUATTRA SET MaNCC = ?, MaNV = ?, NgayTra = ?, LyDo = ? WHERE MaPhieuTra = ?")) {
			stmt.setString(1, r.getMaNCC()); stmt.setString(2, r.getMaNV());
			stmt.setDate(3, new java.sql.Date(r.getNgayTra().getTime())); stmt.setString(4, r.getLyDo());
			stmt.setString(5, r.getMaPhieuTra()); stmt.executeUpdate();
		}
	}

	private void insertDetails(Connection conn, List<ChiTietPhieuXuat> details) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO CHITIETPHIEUXUATTRA (MaPhieuTra, MaBienThe, SoLuong) VALUES (?, ?, ?)")) {
			for (ChiTietPhieuXuat d : details) {
				stmt.setString(1, d.getMaPhieuTra()); stmt.setString(2, d.getMaBienThe()); stmt.setInt(3, d.getSoLuong()); stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	private void deleteDetails(Connection conn, String maPhieuTra) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM CHITIETPHIEUXUATTRA WHERE MaPhieuTra = ?")) {
			stmt.setString(1, maPhieuTra); stmt.executeUpdate();
		}
	}

	private void validateReferences(Connection conn, PhieuXuatTra r) throws SQLException {
		if (!exists(conn, "NHACUNGCAP", "MaNCC", r.getMaNCC()))
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		if (!exists(conn, "NHANVIEN", "MaNV", r.getMaNV()))
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		for (ChiTietPhieuXuat d : r.getChiTietList())
			if (!exists(conn, "BIENTHESANPHAM", "MaBienThe", d.getMaBienThe()))
				throw new IllegalArgumentException("Bien the san pham khong ton tai: " + d.getMaBienThe());
	}

	private boolean exists(Connection conn, String table, String column, String value) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?")) {
			stmt.setString(1, value);
			try (ResultSet rs = stmt.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
		}
	}

	private void validateXuatStockAfterChange(Connection conn, List<ChiTietPhieuXuat> oldDetails, List<ChiTietPhieuXuat> newDetails) throws SQLException {
		Map<String, Integer> oldMap = new HashMap<>(), newMap = new HashMap<>();
		for (ChiTietPhieuXuat d : oldDetails) oldMap.merge(d.getMaBienThe(), d.getSoLuong(), Integer::sum);
		for (ChiTietPhieuXuat d : newDetails) newMap.merge(d.getMaBienThe(), d.getSoLuong(), Integer::sum);
		Set<String> variants = new HashSet<>(); variants.addAll(oldMap.keySet()); variants.addAll(newMap.keySet());
		for (String maBT : variants) {
			int finalStock = getStock(conn, maBT) + oldMap.getOrDefault(maBT, 0) - newMap.getOrDefault(maBT, 0);
			if (finalStock < 0) throw new IllegalArgumentException("So luong ton kho khong du de xuat tra bien the " + maBT + ".");
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

	private void validateReturn(PhieuXuatTra r) {
		if (r == null) throw new IllegalArgumentException("Phieu xuat tra khong hop le.");
		r.setMaPhieuTra(require(r.getMaPhieuTra(), "Ma phieu xuat tra"));
		r.setMaNCC(require(r.getMaNCC(), "Ma nha cung cap"));
		r.setMaNV(require(r.getMaNV(), "Ma nhan vien"));
		r.setLyDo(require(r.getLyDo(), "Ly do xuat tra"));
		if (r.getNgayTra() == null) r.setNgayTra(new java.util.Date());
		List<ChiTietPhieuXuat> details = r.getChiTietList();
		if (details.isEmpty()) throw new IllegalArgumentException("Phieu xuat tra phai co it nhat mot san pham.");
		Set<String> variants = new HashSet<>();
		for (ChiTietPhieuXuat d : details) {
			d.setMaPhieuTra(r.getMaPhieuTra()); d.setMaBienThe(require(d.getMaBienThe(), "Ma bien the"));
			if (!variants.add(d.getMaBienThe())) throw new IllegalArgumentException("Bien the bi trung: " + d.getMaBienThe());
			if (d.getSoLuong() <= 0) throw new IllegalArgumentException("So luong xuat tra phai lon hon 0.");
		}
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
