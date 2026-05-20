package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.KhuyenMaiDAO;
import com.fashionstore.model.KhuyenMai;

public class KhuyenMaiController {
	private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

	public List<KhuyenMai> getAll() {
		return khuyenMaiDAO.getAll();
	}

	public boolean save(KhuyenMai km) {
		return khuyenMaiDAO.save(km);
	}

	public boolean update(KhuyenMai km) {
		return khuyenMaiDAO.update(km);
	}

	public boolean delete(String maKM) {
		return khuyenMaiDAO.delete(maKM);
	}

	public KhuyenMai getById(String maKM) {
		return khuyenMaiDAO.getById(maKM);
	}
}
