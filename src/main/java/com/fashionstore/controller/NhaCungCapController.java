 
package com.fashionstore.controller;

import java.util.List;

import com.fashionstore.dao.NhaCungCapDAO;
import com.fashionstore.model.NhaCungCap;

public class NhaCungCapController {
	private final NhaCungCapDAO nccDAO = new NhaCungCapDAO();

	public List<NhaCungCap> getAll() {
		return nccDAO.getAll();
	}

	public boolean add(NhaCungCap ncc) {
		return nccDAO.insert(ncc);
	}

	public boolean edit(NhaCungCap ncc) {
		return nccDAO.update(ncc);
	}

	public boolean remove(String maNCC) {
		return nccDAO.delete(maNCC);
	}
}
