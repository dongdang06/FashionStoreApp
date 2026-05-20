 
package com.fashionstore.controller;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.fashionstore.dao.PhieuNhapKhoDAO;
import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.PhieuNhapKho;

public class PhieuNhapKhoController {
	private final PhieuNhapKhoDAO phieuNhapDAO = new PhieuNhapKhoDAO();

	public List<PhieuNhapKho> getAll() {
		return phieuNhapDAO.getAll();
	}

	public List<PhieuNhapKho> search(String keyword) {
		return phieuNhapDAO.search(keyword);
	}

	public PhieuNhapKho getById(String maPN) {
		return phieuNhapDAO.getById(maPN);
	}

	public List<ChiTietPhieuNhap> getDetails(String maPN) {
		return phieuNhapDAO.getDetails(maPN);
	}

	public void create(PhieuNhapKho phieuNhap) {
		phieuNhapDAO.create(phieuNhap);
	}

	public void update(PhieuNhapKho phieuNhap) {
		phieuNhapDAO.update(phieuNhap);
	}

	public void delete(String maPN) {
		phieuNhapDAO.delete(maPN);
	}

	public String buildPrintPreview(String maPN) {
		PhieuNhapKho receipt = phieuNhapDAO.getById(maPN);
		if (receipt == null) {
			throw new IllegalArgumentException("Khong tim thay phieu nhap kho.");
		}
		List<ChiTietPhieuNhap> details = phieuNhapDAO.getDetails(maPN);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		StringBuilder builder = new StringBuilder();
		builder.append("PHIEU NHAP KHO\n");
		builder.append("Ma phieu: ").append(receipt.getMaPN()).append('\n');
		builder.append("Ngay nhap: ")
				.append(receipt.getNgayNhap() == null ? "" : dateFormat.format(receipt.getNgayNhap())).append('\n');
		builder.append("Ma nha cung cap: ").append(receipt.getMaNCC()).append('\n');
		builder.append("Ma nhan vien: ").append(receipt.getMaNV()).append("\n\n");
		builder.append(String.format("%-18s %10s %15s %15s%n", "Ma bien the", "So luong", "Gia nhap", "Thanh tien"));
		builder.append("----------------------------------------------------------------\n");
		long total = 0;
		for (ChiTietPhieuNhap detail : details) {
			total += detail.getThanhTien();
			builder.append(String.format("%-18s %10d %15s %15s%n",
					detail.getMaBienThe(),
					detail.getSoLuongNhap(),
					currency.format(detail.getGiaNhap()),
					currency.format(detail.getThanhTien())));
		}
		builder.append("----------------------------------------------------------------\n");
		builder.append("Tong gia tri: ").append(currency.format(total)).append(" VND\n");
		builder.append("\nNguoi lap phieu\n");
		builder.append("(Ky, ghi ro ho ten)\n");
		return builder.toString();
	}
}

