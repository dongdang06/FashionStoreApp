 
package com.fashionstore.controller;

import com.fashionstore.dao.TaiKhoanDAO;

public class AuthController {
	private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

	public boolean login(String username, String password) {
		return taiKhoanDAO.validateLogin(username, password);
	}
}

