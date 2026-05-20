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
			JOptionPane.showMessageDialog(parent, "Khong tim thay tai khoan dang dang nhap.", "Loi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JPasswordField oldPassword = new JPasswordField();
		JPasswordField newPassword = new JPasswordField();
		JPasswordField confirmPassword = new JPasswordField();

		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		form.add(label("Mat khau hien tai"));
		form.add(passwordBox(oldPassword));
		form.add(label("Mat khau moi"));
		form.add(passwordBox(newPassword));
		form.add(label("Nhap lai mat khau moi"));
		form.add(passwordBox(confirmPassword));

		while (true) {
			int result = JOptionPane.showConfirmDialog(parent, form, "Doi mat khau",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION) {
				return;
			}

			String oldValue = new String(oldPassword.getPassword());
			String newValue = new String(newPassword.getPassword());
			String confirmValue = new String(confirmPassword.getPassword());

			if (oldValue.isEmpty() || newValue.isEmpty() || confirmValue.isEmpty()) {
				JOptionPane.showMessageDialog(parent, "Vui long nhap day du thong tin.", "Loi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (newValue.length() < 6) {
				JOptionPane.showMessageDialog(parent, "Mat khau moi phai co it nhat 6 ky tu.", "Loi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (!newValue.equals(confirmValue)) {
				JOptionPane.showMessageDialog(parent, "Mat khau moi va xac nhan khong khop.", "Loi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}
			if (oldValue.equals(newValue)) {
				JOptionPane.showMessageDialog(parent, "Mat khau moi khong duoc trung mat khau hien tai.", "Loi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}

			boolean changed = new AuthController().changePassword(currentUser.getUserName(), oldValue, newValue);
			if (!changed) {
				JOptionPane.showMessageDialog(parent, "Mat khau hien tai khong dung.", "Loi",
						JOptionPane.ERROR_MESSAGE);
				continue;
			}

			currentUser.setPassWord(newValue);
			JOptionPane.showMessageDialog(parent, "Doi mat khau thanh cong.");
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
		JButton toggle = new JButton("Hien");
		toggle.setFocusable(false);
		toggle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		toggle.setForeground(new Color(59, 53, 122));
		toggle.setBackground(new Color(245, 246, 250));
		toggle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 230)),
				BorderFactory.createEmptyBorder(4, 10, 4, 10)));
		toggle.setToolTipText("Hien/An mat khau");
		toggle.addActionListener(event -> {
			boolean hidden = field.getEchoChar() != 0;
			field.setEchoChar(hidden ? (char) 0 : defaultEchoChar);
			toggle.setText(hidden ? "An" : "Hien");
		});

		JPanel panel = new JPanel(new BorderLayout(6, 0));
		panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
		panel.setBackground(Color.WHITE);
		panel.add(field, BorderLayout.CENTER);
		panel.add(toggle, BorderLayout.EAST);
		return panel;
	}
}
