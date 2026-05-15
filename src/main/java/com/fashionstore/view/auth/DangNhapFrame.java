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
import com.fashionstore.view.MainAppFrame;

public class DangNhapFrame extends JFrame {
	private final JTextField usernameField = new JTextField();
	private final JPasswordField passwordField = new JPasswordField();
	private final AuthController authController = new AuthController();

	public DangNhapFrame() {
		setTitle("Dang nhap");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 320);
		setLocationRelativeTo(null);

		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(new Color(245, 246, 250));
		content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

		JLabel title = new JLabel("Fashion Store");
		title.setFont(new Font("Segoe UI", Font.BOLD, 18));
		title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

		JPanel form = new JPanel();
		form.setOpaque(false);
		form.setLayout(new javax.swing.BoxLayout(form, javax.swing.BoxLayout.Y_AXIS));

		form.add(labelFor("Username"));
		form.add(fieldBox(usernameField));
		form.add(spacer(12));
		form.add(labelFor("Password"));
		form.add(fieldBox(passwordField));
		form.add(spacer(18));

		JButton loginButton = new JButton("Dang nhap");
		loginButton.setBackground(new Color(59, 53, 122));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFocusPainted(false);
		loginButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
		loginButton.addActionListener(event -> handleLogin());

		form.add(loginButton);

		content.add(title, BorderLayout.NORTH);
		content.add(form, BorderLayout.CENTER);
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
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));
		field.setBorder(BorderFactory.createEmptyBorder());
		field.setPreferredSize(new Dimension(260, 28));
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

		boolean ok = authController.login(username, password);
		if (ok) {
			SwingUtilities.invokeLater(() -> {
				MainAppFrame frame = new MainAppFrame();
				frame.setVisible(true);
			});
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Sai tai khoan hoac mat khau.");
		}
	}
}
