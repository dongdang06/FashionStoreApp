 
package com.fashionstore.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fashionstore.dao.PhieuXuatTraDAO;
import com.fashionstore.model.ChiTietPhieuXuat;
import com.fashionstore.model.PhieuXuatTra;

public class PhieuXuatTraController {
	private final PhieuXuatTraDAO phieuXuatTraDAO = new PhieuXuatTraDAO();

	public List<PhieuXuatTra> getAll() {
		return phieuXuatTraDAO.getAll();
	}

	public List<PhieuXuatTra> search(String keyword) {
		return phieuXuatTraDAO.search(keyword);
	}

	public PhieuXuatTra getById(String maPhieuTra) {
		return phieuXuatTraDAO.getById(maPhieuTra);
	}

	public List<ChiTietPhieuXuat> getDetails(String maPhieuTra) {
		return phieuXuatTraDAO.getDetails(maPhieuTra);
	}

	public void create(PhieuXuatTra phieuXuatTra) {
		phieuXuatTraDAO.create(phieuXuatTra);
	}

	public void update(PhieuXuatTra phieuXuatTra) {
		phieuXuatTraDAO.update(phieuXuatTra);
	}

	public void delete(String maPhieuTra) {
		phieuXuatTraDAO.delete(maPhieuTra);
	}

	public String buildPrintPreview(String maPhieuTra) {
		PhieuXuatTra returnNote = phieuXuatTraDAO.getById(maPhieuTra);
		if (returnNote == null) {
			throw new IllegalArgumentException("Khong tim thay phieu xuat tra.");
		}
		List<ChiTietPhieuXuat> details = phieuXuatTraDAO.getDetails(maPhieuTra);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder builder = new StringBuilder();
		builder.append("PHIEU XUAT TRA HANG\n");
		builder.append("Ma phieu: ").append(returnNote.getMaPhieuTra()).append('\n');
		builder.append("Ngay tra: ")
				.append(returnNote.getNgayTra() == null ? "" : dateFormat.format(returnNote.getNgayTra())).append('\n');
		builder.append("Ma nha cung cap: ").append(returnNote.getMaNCC()).append('\n');
		builder.append("Ma nhan vien: ").append(returnNote.getMaNV()).append('\n');
		builder.append("Ly do: ").append(returnNote.getLyDo()).append("\n\n");
		builder.append(String.format("%-18s %10s%n", "Ma bien the", "So luong"));
		builder.append("--------------------------------\n");
		int totalQuantity = 0;
		for (ChiTietPhieuXuat detail : details) {
			totalQuantity += detail.getSoLuong();
			builder.append(String.format("%-18s %10d%n", detail.getMaBienThe(), detail.getSoLuong()));
		}
		builder.append("--------------------------------\n");
		builder.append("Tong so luong: ").append(totalQuantity).append('\n');
		builder.append("\nNguoi lap phieu\n");
		builder.append("(Ky, ghi ro ho ten)\n");
		return builder.toString();
	}
}

