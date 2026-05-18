 
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

import com.fashionstore.controller.NhaCungCapController;
import com.fashionstore.model.NhaCungCap;

public class NhaCungCapPanel extends JPanel {
	private final NhaCungCapController nccController = new NhaCungCapController();
	private final List<NhaCungCap> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma NCC", "Ten NCC", "SDT", "Email", "Dia chi", "Trang thai" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public NhaCungCapPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Nha cung cap");
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
		data.addAll(nccController.getAll());
		reloadData();
	}

	public void reloadData() {
		if (data.isEmpty()) {
			data.addAll(nccController.getAll());
		}
		tableModel.setRowCount(0);
		for (NhaCungCap ncc : data) {
			tableModel.addRow(new Object[] {
					ncc.getMaNCC(),
					ncc.getTenNCC(),
					ncc.getSdt(),
					ncc.getEmail(),
					ncc.getDiaChi(),
					ncc.getTrangThaiNCC()
			});
		}
	}

	private void addItem() {
		NhaCungCap ncc = showForm(null);
		if (ncc == null) {
			return;
		}
		data.add(ncc);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		NhaCungCap current = data.get(row);
		NhaCungCap updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa nha cung cap da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private NhaCungCap showForm(NhaCungCap current) {
		JTextField maNCC = new JTextField(current == null ? "" : current.getMaNCC());
		JTextField tenNCC = new JTextField(current == null ? "" : current.getTenNCC());
		JTextField sdt = new JTextField(current == null ? "" : current.getSdt());
		JTextField email = new JTextField(current == null ? "" : current.getEmail());
		JTextField diaChi = new JTextField(current == null ? "" : current.getDiaChi());
		JTextField trangThai = new JTextField(current == null ? "" : current.getTrangThaiNCC());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma NCC"));
		form.add(maNCC);
		form.add(new JLabel("Ten NCC"));
		form.add(tenNCC);
		form.add(new JLabel("SDT"));
		form.add(sdt);
		form.add(new JLabel("Email"));
		form.add(email);
		form.add(new JLabel("Dia chi"));
		form.add(diaChi);
		form.add(new JLabel("Trang thai"));
		form.add(trangThai);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them nha cung cap" : "Sua nha cung cap",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maNCC.getText().trim().isEmpty() || tenNCC.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma NCC va Ten NCC la bat buoc.");
			return null;
		}
		return new NhaCungCap(maNCC.getText().trim(), tenNCC.getText().trim(),
				sdt.getText().trim(), email.getText().trim(), diaChi.getText().trim(),
				trangThai.getText().trim());
	}
}

