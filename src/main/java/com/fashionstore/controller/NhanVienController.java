 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.NhanVienDAO;
import com.fashionstore.model.NhanVien;

public class NhanVienController {
	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

	public List<NhanVien> getAll() {
		return nhanVienDAO.getAll();
	}
}

