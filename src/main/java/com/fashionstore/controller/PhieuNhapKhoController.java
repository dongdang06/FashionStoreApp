 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.PhieuNhapKhoDAO;
import com.fashionstore.model.PhieuNhapKho;

public class PhieuNhapKhoController {
	private final PhieuNhapKhoDAO phieuNhapDAO = new PhieuNhapKhoDAO();

	public List<PhieuNhapKho> getAll() {
		return phieuNhapDAO.getAll();
	}
}

