 
package com.fashionstore.controller;

import com.fashionstore.dao.TaiKhoanDAO;

import com.fashionstore.model.TaiKhoan;

public class AuthController {
	private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

	public TaiKhoan login(String username, String password) {
		return taiKhoanDAO.login(username, password);
	}

	public boolean changePassword(String username, String currentPassword, String newPassword) {
		return taiKhoanDAO.changePassword(username, currentPassword, newPassword);
	}
}

