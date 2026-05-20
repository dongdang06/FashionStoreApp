package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.ChiTietPhieuXuat;
import com.fashionstore.model.NhaCungCap;
import com.fashionstore.model.NhanVien;
import com.fashionstore.model.PhieuXuatTra;
import com.fashionstore.util.MockData;

public class PhieuXuatTraDAO {
	private static final List<PhieuXuatTra> MOCK_RETURNS = new ArrayList<>();
	private static final Map<String, List<ChiTietPhieuXuat>> MOCK_DETAILS = new HashMap<>();
	private static final Map<String, Integer> MOCK_STOCK = new HashMap<>();
	private static boolean mockInitialized;

	public List<PhieuXuatTra> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockAll();
		}
		String sql = "SELECT MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo "
				+ "FROM PHIEUXUATTRA ORDER BY MaPhieuTra";
		List<PhieuXuatTra> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				results.add(mapReturn(rs));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return getMockAll();
			}
		}
		return results;
	}

	public List<PhieuXuatTra> search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAll();
		}
		if (DBConnection.getInstance().isMockMode()) {
			return searchMock(keyword);
		}
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
			for (int i = 1; i <= 6; i++) {
				stmt.setString(i, value);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					results.add(mapReturn(rs));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return searchMock(keyword);
			}
		}
		return results;
	}

	public PhieuXuatTra getById(String maPhieuTra) {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockById(maPhieuTra);
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getById(conn, maPhieuTra);
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				return getMockById(maPhieuTra);
			}
			throw new RuntimeException("Khong the tai phieu xuat tra: " + ex.getMessage(), ex);
		}
	}

	public List<ChiTietPhieuXuat> getDetails(String maPhieuTra) {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockDetails(maPhieuTra);
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getDetails(conn, maPhieuTra);
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				return getMockDetails(maPhieuTra);
			}
			throw new RuntimeException("Khong the tai chi tiet phieu xuat tra: " + ex.getMessage(), ex);
		}
	}

	public void create(PhieuXuatTra returnNote) {
		validateReturn(returnNote);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				if (exists(conn, "PHIEUXUATTRA", "MaPhieuTra", returnNote.getMaPhieuTra())) {
					throw new IllegalArgumentException("Ma phieu xuat tra da ton tai.");
				}
				validateReferences(conn, returnNote);
				validateXuatStockAfterChange(conn, new ArrayList<>(), returnNote.getChiTietList());
				insertHeader(conn, returnNote);
				insertDetails(conn, returnNote.getChiTietList());
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				createMock(returnNote);
				return;
			}
			throw asRuntime("Khong the them phieu xuat tra", ex);
		}
	}

	public void update(PhieuXuatTra returnNote) {
		validateReturn(returnNote);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				PhieuXuatTra current = getById(conn, returnNote.getMaPhieuTra());
				if (current == null) {
					throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
				}
				validateReferences(conn, returnNote);
				List<ChiTietPhieuXuat> oldDetails = getDetails(conn, returnNote.getMaPhieuTra());
				validateXuatStockAfterChange(conn, oldDetails, returnNote.getChiTietList());
				adjustXuatStock(conn, oldDetails, 1);
				deleteDetails(conn, returnNote.getMaPhieuTra());
				updateHeader(conn, returnNote);
				insertDetails(conn, returnNote.getChiTietList());
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				updateMock(returnNote);
				return;
			}
			throw asRuntime("Khong the cap nhat phieu xuat tra", ex);
		}
	}

	public void delete(String maPhieuTra) {
		if (maPhieuTra == null || maPhieuTra.trim().isEmpty()) {
			throw new IllegalArgumentException("Ma phieu xuat tra la bat buoc.");
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				List<ChiTietPhieuXuat> oldDetails = getDetails(conn, maPhieuTra);
				adjustXuatStock(conn, oldDetails, 1);
				deleteDetails(conn, maPhieuTra);
				try (PreparedStatement stmt = conn.prepareStatement(
						"DELETE FROM PHIEUXUATTRA WHERE MaPhieuTra = ?")) {
					stmt.setString(1, maPhieuTra);
					if (stmt.executeUpdate() == 0) {
						throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
					}
				}
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				deleteMock(maPhieuTra);
				return;
			}
			throw asRuntime("Khong the xoa phieu xuat tra", ex);
		}
	}

	private PhieuXuatTra mapReturn(ResultSet rs) throws SQLException {
		return new PhieuXuatTra(
				rs.getString("MaPhieuTra"),
				rs.getString("MaNCC"),
				rs.getString("MaNV"),
				rs.getDate("NgayTra"),
				rs.getString("LyDo"));
	}

	private PhieuXuatTra getById(Connection conn, String maPhieuTra) throws SQLException {
		String sql = "SELECT MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo FROM PHIEUXUATTRA WHERE MaPhieuTra = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPhieuTra);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				PhieuXuatTra returnNote = mapReturn(rs);
				returnNote.setChiTietList(getDetails(conn, maPhieuTra));
				return returnNote;
			}
		}
	}

	private List<ChiTietPhieuXuat> getDetails(Connection conn, String maPhieuTra) throws SQLException {
		String sql = "SELECT MaPhieuTra, MaBienThe, SoLuong "
				+ "FROM CHITIETPHIEUXUATTRA WHERE MaPhieuTra = ? ORDER BY MaBienThe";
		List<ChiTietPhieuXuat> details = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPhieuTra);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					details.add(new ChiTietPhieuXuat(
							rs.getString("MaPhieuTra"),
							rs.getString("MaBienThe"),
							rs.getInt("SoLuong")));
				}
			}
		}
		return details;
	}

	private void insertHeader(Connection conn, PhieuXuatTra returnNote) throws SQLException {
		String sql = "INSERT INTO PHIEUXUATTRA (MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, returnNote.getMaPhieuTra());
			stmt.setString(2, returnNote.getMaNCC());
			stmt.setString(3, returnNote.getMaNV());
			stmt.setDate(4, toSqlDate(returnNote.getNgayTra()));
			stmt.setString(5, returnNote.getLyDo());
			stmt.executeUpdate();
		}
	}

	private void updateHeader(Connection conn, PhieuXuatTra returnNote) throws SQLException {
		String sql = "UPDATE PHIEUXUATTRA SET MaNCC = ?, MaNV = ?, NgayTra = ?, LyDo = ? WHERE MaPhieuTra = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, returnNote.getMaNCC());
			stmt.setString(2, returnNote.getMaNV());
			stmt.setDate(3, toSqlDate(returnNote.getNgayTra()));
			stmt.setString(4, returnNote.getLyDo());
			stmt.setString(5, returnNote.getMaPhieuTra());
			stmt.executeUpdate();
		}
	}

	private void insertDetails(Connection conn, List<ChiTietPhieuXuat> details) throws SQLException {
		String sql = "INSERT INTO CHITIETPHIEUXUATTRA (MaPhieuTra, MaBienThe, SoLuong) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (ChiTietPhieuXuat detail : details) {
				stmt.setString(1, detail.getMaPhieuTra());
				stmt.setString(2, detail.getMaBienThe());
				stmt.setInt(3, detail.getSoLuong());
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	private void deleteDetails(Connection conn, String maPhieuTra) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(
				"DELETE FROM CHITIETPHIEUXUATTRA WHERE MaPhieuTra = ?")) {
			stmt.setString(1, maPhieuTra);
			stmt.executeUpdate();
		}
	}

	private void validateReferences(Connection conn, PhieuXuatTra returnNote) throws SQLException {
		if (!exists(conn, "NHACUNGCAP", "MaNCC", returnNote.getMaNCC())) {
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		}
		if (!exists(conn, "NHANVIEN", "MaNV", returnNote.getMaNV())) {
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		}
		for (ChiTietPhieuXuat detail : returnNote.getChiTietList()) {
			if (!exists(conn, "BIENTHESANPHAM", "MaBienThe", detail.getMaBienThe())) {
				throw new IllegalArgumentException("Bien the san pham khong ton tai: " + detail.getMaBienThe());
			}
		}
	}

	private boolean exists(Connection conn, String table, String column, String value) throws SQLException {
		String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, value);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private void validateXuatStockAfterChange(Connection conn, List<ChiTietPhieuXuat> oldDetails,
			List<ChiTietPhieuXuat> newDetails) throws SQLException {
		Map<String, Integer> oldMap = aggregateXuat(oldDetails);
		Map<String, Integer> newMap = aggregateXuat(newDetails);
		Set<String> variants = new HashSet<>();
		variants.addAll(oldMap.keySet());
		variants.addAll(newMap.keySet());
		for (String maBienThe : variants) {
			int finalStock = getStock(conn, maBienThe) + oldMap.getOrDefault(maBienThe, 0)
					- newMap.getOrDefault(maBienThe, 0);
			if (finalStock < 0) {
				throw new IllegalArgumentException("So luong ton kho khong du de xuat tra bien the " + maBienThe + ".");
			}
		}
	}

	private int getStock(Connection conn, String maBienThe) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT SoLuongTon FROM BIENTHESANPHAM WHERE MaBienThe = ?")) {
			stmt.setString(1, maBienThe);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) {
					throw new IllegalArgumentException("Bien the san pham khong ton tai: " + maBienThe);
				}
				return rs.getInt("SoLuongTon");
			}
		}
	}

	private void adjustXuatStock(Connection conn, List<ChiTietPhieuXuat> details, int direction) throws SQLException {
		if (details.isEmpty()) {
			return;
		}
		String sql = "UPDATE BIENTHESANPHAM SET SoLuongTon = SoLuongTon + ? WHERE MaBienThe = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (ChiTietPhieuXuat detail : details) {
				stmt.setInt(1, detail.getSoLuong() * direction);
				stmt.setString(2, detail.getMaBienThe());
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	private Map<String, Integer> aggregateXuat(List<ChiTietPhieuXuat> details) {
		Map<String, Integer> result = new HashMap<>();
		for (ChiTietPhieuXuat detail : details) {
			result.put(detail.getMaBienThe(),
					result.getOrDefault(detail.getMaBienThe(), 0) + detail.getSoLuong());
		}
		return result;
	}

	private void validateReturn(PhieuXuatTra returnNote) {
		if (returnNote == null) {
			throw new IllegalArgumentException("Phieu xuat tra khong hop le.");
		}
		returnNote.setMaPhieuTra(require(returnNote.getMaPhieuTra(), "Ma phieu xuat tra"));
		returnNote.setMaNCC(require(returnNote.getMaNCC(), "Ma nha cung cap"));
		returnNote.setMaNV(require(returnNote.getMaNV(), "Ma nhan vien"));
		returnNote.setLyDo(require(returnNote.getLyDo(), "Ly do xuat tra"));
		if (returnNote.getNgayTra() == null) {
			returnNote.setNgayTra(new Date());
		}
		List<ChiTietPhieuXuat> details = returnNote.getChiTietList();
		if (details.isEmpty()) {
			throw new IllegalArgumentException("Phieu xuat tra phai co it nhat mot san pham.");
		}
		Set<String> variants = new HashSet<>();
		for (ChiTietPhieuXuat detail : details) {
			detail.setMaPhieuTra(returnNote.getMaPhieuTra());
			detail.setMaBienThe(require(detail.getMaBienThe(), "Ma bien the"));
			if (!variants.add(detail.getMaBienThe())) {
				throw new IllegalArgumentException("Bien the bi trung trong phieu xuat tra: " + detail.getMaBienThe());
			}
			if (detail.getSoLuong() <= 0) {
				throw new IllegalArgumentException("So luong xuat tra phai lon hon 0.");
			}
		}
	}

	private String require(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " la bat buoc.");
		}
		return value.trim();
	}

	private java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	private void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException ignored) {
		}
	}

	private RuntimeException asRuntime(String message, Exception ex) {
		if (ex instanceof RuntimeException) {
			return (RuntimeException) ex;
		}
		return new RuntimeException(message + ": " + ex.getMessage(), ex);
	}

	private synchronized void initMockData() {
		if (mockInitialized) {
			return;
		}
		MOCK_RETURNS.addAll(MockData.getPhieuXuatList());
		MOCK_DETAILS.put("PT-01", new ArrayList<ChiTietPhieuXuat>());
		MOCK_DETAILS.get("PT-01").add(new ChiTietPhieuXuat("PT-01", "BT-001", 2));
		MOCK_DETAILS.put("PT-02", new ArrayList<ChiTietPhieuXuat>());
		MOCK_DETAILS.get("PT-02").add(new ChiTietPhieuXuat("PT-02", "BT-003", 1));
		for (BienTheSanPham item : MockData.getBienTheList()) {
			MOCK_STOCK.put(item.getMaBienThe(), item.getSoLuongTon());
		}
		mockInitialized = true;
	}

	private List<PhieuXuatTra> getMockAll() {
		initMockData();
		List<PhieuXuatTra> copies = new ArrayList<>();
		for (PhieuXuatTra returnNote : MOCK_RETURNS) {
			copies.add(copyMockReturn(returnNote));
		}
		return copies;
	}

	private List<PhieuXuatTra> searchMock(String keyword) {
		initMockData();
		String value = keyword.trim().toLowerCase(Locale.ROOT);
		List<PhieuXuatTra> results = new ArrayList<>();
		for (PhieuXuatTra returnNote : MOCK_RETURNS) {
			if (contains(returnNote.getMaPhieuTra(), value) || contains(returnNote.getMaNCC(), value)
					|| contains(returnNote.getMaNV(), value) || contains(returnNote.getLyDo(), value)
					|| mockDetailsContain(returnNote.getMaPhieuTra(), value)) {
				results.add(copyMockReturn(returnNote));
			}
		}
		return results;
	}

	private PhieuXuatTra getMockById(String maPhieuTra) {
		initMockData();
		for (PhieuXuatTra returnNote : MOCK_RETURNS) {
			if (returnNote.getMaPhieuTra().equals(maPhieuTra)) {
				return copyMockReturn(returnNote);
			}
		}
		return null;
	}

	private List<ChiTietPhieuXuat> getMockDetails(String maPhieuTra) {
		initMockData();
		List<ChiTietPhieuXuat> details = MOCK_DETAILS.getOrDefault(maPhieuTra, new ArrayList<>());
		List<ChiTietPhieuXuat> copies = new ArrayList<>();
		for (ChiTietPhieuXuat detail : details) {
			copies.add(new ChiTietPhieuXuat(detail.getMaPhieuTra(), detail.getMaBienThe(), detail.getSoLuong()));
		}
		return copies;
	}

	private void createMock(PhieuXuatTra returnNote) {
		initMockData();
		validateReturn(returnNote);
		if (getMockById(returnNote.getMaPhieuTra()) != null) {
			throw new IllegalArgumentException("Ma phieu xuat tra da ton tai.");
		}
		validateMockReferences(returnNote);
		validateMockStock(new ArrayList<>(), returnNote.getChiTietList());
		applyMockStock(new ArrayList<>(), returnNote.getChiTietList());
		MOCK_RETURNS.add(copyHeader(returnNote));
		MOCK_DETAILS.put(returnNote.getMaPhieuTra(), getDetailCopies(returnNote.getChiTietList()));
	}

	private void updateMock(PhieuXuatTra returnNote) {
		initMockData();
		validateReturn(returnNote);
		validateMockReferences(returnNote);
		for (int i = 0; i < MOCK_RETURNS.size(); i++) {
			if (MOCK_RETURNS.get(i).getMaPhieuTra().equals(returnNote.getMaPhieuTra())) {
				List<ChiTietPhieuXuat> oldDetails = getMockDetails(returnNote.getMaPhieuTra());
				validateMockStock(oldDetails, returnNote.getChiTietList());
				applyMockStock(oldDetails, returnNote.getChiTietList());
				MOCK_RETURNS.set(i, copyHeader(returnNote));
				MOCK_DETAILS.put(returnNote.getMaPhieuTra(), getDetailCopies(returnNote.getChiTietList()));
				return;
			}
		}
		throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
	}

	private void deleteMock(String maPhieuTra) {
		initMockData();
		List<ChiTietPhieuXuat> oldDetails = getMockDetails(maPhieuTra);
		if (!MOCK_RETURNS.removeIf(item -> item.getMaPhieuTra().equals(maPhieuTra))) {
			throw new IllegalArgumentException("Phieu xuat tra khong ton tai.");
		}
		applyMockStock(oldDetails, new ArrayList<>());
		MOCK_DETAILS.remove(maPhieuTra);
	}

	private void validateMockReferences(PhieuXuatTra returnNote) {
		if (!mockSupplierExists(returnNote.getMaNCC())) {
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		}
		if (!mockEmployeeExists(returnNote.getMaNV())) {
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		}
		for (ChiTietPhieuXuat detail : returnNote.getChiTietList()) {
			if (findMockVariant(detail.getMaBienThe()) == null) {
				throw new IllegalArgumentException("Bien the san pham khong ton tai: " + detail.getMaBienThe());
			}
		}
	}

	private void validateMockStock(List<ChiTietPhieuXuat> oldDetails, List<ChiTietPhieuXuat> newDetails) {
		Map<String, Integer> oldMap = aggregateXuat(oldDetails);
		Map<String, Integer> newMap = aggregateXuat(newDetails);
		Set<String> variants = new HashSet<>();
		variants.addAll(oldMap.keySet());
		variants.addAll(newMap.keySet());
		for (String maBienThe : variants) {
			int stock = MOCK_STOCK.getOrDefault(maBienThe, 0);
			int finalStock = stock + oldMap.getOrDefault(maBienThe, 0) - newMap.getOrDefault(maBienThe, 0);
			if (finalStock < 0) {
				throw new IllegalArgumentException("So luong ton kho khong du de xuat tra bien the " + maBienThe + ".");
			}
		}
	}

	private void applyMockStock(List<ChiTietPhieuXuat> oldDetails, List<ChiTietPhieuXuat> newDetails) {
		for (ChiTietPhieuXuat detail : oldDetails) {
			MOCK_STOCK.put(detail.getMaBienThe(),
					MOCK_STOCK.getOrDefault(detail.getMaBienThe(), 0) + detail.getSoLuong());
		}
		for (ChiTietPhieuXuat detail : newDetails) {
			MOCK_STOCK.put(detail.getMaBienThe(),
					MOCK_STOCK.getOrDefault(detail.getMaBienThe(), 0) - detail.getSoLuong());
		}
	}

	private boolean mockSupplierExists(String maNCC) {
		for (NhaCungCap ncc : MockData.getNhaCungCapList()) {
			if (ncc.getMaNCC().equals(maNCC)) {
				return true;
			}
		}
		return false;
	}

	private boolean mockEmployeeExists(String maNV) {
		for (NhanVien nv : MockData.getNhanVienList()) {
			if (nv.getMaNV().equals(maNV)) {
				return true;
			}
		}
		return false;
	}

	private BienTheSanPham findMockVariant(String maBienThe) {
		for (BienTheSanPham item : MockData.getBienTheList()) {
			if (item.getMaBienThe().equals(maBienThe)) {
				return item;
			}
		}
		return null;
	}

	private boolean mockDetailsContain(String maPhieuTra, String value) {
		for (ChiTietPhieuXuat detail : MOCK_DETAILS.getOrDefault(maPhieuTra, new ArrayList<>())) {
			if (contains(detail.getMaBienThe(), value) || contains(String.valueOf(detail.getSoLuong()), value)) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(String source, String value) {
		return source != null && source.toLowerCase(Locale.ROOT).contains(value);
	}

	private PhieuXuatTra copyMockReturn(PhieuXuatTra returnNote) {
		PhieuXuatTra copy = copyHeader(returnNote);
		copy.setChiTietList(getMockDetails(returnNote.getMaPhieuTra()));
		return copy;
	}

	private PhieuXuatTra copyHeader(PhieuXuatTra returnNote) {
		Date date = returnNote.getNgayTra() == null ? null : new Date(returnNote.getNgayTra().getTime());
		return new PhieuXuatTra(returnNote.getMaPhieuTra(), returnNote.getMaNCC(),
				returnNote.getMaNV(), date, returnNote.getLyDo());
	}

	private List<ChiTietPhieuXuat> getDetailCopies(List<ChiTietPhieuXuat> details) {
		List<ChiTietPhieuXuat> copies = new ArrayList<>();
		for (ChiTietPhieuXuat detail : details) {
			copies.add(new ChiTietPhieuXuat(detail.getMaPhieuTra(), detail.getMaBienThe(), detail.getSoLuong()));
		}
		return copies;
	}
}
