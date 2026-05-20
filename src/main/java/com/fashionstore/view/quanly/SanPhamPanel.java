 
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.DanhMucSanPhamController;
import com.fashionstore.controller.SanPhamController;
import com.fashionstore.model.DanhMucSanPham;
import com.fashionstore.model.SanPham;

public class SanPhamPanel extends JPanel {
	private final SanPhamController sanPhamController = new SanPhamController();
	private final DanhMucSanPhamController danhMucController = new DanhMucSanPhamController();
	private final List<SanPham> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma SP", "Ten SP", "Ma DM", "Trang thai" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public SanPhamPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("San pham");
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
		data.addAll(sanPhamController.getAll());
		reloadData();
	}

	public void reloadData() {
		if (data.isEmpty()) {
			data.addAll(sanPhamController.getAll());
		}
		tableModel.setRowCount(0);
		for (SanPham sp : data) {
			tableModel.addRow(new Object[] {
					sp.getMaSP(),
					sp.getTenSP(),
					sp.getMaDM(),
					sp.getTrangThaiKD()
			});
		}
	}

	private void addItem() {
		SanPham sp = showForm(null);
		if (sp == null) {
			return;
		}
		data.add(sp);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		SanPham current = data.get(row);
		SanPham updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa san pham da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private SanPham showForm(SanPham current) {
		JTextField maSP = new JTextField(current == null ? com.fashionstore.util.MaGenerator.nextMaSP() : current.getMaSP());
		maSP.setEditable(false);
		JTextField tenSP = new JTextField(current == null ? "" : current.getTenSP());
		JComboBox<String> maDM = new JComboBox<>(getDanhMucOptions());
		JComboBox<String> trangThai = new JComboBox<>(new String[] { "Dang ban", "Ngung ban" });
		if (current != null) {
			maDM.setSelectedItem(getDanhMucDisplay(current.getMaDM()));
			trangThai.setSelectedItem(current.getTrangThaiKD());
		}

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma SP"));
		form.add(maSP);
		form.add(new JLabel("Ten SP"));
		form.add(tenSP);
		form.add(new JLabel("Ma DM"));
		form.add(maDM);
		form.add(new JLabel("Trang thai"));
		form.add(trangThai);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them san pham" : "Sua san pham",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maSP.getText().trim().isEmpty() || tenSP.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma SP va Ten SP la bat buoc.");
			return null;
		}
		return new SanPham(maSP.getText().trim(), extractDanhMucCode((String) maDM.getSelectedItem()),
				tenSP.getText().trim(), (String) trangThai.getSelectedItem());
	}

	private String[] getDanhMucOptions() {
		List<String> options = new ArrayList<>();
		List<DanhMucSanPham> categories = danhMucController.getAll();
		for (DanhMucSanPham dm : categories) {
			String display = formatDanhMucDisplay(dm);
			String maDM = dm.getMaDM();
			if (maDM != null && !maDM.isBlank() && !options.contains(display)) {
				options.add(display);
			}
		}
		if (options.isEmpty()) {
			options.add("DM-001 - Ao");
			options.add("DM-002 - Quan");
			options.add("DM-003 - Vay");
		}
		return options.toArray(new String[0]);
	}

	private String formatDanhMucDisplay(DanhMucSanPham dm) {
		String maDM = dm.getMaDM() == null ? "" : dm.getMaDM().trim();
		String tenDM = dm.getTenDM() == null ? "" : dm.getTenDM().trim();
		if (tenDM.isEmpty()) {
			return maDM;
		}
		return maDM + " - " + tenDM;
	}

	private String getDanhMucDisplay(String maDM) {
		if (maDM == null || maDM.isBlank()) {
			return maDM;
		}
		List<DanhMucSanPham> categories = danhMucController.getAll();
		for (DanhMucSanPham dm : categories) {
			if (maDM.equals(dm.getMaDM())) {
				return formatDanhMucDisplay(dm);
			}
		}
		return maDM;
	}

	private String extractDanhMucCode(String display) {
		if (display == null) {
			return null;
		}
		int index = display.indexOf(" - ");
		return index > 0 ? display.substring(0, index).trim() : display.trim();
	}
}

