
package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.NhanVienController;
import com.fashionstore.model.NhanVien;

public class NhanVienPanel extends JPanel {
	private final NhanVienController nhanVienController = new NhanVienController();
	private final List<NhanVien> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma NV", "Ho ten", "Email", "SDT", "Vai tro", "Trang thai" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public NhanVienPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Nhan vien");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Them");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sua");
		editButton.addActionListener(event -> editItem());

		boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Quan ly");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
		javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(
				tableModel);
		table.setRowSorter(sorter);

		javax.swing.JPanel searchPanel = new javax.swing.JPanel(
				new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		javax.swing.JTextField txtSearch = new javax.swing.JTextField(20);

		// Tìm kiếm thời gian thực khi gõ phím
		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			private void filter() {
				String text = txtSearch.getText();
				if (text.trim().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
				}
			}
		});

		javax.swing.JButton btnSearch = new javax.swing.JButton("Tra cuu");
		btnSearch.addActionListener(e -> {
			String text = txtSearch.getText();
			if (text.trim().length() == 0) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
			}
		});

		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		header.add(searchPanel, java.awt.BorderLayout.CENTER);

		header.add(actions, BorderLayout.EAST);

		table.setRowHeight(28);
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

		add(header, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		reloadData();
	}

	private void reloadFromSource() {
		data.clear();
		data.addAll(nhanVienController.getAll());
		reloadData();
	}

	public void reloadData() {
		if (data.isEmpty()) {
			data.addAll(nhanVienController.getAll());
		}
		tableModel.setRowCount(0);
		for (NhanVien nv : data) {
			tableModel.addRow(new Object[] {
					nv.getMaNV(),
					nv.getHoTen(),
					nv.getEmail(),
					nv.getSdt(),
					nv.getVaiTro(),
					nv.getTrangThaiLamViec()
			});
		}
	}

	private void addItem() {
		NhanVien nv = showForm(null);
		if (nv == null) {
			return;
		}
		data.add(nv);
		reloadData();
	}

	private void editItem() {
		int viewRow = table.getSelectedRow();
		if (viewRow < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}

		int modelRow = table.convertRowIndexToModel(viewRow);
		NhanVien current = data.get(modelRow);
		NhanVien updated = showForm(current);
		if (updated == null) {
			return;
		}
		data.set(modelRow, updated);
		reloadData();
	}

	private NhanVien showForm(NhanVien current) {
		boolean isNew = current == null;

		JTextField maNV = new JTextField(isNew ? "" : current.getMaNV());
		maNV.setEditable(isNew);

		JTextField hoTen = new JTextField(isNew ? "" : current.getHoTen());
		JTextField email = new JTextField(isNew ? "" : current.getEmail());
		JTextField sdt = new JTextField(isNew ? "" : current.getSdt());

		// Trạng thái làm việc
		String[] trangThaiOptions = { "Dang lam viec", "Da nghi viec" };
		javax.swing.JComboBox<String> cbTrangThai = new javax.swing.JComboBox<>(trangThaiOptions);
		if (!isNew && "Da nghi viec".equals(current.getTrangThaiLamViec())) {
			cbTrangThai.setSelectedIndex(1);
		}

		// Phân quyền
		String[] vaiTroOptions = { "Quan ly", "Nhan vien ban hang", "Nhan vien kho", "Nhan vien ke toan" };
		javax.swing.JComboBox<String> cbVaiTro = new javax.swing.JComboBox<>(vaiTroOptions);

		if (!isNew && current.getVaiTro() != null) {
			for (int i = 0; i < vaiTroOptions.length; i++) {
				if (vaiTroOptions[i].equalsIgnoreCase(current.getVaiTro())) {
					cbVaiTro.setSelectedIndex(i);
					break;
				}
			}
		}

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Mã NV (Tên đăng nhập)"));
		form.add(maNV);
		form.add(new JLabel("Họ tên"));
		form.add(hoTen);
		form.add(new JLabel("Email"));
		form.add(email);
		form.add(new JLabel("Số điện thoại"));
		form.add(sdt);

		form.add(new JLabel("Phân quyền tài khoản"));
		form.add(cbVaiTro);

		if (!isNew) {
			form.add(new JLabel("Trạng thái làm việc"));
			form.add(cbTrangThai);
		}

		int result = JOptionPane.showConfirmDialog(this, form,
				isNew ? "Thêm nhân viên mới" : "Sửa thông tin nhân viên",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result != JOptionPane.OK_OPTION) {
			return null;
		}

		if (maNV.getText().trim().isEmpty() || hoTen.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mã NV và Họ tên là bắt buộc.");
			return null;
		}

		if (isNew) {
			String selectedVaiTro = cbVaiTro.getSelectedItem().toString();
			JOptionPane.showMessageDialog(this, "Tài khoản nhân viên đã được tự động cấp:\n"
					+ "- Tên đăng nhập: " + maNV.getText().trim() + "\n"
					+ "- Mật khẩu: 123456\n"
					+ "- Quyền: " + selectedVaiTro,
					"Tạo tài khoản thành công", JOptionPane.INFORMATION_MESSAGE);
			// Bug #8 fix: lưu vaiTro vào đối tượng NhanVien
			return new NhanVien(maNV.getText().trim(), hoTen.getText().trim(),
					email.getText().trim(), sdt.getText().trim(), "Dang lam viec", selectedVaiTro);
		} else {
			String newTrangThai = cbTrangThai.getSelectedItem().toString();
			String selectedVaiTro = cbVaiTro.getSelectedItem().toString();
			if (!newTrangThai.equals(current.getTrangThaiLamViec()) && newTrangThai.equals("Da nghi viec")) {
				JOptionPane.showMessageDialog(this, "Cảnh báo: Tài khoản của nhân viên này đã bị vô hiệu hóa!",
						"Khóa tài khoản", JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Đã cập nhật phân quyền thành: " + selectedVaiTro,
						"Cập nhật thành công", JOptionPane.INFORMATION_MESSAGE);
			}
			return new NhanVien(maNV.getText().trim(), hoTen.getText().trim(),
					email.getText().trim(), sdt.getText().trim(), newTrangThai, selectedVaiTro);
		}
	}
}
