
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

import com.fashionstore.controller.DanhMucSanPhamController;
import com.fashionstore.model.DanhMucSanPham;

public class DanhMucSanPhamPanel extends JPanel {
	private final DanhMucSanPhamController danhMucController = new DanhMucSanPhamController();
	private final List<DanhMucSanPham> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Mã DM", "Tên DM", "Mã DM cha" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public DanhMucSanPhamPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Danh mục sản phẩm");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Thêm");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sửa");
		editButton.addActionListener(event -> editItem());
		JButton deleteButton = new JButton("Xóa");
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
		javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(
				tableModel);
		table.setRowSorter(sorter);

		javax.swing.JPanel searchPanel = new javax.swing.JPanel(
				new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		javax.swing.JTextField txtSearch = new javax.swing.JTextField(20);

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

		javax.swing.JButton btnSearch = new javax.swing.JButton("Tra cứu");
		txtSearch.addActionListener(e -> btnSearch.doClick());
		btnSearch.addActionListener(e -> {
			String text = txtSearch.getText();
			if (text.trim().length() == 0) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
				if (table.getRowCount() == 0) {
					JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp", "Thông báo", JOptionPane.WARNING_MESSAGE);
				}
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
		data.addAll(danhMucController.getAll());
		reloadData();
	}

	public void reloadData() {
		if (data.isEmpty()) {
			data.addAll(danhMucController.getAll());
		}
		tableModel.setRowCount(0);
		for (DanhMucSanPham dm : data) {
			tableModel.addRow(new Object[] {
					dm.getMaDM(),
					dm.getTenDM(),
					dm.getMaDMCha()
			});
		}
	}

	private void addItem() {
		DanhMucSanPham dm = showForm(null);
		if (dm == null) {
			return;
		}
		try {
			danhMucController.add(dm);
			data.add(dm);
			reloadData();
			JOptionPane.showMessageDialog(this, "Thêm danh mục thành công.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		DanhMucSanPham current = data.get(modelRow);
		DanhMucSanPham updated = showForm(current);
		if (updated == null) {
			return;
		}
		try {
			danhMucController.edit(updated);
			data.set(modelRow, updated);
			reloadData();
			JOptionPane.showMessageDialog(this, "Cập nhật danh mục thành công.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		DanhMucSanPham current = data.get(modelRow);
		int ok = JOptionPane.showConfirmDialog(this,
				"Xóa danh mục \"" + current.getTenDM() + "\"?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			danhMucController.remove(current.getMaDM());
			data.remove(modelRow);
			reloadData();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Không thể xóa. Có thể danh mục này còn danh mục con.\nLỗi: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private DanhMucSanPham showForm(DanhMucSanPham current) {
		JTextField maDM = new JTextField(
				current == null ? com.fashionstore.util.MaGenerator.nextMaDM() : current.getMaDM());
		maDM.setEditable(false);
		JTextField tenDM = new JTextField(current == null ? "" : current.getTenDM());
		JTextField maDMCha = new JTextField(current == null ? "" : current.getMaDMCha());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Mã DM"));
		form.add(maDM);
		form.add(new JLabel("Tên DM"));
		form.add(tenDM);
		form.add(new JLabel("Mã DM cha"));
		form.add(maDMCha);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Thêm danh mục" : "Sửa danh mục",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maDM.getText().trim().isEmpty() || tenDM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mã DM và Tên DM là bắt buộc.");
			return null;
		}
		return new DanhMucSanPham(maDM.getText().trim(), tenDM.getText().trim(),
				maDMCha.getText().trim().isEmpty() ? null : maDMCha.getText().trim());
	}
}
