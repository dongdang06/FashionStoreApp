package com.fashionstore.view.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.fashionstore.controller.AuthController;
import com.fashionstore.model.TaiKhoan;
import com.fashionstore.util.SessionManager;

public class DoiMatKhauDialog {
	private DoiMatKhauDialog() {
	}

	public static void show(Component parent) {
		TaiKhoan currentUser = SessionManager.getCurrentUser();
		if (currentUser == null || currentUser.getUserName() == null) {
			JOptionPane.showMessageDialog(parent, "Không tìm thấy tài khoản đang đăng nhập.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPasswordField oldPassword = new JPasswordField();
		JPasswordField newPassword = new JPasswordField();
		JPasswordField confirmPassword = new JPasswordField();

		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		form.add(label("Mật khẩu hiện tại"));
		form.add(passwordBox(oldPassword));
		form.add(label("Mật khẩu mới"));
		form.add(passwordBox(newPassword));
		form.add(label("Nhập lại mật khẩu mới"));
		form.add(passwordBox(confirmPassword));

		while (true) {
			int result = JOptionPane.showConfirmDialog(parent, form, "Đổi mật khẩu",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION) {
				return;
			}

			String oldValue = new String(oldPassword.getPassword());
			String newValue = new String(newPassword.getPassword());
			String confirmValue = new String(confirmPassword.getPassword());

			if (oldValue.isEmpty() || newValue.isEmpty() || confirmValue.isEmpty()) {
				JOptionPane.showMessageDialog(parent, "Vui lòng nhập đầy đủ thông tin.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (newValue.length() < 6) {
				JOptionPane.showMessageDialog(parent, "Mật khẩu mới phải có ít nhất 6 ký tự.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (!newValue.equals(confirmValue)) {
				JOptionPane.showMessageDialog(parent, "Mật khẩu mới và xác nhận không khớp.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (oldValue.equals(newValue)) {
				JOptionPane.showMessageDialog(parent, "Mật khẩu mới không được trùng mật khẩu hiện tại.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}

			boolean changed = new AuthController().changePassword(currentUser.getUserName(), oldValue, newValue);
			if (!changed) {
				JOptionPane.showMessageDialog(parent, "Mật khẩu hiện tại không đúng.", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}

			currentUser.setPassWord(newValue);
			JOptionPane.showMessageDialog(parent, "Đổi mật khẩu thành công.");
			return;
		}
	}

	private static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		return label;
	}

	private static JPanel passwordBox(JPasswordField field) {
		field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
		field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		field.setPreferredSize(new Dimension(220, 30));

		char defaultEchoChar = field.getEchoChar();
		JButton toggle = new JButton("Hiện");
		toggle.setFocusable(false);
		toggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		toggle.setForeground(new Color(59, 53, 122));
		toggle.setBackground(new Color(245, 246, 250));
		toggle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 230)),
				BorderFactory.createEmptyBorder(4, 10, 4, 10)));
		toggle.setToolTipText("Hiện/Ẩn mật khẩu");
		toggle.addActionListener(event -> {
			boolean hidden = field.getEchoChar() != 0;
			field.setEchoChar(hidden ? (char) 0 : defaultEchoChar);
			toggle.setText(hidden ? "Ẩn" : "Hiện");
		});

		JPanel panel = new JPanel(new BorderLayout(6, 0));
		panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
		panel.setBackground(Color.WHITE);
		panel.add(field, BorderLayout.CENTER);
		panel.add(toggle, BorderLayout.EAST);
		return panel;
	}
}
