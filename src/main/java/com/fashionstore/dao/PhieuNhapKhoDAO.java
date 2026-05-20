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
import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.NhaCungCap;
import com.fashionstore.model.NhanVien;
import com.fashionstore.model.PhieuNhapKho;
import com.fashionstore.util.MockData;

public class PhieuNhapKhoDAO {
	private static final List<PhieuNhapKho> MOCK_RECEIPTS = new ArrayList<>();
	private static final Map<String, List<ChiTietPhieuNhap>> MOCK_DETAILS = new HashMap<>();
	private static boolean mockInitialized;

	public List<PhieuNhapKho> getAll() {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockAll();
		}
		String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP ORDER BY MaPN";
		List<PhieuNhapKho> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				results.add(mapReceipt(rs));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (DBConnection.getInstance().isMockMode()) {
				return getMockAll();
			}
		}
		return results;
	}

	public List<PhieuNhapKho> search(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return getAll();
		}
		if (DBConnection.getInstance().isMockMode()) {
			return searchMock(keyword);
		}
		String sql = "SELECT DISTINCT pn.MaPN, pn.NgayNhap, pn.TongGiaTri, pn.MaNCC, pn.MaNV "
				+ "FROM PHIEUNHAP pn LEFT JOIN CHITIETPHIEUNHAP ct ON pn.MaPN = ct.MaPN "
				+ "WHERE LOWER(pn.MaPN) LIKE ? OR LOWER(pn.MaNCC) LIKE ? "
				+ "OR LOWER(pn.MaNV) LIKE ? OR LOWER(ct.MaBienThe) LIKE ? "
				+ "OR TO_CHAR(pn.NgayNhap, 'DD/MM/YYYY') LIKE ? "
				+ "ORDER BY pn.MaPN";
		List<PhieuNhapKho> results = new ArrayList<>();
		String value = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 1; i <= 5; i++) {
				stmt.setString(i, value);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					results.add(mapReceipt(rs));
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

	public PhieuNhapKho getById(String maPN) {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockById(maPN);
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getById(conn, maPN);
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				return getMockById(maPN);
			}
			throw new RuntimeException("Khong the tai phieu nhap kho: " + ex.getMessage(), ex);
		}
	}

	public List<ChiTietPhieuNhap> getDetails(String maPN) {
		if (DBConnection.getInstance().isMockMode()) {
			return getMockDetails(maPN);
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			return getDetails(conn, maPN);
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				return getMockDetails(maPN);
			}
			throw new RuntimeException("Khong the tai chi tiet phieu nhap: " + ex.getMessage(), ex);
		}
	}

	public void create(PhieuNhapKho receipt) {
		validateReceipt(receipt);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				if (exists(conn, "PHIEUNHAP", "MaPN", receipt.getMaPN())) {
					throw new IllegalArgumentException("Ma phieu nhap da ton tai.");
				}
				validateReferences(conn, receipt);
				insertHeader(conn, receipt);
				insertDetails(conn, receipt.getChiTietList());
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				createMock(receipt);
				return;
			}
			throw asRuntime("Khong the them phieu nhap kho", ex);
		}
	}

	public void update(PhieuNhapKho receipt) {
		validateReceipt(receipt);
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				PhieuNhapKho current = getById(conn, receipt.getMaPN());
				if (current == null) {
					throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
				}
				validateReferences(conn, receipt);
				List<ChiTietPhieuNhap> oldDetails = getDetails(conn, receipt.getMaPN());
				validateNhapStockAfterChange(conn, oldDetails, receipt.getChiTietList());
				deleteDetails(conn, receipt.getMaPN());
				updateHeader(conn, receipt);
				insertDetails(conn, receipt.getChiTietList());
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				updateMock(receipt);
				return;
			}
			throw asRuntime("Khong the cap nhat phieu nhap kho", ex);
		}
	}

	public void delete(String maPN) {
		if (maPN == null || maPN.trim().isEmpty()) {
			throw new IllegalArgumentException("Ma phieu nhap la bat buoc.");
		}
		try (Connection conn = DBConnection.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			try {
				List<ChiTietPhieuNhap> oldDetails = getDetails(conn, maPN);
				validateNhapStockAfterChange(conn, oldDetails, new ArrayList<>());
				deleteDetails(conn, maPN);
				try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM PHIEUNHAP WHERE MaPN = ?")) {
					stmt.setString(1, maPN);
					if (stmt.executeUpdate() == 0) {
						throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
					}
				}
				conn.commit();
			} catch (Exception ex) {
				rollback(conn);
				throw ex;
			}
		} catch (Exception ex) {
			if (DBConnection.getInstance().isMockMode()) {
				deleteMock(maPN);
				return;
			}
			throw asRuntime("Khong the xoa phieu nhap kho", ex);
		}
	}

	private PhieuNhapKho mapReceipt(ResultSet rs) throws SQLException {
		return new PhieuNhapKho(
				rs.getString("MaPN"),
				rs.getDate("NgayNhap"),
				rs.getLong("TongGiaTri"),
				rs.getString("MaNCC"),
				rs.getString("MaNV"));
	}

	private PhieuNhapKho getById(Connection conn, String maPN) throws SQLException {
		String sql = "SELECT MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV FROM PHIEUNHAP WHERE MaPN = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPN);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				PhieuNhapKho receipt = mapReceipt(rs);
				receipt.setChiTietList(getDetails(conn, maPN));
				return receipt;
			}
		}
	}

	private List<ChiTietPhieuNhap> getDetails(Connection conn, String maPN) throws SQLException {
		String sql = "SELECT MaPN, MaBienThe, SoLuongNhap, GiaNhap "
				+ "FROM CHITIETPHIEUNHAP WHERE MaPN = ? ORDER BY MaBienThe";
		List<ChiTietPhieuNhap> details = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, maPN);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					details.add(new ChiTietPhieuNhap(
							rs.getString("MaPN"),
							rs.getString("MaBienThe"),
							rs.getInt("SoLuongNhap"),
							rs.getLong("GiaNhap")));
				}
			}
		}
		return details;
	}

	private void insertHeader(Connection conn, PhieuNhapKho receipt) throws SQLException {
		String sql = "INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV) VALUES (?, ?, 0, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, receipt.getMaPN());
			stmt.setDate(2, toSqlDate(receipt.getNgayNhap()));
			stmt.setString(3, receipt.getMaNCC());
			stmt.setString(4, receipt.getMaNV());
			stmt.executeUpdate();
		}
	}

	private void updateHeader(Connection conn, PhieuNhapKho receipt) throws SQLException {
		String sql = "UPDATE PHIEUNHAP SET NgayNhap = ?, MaNCC = ?, MaNV = ? WHERE MaPN = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setDate(1, toSqlDate(receipt.getNgayNhap()));
			stmt.setString(2, receipt.getMaNCC());
			stmt.setString(3, receipt.getMaNV());
			stmt.setString(4, receipt.getMaPN());
			stmt.executeUpdate();
		}
	}

	private void insertDetails(Connection conn, List<ChiTietPhieuNhap> details) throws SQLException {
		String sql = "INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (ChiTietPhieuNhap detail : details) {
				stmt.setString(1, detail.getMaPN());
				stmt.setString(2, detail.getMaBienThe());
				stmt.setInt(3, detail.getSoLuongNhap());
				stmt.setLong(4, detail.getGiaNhap());
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	private void deleteDetails(Connection conn, String maPN) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM CHITIETPHIEUNHAP WHERE MaPN = ?")) {
			stmt.setString(1, maPN);
			stmt.executeUpdate();
		}
	}

	private void validateReferences(Connection conn, PhieuNhapKho receipt) throws SQLException {
		if (!exists(conn, "NHACUNGCAP", "MaNCC", receipt.getMaNCC())) {
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		}
		if (!exists(conn, "NHANVIEN", "MaNV", receipt.getMaNV())) {
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		}
		for (ChiTietPhieuNhap detail : receipt.getChiTietList()) {
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

	private void validateNhapStockAfterChange(Connection conn, List<ChiTietPhieuNhap> oldDetails,
			List<ChiTietPhieuNhap> newDetails) throws SQLException {
		Map<String, Integer> oldMap = aggregateNhap(oldDetails);
		Map<String, Integer> newMap = aggregateNhap(newDetails);
		Set<String> variants = new HashSet<>();
		variants.addAll(oldMap.keySet());
		variants.addAll(newMap.keySet());
		for (String maBienThe : variants) {
			int finalStock = getStock(conn, maBienThe) - oldMap.getOrDefault(maBienThe, 0)
					+ newMap.getOrDefault(maBienThe, 0);
			if (finalStock < 0) {
				throw new IllegalArgumentException("Ton kho se am sau khi cap nhat bien the " + maBienThe + ".");
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

	private Map<String, Integer> aggregateNhap(List<ChiTietPhieuNhap> details) {
		Map<String, Integer> result = new HashMap<>();
		for (ChiTietPhieuNhap detail : details) {
			result.put(detail.getMaBienThe(),
					result.getOrDefault(detail.getMaBienThe(), 0) + detail.getSoLuongNhap());
		}
		return result;
	}

	private void validateReceipt(PhieuNhapKho receipt) {
		if (receipt == null) {
			throw new IllegalArgumentException("Phieu nhap khong hop le.");
		}
		receipt.setMaPN(require(receipt.getMaPN(), "Ma phieu nhap"));
		receipt.setMaNCC(require(receipt.getMaNCC(), "Ma nha cung cap"));
		receipt.setMaNV(require(receipt.getMaNV(), "Ma nhan vien"));
		if (receipt.getNgayNhap() == null) {
			receipt.setNgayNhap(new Date());
		}
		List<ChiTietPhieuNhap> details = receipt.getChiTietList();
		if (details.isEmpty()) {
			throw new IllegalArgumentException("Phieu nhap phai co it nhat mot san pham.");
		}
		Set<String> variants = new HashSet<>();
		long total = 0;
		for (ChiTietPhieuNhap detail : details) {
			detail.setMaPN(receipt.getMaPN());
			detail.setMaBienThe(require(detail.getMaBienThe(), "Ma bien the"));
			if (!variants.add(detail.getMaBienThe())) {
				throw new IllegalArgumentException("Bien the bi trung trong phieu nhap: " + detail.getMaBienThe());
			}
			if (detail.getSoLuongNhap() <= 0) {
				throw new IllegalArgumentException("So luong nhap phai lon hon 0.");
			}
			if (detail.getGiaNhap() <= 0) {
				throw new IllegalArgumentException("Gia nhap phai lon hon 0.");
			}
			total += detail.getThanhTien();
		}
		receipt.setTongGiaTri(total);
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
		MOCK_RECEIPTS.addAll(MockData.getPhieuNhapList());
		MOCK_DETAILS.put("PN-01", new ArrayList<ChiTietPhieuNhap>());
		MOCK_DETAILS.get("PN-01").add(new ChiTietPhieuNhap("PN-01", "BT-001", 10, 200000L));
		MOCK_DETAILS.get("PN-01").add(new ChiTietPhieuNhap("PN-01", "BT-002", 15, 100000L));
		MOCK_DETAILS.put("PN-02", new ArrayList<ChiTietPhieuNhap>());
		MOCK_DETAILS.get("PN-02").add(new ChiTietPhieuNhap("PN-02", "BT-003", 5, 400000L));
		MOCK_DETAILS.get("PN-02").add(new ChiTietPhieuNhap("PN-02", "BT-001", 1, 200000L));
		refreshMockTotals();
		mockInitialized = true;
	}

	private List<PhieuNhapKho> getMockAll() {
		initMockData();
		List<PhieuNhapKho> copies = new ArrayList<>();
		for (PhieuNhapKho receipt : MOCK_RECEIPTS) {
			copies.add(copyMockReceipt(receipt));
		}
		return copies;
	}

	private List<PhieuNhapKho> searchMock(String keyword) {
		initMockData();
		String value = keyword.trim().toLowerCase(Locale.ROOT);
		List<PhieuNhapKho> results = new ArrayList<>();
		for (PhieuNhapKho receipt : MOCK_RECEIPTS) {
			if (contains(receipt.getMaPN(), value) || contains(receipt.getMaNCC(), value)
					|| contains(receipt.getMaNV(), value) || contains(String.valueOf(receipt.getTongGiaTri()), value)
					|| mockDetailsContain(receipt.getMaPN(), value)) {
				results.add(copyMockReceipt(receipt));
			}
		}
		return results;
	}

	private PhieuNhapKho getMockById(String maPN) {
		initMockData();
		for (PhieuNhapKho receipt : MOCK_RECEIPTS) {
			if (receipt.getMaPN().equals(maPN)) {
				return copyMockReceipt(receipt);
			}
		}
		return null;
	}

	private List<ChiTietPhieuNhap> getMockDetails(String maPN) {
		initMockData();
		List<ChiTietPhieuNhap> details = MOCK_DETAILS.getOrDefault(maPN, new ArrayList<>());
		List<ChiTietPhieuNhap> copies = new ArrayList<>();
		for (ChiTietPhieuNhap detail : details) {
			copies.add(new ChiTietPhieuNhap(detail.getMaPN(), detail.getMaBienThe(),
					detail.getSoLuongNhap(), detail.getGiaNhap()));
		}
		return copies;
	}

	private void createMock(PhieuNhapKho receipt) {
		initMockData();
		validateReceipt(receipt);
		if (getMockById(receipt.getMaPN()) != null) {
			throw new IllegalArgumentException("Ma phieu nhap da ton tai.");
		}
		validateMockReferences(receipt);
		MOCK_RECEIPTS.add(copyHeader(receipt));
		MOCK_DETAILS.put(receipt.getMaPN(), getDetailCopies(receipt.getChiTietList()));
		refreshMockTotals();
	}

	private void updateMock(PhieuNhapKho receipt) {
		initMockData();
		validateReceipt(receipt);
		validateMockReferences(receipt);
		for (int i = 0; i < MOCK_RECEIPTS.size(); i++) {
			if (MOCK_RECEIPTS.get(i).getMaPN().equals(receipt.getMaPN())) {
				MOCK_RECEIPTS.set(i, copyHeader(receipt));
				MOCK_DETAILS.put(receipt.getMaPN(), getDetailCopies(receipt.getChiTietList()));
				refreshMockTotals();
				return;
			}
		}
		throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
	}

	private void deleteMock(String maPN) {
		initMockData();
		if (!MOCK_RECEIPTS.removeIf(item -> item.getMaPN().equals(maPN))) {
			throw new IllegalArgumentException("Phieu nhap kho khong ton tai.");
		}
		MOCK_DETAILS.remove(maPN);
	}

	private void validateMockReferences(PhieuNhapKho receipt) {
		if (!mockSupplierExists(receipt.getMaNCC())) {
			throw new IllegalArgumentException("Nha cung cap khong ton tai trong CSDL.");
		}
		if (!mockEmployeeExists(receipt.getMaNV())) {
			throw new IllegalArgumentException("Nhan vien khong ton tai trong CSDL.");
		}
		for (ChiTietPhieuNhap detail : receipt.getChiTietList()) {
			BienTheSanPham variant = findMockVariant(detail.getMaBienThe());
			if (variant == null) {
				throw new IllegalArgumentException("Bien the san pham khong ton tai: " + detail.getMaBienThe());
			}
			if (detail.getGiaNhap() >= variant.getGiaBan()) {
				throw new IllegalArgumentException("Gia nhap phai nho hon gia ban hien tai.");
			}
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

	private void refreshMockTotals() {
		for (PhieuNhapKho receipt : MOCK_RECEIPTS) {
			long total = 0;
			for (ChiTietPhieuNhap detail : MOCK_DETAILS.getOrDefault(receipt.getMaPN(), new ArrayList<>())) {
				total += detail.getThanhTien();
			}
			receipt.setTongGiaTri(total);
		}
	}

	private boolean mockDetailsContain(String maPN, String value) {
		for (ChiTietPhieuNhap detail : MOCK_DETAILS.getOrDefault(maPN, new ArrayList<>())) {
			if (contains(detail.getMaBienThe(), value) || contains(String.valueOf(detail.getSoLuongNhap()), value)
					|| contains(String.valueOf(detail.getGiaNhap()), value)) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(String source, String value) {
		return source != null && source.toLowerCase(Locale.ROOT).contains(value);
	}

	private PhieuNhapKho copyMockReceipt(PhieuNhapKho receipt) {
		PhieuNhapKho copy = copyHeader(receipt);
		copy.setChiTietList(getMockDetails(receipt.getMaPN()));
		return copy;
	}

	private PhieuNhapKho copyHeader(PhieuNhapKho receipt) {
		Date date = receipt.getNgayNhap() == null ? null : new Date(receipt.getNgayNhap().getTime());
		return new PhieuNhapKho(receipt.getMaPN(), date, receipt.getTongGiaTri(),
				receipt.getMaNCC(), receipt.getMaNV());
	}

	private List<ChiTietPhieuNhap> getDetailCopies(List<ChiTietPhieuNhap> details) {
		List<ChiTietPhieuNhap> copies = new ArrayList<>();
		for (ChiTietPhieuNhap detail : details) {
			copies.add(new ChiTietPhieuNhap(detail.getMaPN(), detail.getMaBienThe(),
					detail.getSoLuongNhap(), detail.getGiaNhap()));
		}
		return copies;
	}
}
