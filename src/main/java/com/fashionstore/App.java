 
package com.fashionstore;

import javax.swing.SwingUtilities;

import com.fashionstore.view.auth.DangNhapFrame;

public class App {
	public static void main(String[] args) {
		// Run DB schema updates for promotions
		try (java.sql.Connection conn = com.fashionstore.dao.DBConnection.getInstance().getConnection();
				java.sql.Statement stmt = conn.createStatement()) {
			try {
				stmt.execute("ALTER TABLE KHUYENMAI DROP CONSTRAINT chk_km_trangthai");
			} catch (Exception ignored) {}
			stmt.execute("ALTER TABLE KHUYENMAI ADD CONSTRAINT chk_km_trangthai CHECK (TrangThaiKM IN ('Dang dien ra', 'Ket thuc', 'Chua bat dau'))");
			stmt.execute("CREATE OR REPLACE TRIGGER TRG_TuDongCapNhatTrangThaiKM "
					+ "BEFORE INSERT OR UPDATE ON KHUYENMAI "
					+ "FOR EACH ROW "
					+ "BEGIN "
					+ "    IF TRUNC(:NEW.NgayKetThuc) < TRUNC(SYSDATE) THEN "
					+ "        :NEW.TrangThaiKM := 'Ket thuc'; "
					+ "    ELSIF TRUNC(:NEW.NgayBatDau) > TRUNC(SYSDATE) THEN "
					+ "        :NEW.TrangThaiKM := 'Chua bat dau'; "
					+ "    ELSE "
					+ "        :NEW.TrangThaiKM := 'Dang dien ra'; "
					+ "    END IF; "
					+ "END;");

			// Clean up duplicate triggers in DB if they exist to prevent double inventory changes
			String[] duplicateTriggers = {"TRG_CONGTONKHO", "TRG_TRUKHOKHIXUATTRA", "TRG_CAPNHATKHO"};
			for (String trg : duplicateTriggers) {
				try {
					stmt.execute("DROP TRIGGER " + trg);
					System.out.println("Dropped legacy duplicate trigger: " + trg);
				} catch (Exception ignored) {}
			}
		} catch (Exception ex) {
			System.err.println("Migration failed: " + ex.getMessage());
		}

		SwingUtilities.invokeLater(() -> {
			DangNhapFrame frame = new DangNhapFrame();
			frame.setVisible(true);
		});
	}
}

