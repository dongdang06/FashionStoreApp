 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.DanhMucSanPhamDAO;
import com.fashionstore.model.DanhMucSanPham;

public class DanhMucSanPhamController {
	private final DanhMucSanPhamDAO danhMucDAO = new DanhMucSanPhamDAO();

	public List<DanhMucSanPham> getAll() {
		return danhMucDAO.getAll();
	}
}

