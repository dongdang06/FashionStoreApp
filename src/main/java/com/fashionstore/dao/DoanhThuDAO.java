 
package com.fashionstore.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fashionstore.model.BaoCaoDoanhThu;

import oracle.jdbc.OracleTypes;

public class DoanhThuDAO {
	public long getCurrentMonthRevenue() {
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(SYSDATE, 'MM')";
		return queryLongValue(sql);
	}

	public long getLastMonthRevenue() {
		String sql = "SELECT NVL(SUM(TongTienHD), 0) "
				+ "FROM HOADON "
				+ "WHERE TRUNC(NgayXuat, 'MM') = TRUNC(ADD_MONTHS(SYSDATE, -1), 'MM')";
		return queryLongValue(sql);
	}

	/**
	 * Thống kê doanh thu sử dụng Stored Procedure PROC_ThongKeDoanhThu.
	 * Proc trả về SYS_REFCURSOR chứa: TieuChi, SoLuong, TongDoanhThu, TongChiPhi.
	 */
	public List<BaoCaoDoanhThu> getBaoCaoDoanhThu(String criteria, Date tuNgay, Date denNgay) {
		List<BaoCaoDoanhThu> list = new ArrayList<>();
		
		// Chuẩn hóa thời gian bắt đầu và kết thúc
		java.util.Calendar cal = java.util.Calendar.getInstance();
		if (tuNgay != null) {
			cal.setTime(tuNgay);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			tuNgay = cal.getTime();
		} else {
			cal.set(1970, 0, 1, 0, 0, 0);
			tuNgay = cal.getTime();
		}

		if (denNgay != null) {
			cal.setTime(denNgay);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
			cal.set(java.util.Calendar.MINUTE, 59);
			cal.set(java.util.Calendar.SECOND, 59);
			cal.set(java.util.Calendar.MILLISECOND, 999);
			denNgay = cal.getTime();
		} else {
			denNgay = new Date();
		}

		String call = "{ CALL PROC_ThongKeDoanhThu(?, ?, ?, ?) }";
		try (Connection conn = DBConnection.getInstance().getConnection();
				CallableStatement stmt = conn.prepareCall(call)) {
			// IN parameters
			stmt.setTimestamp(1, new java.sql.Timestamp(tuNgay.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(denNgay.getTime()));
			stmt.setString(3, criteria); // 'Theo ngày' hoặc 'Theo sản phẩm'
			// OUT parameter
			stmt.registerOutParameter(4, OracleTypes.CURSOR);

			stmt.execute();

			try (ResultSet rs = (ResultSet) stmt.getObject(4)) {
				while (rs.next()) {
					BaoCaoDoanhThu bc = new BaoCaoDoanhThu(
							rs.getString("TieuChi"),
							rs.getInt("SoLuong"),
							rs.getLong("TongDoanhThu"),
							rs.getLong("TongChiPhi"));
					list.add(bc);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	private long queryLongValue(String sql) {
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0L;
	}
}


