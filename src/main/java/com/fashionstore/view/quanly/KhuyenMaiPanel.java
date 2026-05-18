 
package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.KhuyenMaiController;
import com.fashionstore.model.KhuyenMai;

public class KhuyenMaiPanel extends JPanel {
	private final KhuyenMaiController khuyenMaiController = new KhuyenMaiController();
	private final List<KhuyenMai> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma KM", "Ten KM", "Bat dau", "Ket thuc", "Muc giam", "Trang thai" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public KhuyenMaiPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Khuyen mai");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Them");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sua");
		editButton.addActionListener(event -> editItem());
		JButton deleteButton = new JButton("Xoa");
		deleteButton.addActionListener(event -> deleteItem());

		boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Quan ly");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);
		deleteButton.setEnabled(canEdit);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
		actions.add(deleteButton);
		javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		javax.swing.JPanel searchPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		javax.swing.JTextField txtSearch = new javax.swing.JTextField(20);
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
		data.addAll(khuyenMaiController.getAll());
		reloadData();
	}

	public void reloadData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		if (data.isEmpty()) {
			data.addAll(khuyenMaiController.getAll());
		}
		tableModel.setRowCount(0);
		for (KhuyenMai km : data) {
			tableModel.addRow(new Object[] {
					km.getMaKM(),
					km.getTenKM(),
					km.getNgayBatDau() == null ? "" : dateFormat.format(km.getNgayBatDau()),
					km.getNgayKetThuc() == null ? "" : dateFormat.format(km.getNgayKetThuc()),
					currency.format(km.getMucGiamToiDa()),
					km.getTrangThaiKM()
			});
		}
	}

	private void addItem() {
		KhuyenMai km = showForm(null);
		if (km == null) {
			return;
		}
		data.add(km);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		KhuyenMai current = data.get(row);
		KhuyenMai updated = showForm(current);
		if (updated == null) {
			return;
		}
		data.set(row, updated);
		reloadData();
	}

	private void deleteItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can xoa.");
			return;
		}
		int ok = JOptionPane.showConfirmDialog(this, "Xoa khuyen mai da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private KhuyenMai showForm(KhuyenMai current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		JTextField maKM = new JTextField(current == null ? "" : current.getMaKM());
		JTextField tenKM = new JTextField(current == null ? "" : current.getTenKM());
		JTextField batDau = new JTextField(current == null || current.getNgayBatDau() == null
				? "" : dateFormat.format(current.getNgayBatDau()));
		JTextField ketThuc = new JTextField(current == null || current.getNgayKetThuc() == null
				? "" : dateFormat.format(current.getNgayKetThuc()));
		JTextField mucGiam = new JTextField(current == null ? "" : String.valueOf(current.getMucGiamToiDa()));
		JTextField trangThai = new JTextField(current == null ? "" : current.getTrangThaiKM());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma KM"));
		form.add(maKM);
		form.add(new JLabel("Ten KM"));
		form.add(tenKM);
		form.add(new JLabel("Bat dau (dd/MM/yyyy)"));
		form.add(batDau);
		form.add(new JLabel("Ket thuc (dd/MM/yyyy)"));
		form.add(ketThuc);
		form.add(new JLabel("Muc giam"));
		form.add(mucGiam);
		form.add(new JLabel("Trang thai"));
		form.add(trangThai);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them khuyen mai" : "Sua khuyen mai",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maKM.getText().trim().isEmpty() || tenKM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma KM va Ten KM la bat buoc.");
			return null;
		}
		try {
			long mucGiamValue = mucGiam.getText().trim().isEmpty() ? 0 : Long.parseLong(mucGiam.getText().trim());
			java.util.Date ngayBatDau = batDau.getText().trim().isEmpty() ? null
					: dateFormat.parse(batDau.getText().trim());
			java.util.Date ngayKetThuc = ketThuc.getText().trim().isEmpty() ? null
					: dateFormat.parse(ketThuc.getText().trim());
			return new KhuyenMai(maKM.getText().trim(), tenKM.getText().trim(),
					ngayBatDau, ngayKetThuc, mucGiamValue, trangThai.getText().trim());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Du lieu khong hop le.");
			return null;
		}
	}
}

