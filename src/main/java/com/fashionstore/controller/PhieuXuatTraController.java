 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.PhieuXuatTraDAO;
import com.fashionstore.model.PhieuXuatTra;

public class PhieuXuatTraController {
	private final PhieuXuatTraDAO phieuXuatTraDAO = new PhieuXuatTraDAO();

	public List<PhieuXuatTra> getAll() {
		return phieuXuatTraDAO.getAll();
	}
}

