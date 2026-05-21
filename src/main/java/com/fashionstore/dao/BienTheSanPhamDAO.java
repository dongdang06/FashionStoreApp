 
package com.fashionstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamDAO {
	public List<BienTheSanPham> getAll() {
		String sql = "SELECT bt.MaBienThe, bt.MaSP, bt.MauSac, bt.KichThuoc, bt.GiaBan, bt.SoLuongTon, latest.GiaNhap "
				+ "FROM BIENTHESANPHAM bt "
				+ "LEFT JOIN ( "
				+ "    SELECT MaBienThe, GiaNhap "
				+ "    FROM ( "
				+ "        SELECT ctpn.MaBienThe, ctpn.GiaNhap, "
				+ "               ROW_NUMBER() OVER (PARTITION BY ctpn.MaBienThe ORDER BY pn.NgayNhap DESC) as rn "
				+ "        FROM CHITIETPHIEUNHAP ctpn "
				+ "        JOIN PHIEUNHAP pn ON ctpn.MaPN = pn.MaPN "
				+ "    ) "
				+ "    WHERE rn = 1 "
				+ ") latest ON bt.MaBienThe = latest.MaBienThe "
				+ "ORDER BY bt.MaBienThe";
		List<BienTheSanPham> results = new ArrayList<>();
		try (Connection conn = DBConnection.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				BienTheSanPham item = new BienTheSanPham(
						rs.getString("MaBienThe"),
						rs.getString("MaSP"),
						rs.getString("MauSac"),
						rs.getString("KichThuoc"),
						rs.getLong("GiaBan"),
						rs.getInt("SoLuongTon"));
				item.setGiaNhap(rs.getLong("GiaNhap"));
				results.add(item);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}
