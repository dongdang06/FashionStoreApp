 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.KhuyenMaiDAO;
import com.fashionstore.model.KhuyenMai;

public class KhuyenMaiController {
	private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

	public List<KhuyenMai> getAll() {
		return khuyenMaiDAO.getAll();
	}
}

