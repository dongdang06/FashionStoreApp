 
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
			new Object[] { "Ma DM", "Ten DM", "Ma DM cha" }, 0) {
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

		JLabel title = new JLabel("Danh muc san pham");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("Lam moi");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Them");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sua");
		editButton.addActionListener(event -> editItem());
		JButton deleteButton = new JButton("Xoa");
		deleteButton.addActionListener(event -> deleteItem());

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
		actions.add(deleteButton);
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
		data.add(dm);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		DanhMucSanPham current = data.get(row);
		DanhMucSanPham updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa danh muc da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private DanhMucSanPham showForm(DanhMucSanPham current) {
		JTextField maDM = new JTextField(current == null ? "" : current.getMaDM());
		JTextField tenDM = new JTextField(current == null ? "" : current.getTenDM());
		JTextField maDMCha = new JTextField(current == null ? "" : current.getMaDMCha());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma DM"));
		form.add(maDM);
		form.add(new JLabel("Ten DM"));
		form.add(tenDM);
		form.add(new JLabel("Ma DM cha"));
		form.add(maDMCha);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them danh muc" : "Sua danh muc",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maDM.getText().trim().isEmpty() || tenDM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma DM va Ten DM la bat buoc.");
			return null;
		}
		return new DanhMucSanPham(maDM.getText().trim(), tenDM.getText().trim(),
				maDMCha.getText().trim().isEmpty() ? null : maDMCha.getText().trim());
	}
}

