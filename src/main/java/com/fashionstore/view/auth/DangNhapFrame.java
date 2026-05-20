package com.fashionstore.view.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.fashionstore.controller.AuthController;

public class DangNhapFrame extends JFrame {
	private final JTextField usernameField = new JTextField();
	private final JPasswordField passwordField = new JPasswordField();
	private final AuthController authController = new AuthController();

	public DangNhapFrame() {
		setTitle("Dang nhap");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1360, 800);
		setLocationRelativeTo(null);

		JPanel content = new JPanel(new java.awt.GridBagLayout());
		content.setBackground(new Color(245, 246, 250));

		JPanel loginCard = new JPanel(new BorderLayout());
		loginCard.setBackground(Color.WHITE);
		loginCard.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 230)),
				BorderFactory.createEmptyBorder(40, 48, 40, 48)));

		JLabel title = new JLabel("FASHION STORE", javax.swing.SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));

		JPanel form = new JPanel();
		form.setOpaque(false);
		form.setLayout(new javax.swing.BoxLayout(form, javax.swing.BoxLayout.Y_AXIS));

		form.add(labelFor("Username"));
		form.add(fieldBox(usernameField));
		form.add(spacer(16));
		form.add(labelFor("Password"));
		form.add(fieldBox(passwordField));
		form.add(spacer(32));

		JButton loginButton = new JButton("Đăng nhập");
		loginButton.setBackground(new Color(59, 53, 122));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		loginButton.setFocusPainted(false);
		loginButton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		loginButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		loginButton.addActionListener(event -> handleLogin());

		form.add(loginButton);

		loginCard.add(title, BorderLayout.NORTH);
		loginCard.add(form, BorderLayout.CENTER);
		
		content.add(loginCard);
		setContentPane(content);
	}

	private JLabel labelFor(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 2));
		return label;
	}

	private JPanel fieldBox(javax.swing.JComponent field) {
		JPanel box = new JPanel(new BorderLayout());
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 230)),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		field.setPreferredSize(new Dimension(320, 32));
		box.add(field, BorderLayout.CENTER);
		return box;
	}

	private javax.swing.Box.Filler spacer(int height) {
		return new javax.swing.Box.Filler(new Dimension(0, height),
				new Dimension(0, height), new Dimension(0, height));
	}

	private void handleLogin() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword());

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui long nhap day du thong tin.");
			return;
		}

		com.fashionstore.model.TaiKhoan tk = authController.login(username, password);
		if (tk != null) {
			com.fashionstore.util.SessionManager.setCurrentUser(tk);
			SwingUtilities.invokeLater(() -> {
				new com.fashionstore.view.MainAppFrame().setVisible(true);
			});
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Sai tai khoan hoac mat khau.");
		}
	}
}
