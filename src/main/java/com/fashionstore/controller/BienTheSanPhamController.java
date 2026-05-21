 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.BienTheSanPhamDAO;
import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamController {
	private final BienTheSanPhamDAO bienTheDAO = new BienTheSanPhamDAO();

	public List<BienTheSanPham> getAll() {
		return bienTheDAO.getAll();
	}
	public boolean add(BienTheSanPham bt) {
		return bienTheDAO.insert(bt);
	}

	public boolean edit(BienTheSanPham bt) {
		return bienTheDAO.update(bt);
	}

	public boolean remove(String maBienThe) {
		return bienTheDAO.delete(maBienThe);
	}
}

