 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.BienTheSanPhamDAO;
import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamController {
	private final BienTheSanPhamDAO bienTheDAO = new BienTheSanPhamDAO();

	public List<BienTheSanPham> getAll() {
		return bienTheDAO.getAll();
	}
}

