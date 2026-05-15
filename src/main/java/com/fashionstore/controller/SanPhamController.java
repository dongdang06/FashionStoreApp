 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.SanPhamDAO;
import com.fashionstore.model.SanPham;

public class SanPhamController {
	private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

	public List<SanPham> getAll() {
		return sanPhamDAO.getAll();
	}
}

