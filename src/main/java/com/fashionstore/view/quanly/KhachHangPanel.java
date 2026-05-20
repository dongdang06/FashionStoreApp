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
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.fashionstore.controller.KhachHangController;
import com.fashionstore.model.KhachHang;

public class KhachHangPanel extends JPanel {
	private final KhachHangController khachHangController = new KhachHangController();
	private final List<KhachHang> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Mã KH", "Họ Tên", "Số Điện Thoại", "Điểm Tích Lũy" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public KhachHangPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Khách hàng & Điểm tích lũy");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("↻");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Thêm");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sửa");
		editButton.addActionListener(event -> editItem());

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		JTextField txtSearch = new JTextField(20);
		JButton btnSearch = new JButton("Tra cứu");
		btnSearch.addActionListener(e -> {
			String text = txtSearch.getText();
			if (text.trim().length() == 0) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		});
		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		header.add(searchPanel, BorderLayout.CENTER);

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
		data.addAll(khachHangController.getAllCustomers());
		reloadData();
	}

	public void reloadData() {
		data.clear();
		data.addAll(khachHangController.getAllCustomers());
		tableModel.setRowCount(0);
		for (KhachHang kh : data) {
			tableModel.addRow(new Object[] {
					kh.getMaKH(),
					kh.getHoTen(),
					kh.getSdt(),
					kh.getDiemTichLuy()
			});
		}
	}

	private void addItem() {
		showForm(null);
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		String maKH = (String) tableModel.getValueAt(modelRow, 0);
		KhachHang current = null;
		for (KhachHang kh : data) {
			if (kh.getMaKH().equals(maKH)) {
				current = kh;
				break;
			}
		}
		if (current != null) {
			showForm(current);
		}
	}

	private void showForm(KhachHang current) {
		JTextField txtMaKH = new JTextField(current == null ? com.fashionstore.util.MaGenerator.nextMaKH() : current.getMaKH());
		txtMaKH.setEditable(false);
		JTextField txtHoTen = new JTextField(current == null ? "" : current.getHoTen());
		JTextField txtSdt = new JTextField(current == null ? "" : current.getSdt());
		JTextField txtDiem = new JTextField(current == null ? "0" : String.valueOf(current.getDiemTichLuy()));
		txtDiem.setEditable(false);

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Mã Khách Hàng"));
		form.add(txtMaKH);
		form.add(new JLabel("Họ và Tên"));
		form.add(txtHoTen);
		form.add(new JLabel("Số Điện Thoại"));
		form.add(txtSdt);
		form.add(new JLabel("Điểm Tích Lũy"));
		form.add(txtDiem);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Thêm khách hàng" : "Sửa thông tin khách hàng",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		String hoTen = txtHoTen.getText().trim();
		String sdt = txtSdt.getText().trim();

		if (hoTen.isEmpty() || sdt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Họ tên và Số điện thoại không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!sdt.matches("\\d{9,11}")) {
			JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải gồm 9 - 11 chữ số)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			if (current == null) {
				// Thêm mới
				KhachHang kh = new KhachHang();
				kh.setHoTen(hoTen);
				kh.setSdt(sdt);
				boolean success = khachHangController.saveCustomer(kh);
				if (success) {
					JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// Cập nhật
				current.setHoTen(hoTen);
				current.setSdt(sdt);
				boolean success = khachHangController.updateCustomer(current);
				if (success) {
					JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
			reloadData();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}
