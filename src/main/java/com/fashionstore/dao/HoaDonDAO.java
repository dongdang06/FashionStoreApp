 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.HoaDonSummary;

public class HoaDonDAO {
	public List<HoaDonSummary> getRecentInvoices(int limit) {
		String sql = "SELECT hd.MaHD, hd.MaDH, hd.NgayXuat, hd.TongTienHD, "
				+ "hd.PhuongThucTT, nv.HoTen "
				+ "FROM HOADON hd "
				+ "LEFT JOIN NHANVIEN nv ON nv.MaNV = hd.MaNV "
				+ "ORDER BY hd.NgayXuat DESC "
				+ "FETCH FIRST ? ROWS ONLY";
		List<HoaDonSummary> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					HoaDonSummary summary = new HoaDonSummary(
							rs.getString("MaHD"),
							rs.getString("MaDH"),
							rs.getDate("NgayXuat"),
							rs.getLong("TongTienHD"),
							rs.getString("PhuongThucTT"),
							rs.getString("HoTen"));
					results.add(summary);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}

	public String getInvoiceHTML(String maHD) {
		String sqlMain = "SELECT hd.MaHD, hd.NgayXuat, hd.TongTienHD, hd.PhuongThucTT, nv.HoTen AS TenNV, "
				+ "dh.MaKH, dh.DiemNhanDuoc, dh.DiemSuDung "
				+ "FROM HOADON hd "
				+ "JOIN DONHANG dh ON hd.MaDH = dh.MaDH "
				+ "LEFT JOIN NHANVIEN nv ON hd.MaNV = nv.MaNV "
				+ "WHERE hd.MaHD = ?";

		String sqlItems = "SELECT sp.TenSP, bt.MauSac, bt.KichThuoc, ctdh.SoLuong, ctdh.GiaBanLucMua "
				+ "FROM HOADON hd "
				+ "JOIN CHITIETDONHANG ctdh ON hd.MaDH = ctdh.MaDH "
				+ "JOIN BIENTHESANPHAM bt ON ctdh.MaBienThe = bt.MaBienThe "
				+ "JOIN SANPHAM sp ON bt.MaSP = sp.MaSP "
				+ "WHERE hd.MaHD = ?";

		StringBuilder html = new StringBuilder();
		html.append("<html><head><style>")
			.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 15px; color: #333; }")
			.append(".header { text-align: center; border-bottom: 2px dashed #999; padding-bottom: 8px; }")
			.append(".title { font-size: 18px; font-weight: bold; }")
			.append(".info { margin: 12px 0; font-size: 12px; line-height: 1.5; }")
			.append(".table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
			.append(".table th, .table td { padding: 6px; text-align: left; font-size: 11px; }")
			.append(".table th { border-bottom: 1px solid #333; }")
			.append(".table td { border-bottom: 1px dashed #ccc; }")
			.append(".right { text-align: right; }")
			.append(".total-section { margin-top: 15px; border-top: 2px dashed #999; padding-top: 8px; font-size: 12px; font-weight: bold; }")
			.append(".footer { text-align: center; margin-top: 25px; font-size: 11px; font-style: italic; color: #666; }")
			.append("</style></head><body>");

		try (Connection conn = DBConnection.getInstance().getConnection()) {
			String ngayXuat = "";
			long tongTien = 0;
			String phuongThuc = "";
			String nhanVien = "";
			String maKH = "";
			int diemNhan = 0;
			int diemDung = 0;

			try (PreparedStatement stmt = conn.prepareStatement(sqlMain)) {
				stmt.setString(1, maHD);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						ngayXuat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(rs.getTimestamp("NgayXuat"));
						tongTien = rs.getLong("TongTienHD");
						phuongThuc = rs.getString("PhuongThucTT");
						nhanVien = rs.getString("TenNV");
						maKH = rs.getString("MaKH");
						diemNhan = rs.getInt("DiemNhanDuoc");
						diemDung = rs.getInt("DiemSuDung");
					} else {
						return "<html><body><h3 align='center'>Không tìm thấy dữ liệu hóa đơn!</h3></body></html>";
					}
				}
			}

			// Render Header
			html.append("<div class='header'>")
				.append("<div class='title'>FASHION STORE</div>")
				.append("<div>Địa chỉ: Cầu Giấy, Hà Nội</div>")
				.append("<div>Điện thoại: 0987.654.321</div>")
				.append("</div>");

			// Render Info
			html.append("<div class='info'>")
				.append("<b>Mã hóa đơn:</b> ").append(maHD).append("<br>")
				.append("<b>Ngày xuất:</b> ").append(ngayXuat).append("<br>")
				.append("<b>Nhân viên:</b> ").append(nhanVien != null ? nhanVien : "N/A").append("<br>")
				.append("<b>Khách hàng:</b> ").append(maKH != null ? maKH : "Khách vãng lai").append("<br>");
			if (maKH != null) {
				html.append("<b>Điểm sử dụng:</b> ").append(diemDung).append("<br>")
					.append("<b>Điểm nhận thêm:</b> +").append(diemNhan).append(" điểm<br>");
			}
			html.append("</div>");

			// Render Items Table
			html.append("<table class='table'>")
				.append("<tr><th>Sản phẩm</th><th>SL</th><th class='right'>Giá</th><th class='right'>T.Tiền</th></tr>");

			java.text.NumberFormat fmt = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));

			try (PreparedStatement stmt = conn.prepareStatement(sqlItems)) {
				stmt.setString(1, maHD);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						String ten = rs.getString("TenSP") + " (" + rs.getString("MauSac") + " - " + rs.getString("KichThuoc") + ")";
						int sl = rs.getInt("SoLuong");
						long gia = rs.getLong("GiaBanLucMua");
						long tt = sl * gia;

						html.append("<tr>")
							.append("<td>").append(ten).append("</td>")
							.append("<td>").append(sl).append("</td>")
							.append("<td class='right'>").append(fmt.format(gia)).append("</td>")
							.append("<td class='right'>").append(fmt.format(tt)).append("</td>")
							.append("</tr>");
					}
				}
			}
			html.append("</table>");

			// Render Total Section
			html.append("<div class='total-section'>")
				.append("<table style='width:100%'>")
				.append("<tr><td>Tổng thanh toán:</td><td class='right' style='color:red; font-size:14px;'>").append(fmt.format(tongTien)).append(" đ</td></tr>")
				.append("<tr><td style='font-size:10px; font-weight:normal;'>Hình thức:</td><td class='right' style='font-size:10px; font-weight:normal;'>").append(phuongThuc.equals("Tien mat") ? "Tiền mặt" : "Chuyển khoản").append("</td></tr>")
				.append("</table>")
				.append("</div>");

			// Render Footer
			html.append("<div class='footer'>")
				.append("<p>Cảm ơn quý khách đã mua sắm!</p>")
				.append("<p>Hẹn gặp lại quý khách!</p>")
				.append("</div>");

		} catch (Exception ex) {
			ex.printStackTrace();
			return "<html><body><h3>Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage() + "</h3></body></html>";
		}

		html.append("</body></html>");
		return html.toString();
	}
}
