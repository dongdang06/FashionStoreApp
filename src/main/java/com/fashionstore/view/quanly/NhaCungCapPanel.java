 
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

		javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
				new javax.swing.table.TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		JTextField txtSearch = new JTextField(20);
		JButton btnSearch = new JButton("Tra cuu");
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
		try {
			nccController.add(ncc);
			data.add(ncc);
			reloadData();
			JOptionPane.showMessageDialog(this, "Them nha cung cap thanh cong.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		NhaCungCap current = data.get(modelRow);
		NhaCungCap updated = showForm(current);
		if (updated == null) {
			return;
		}
		try {
			nccController.edit(updated);
			data.set(modelRow, updated);
			reloadData();
			JOptionPane.showMessageDialog(this, "Cap nhat nha cung cap thanh cong.");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can xoa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		NhaCungCap current = data.get(modelRow);
		int ok = JOptionPane.showConfirmDialog(this,
				"Xoa nha cung cap \"" + current.getTenNCC() + "\"?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			nccController.remove(current.getMaNCC());
			data.remove(modelRow);
			reloadData();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Khong the xoa nha cung cap.\nLoi: " + ex.getMessage(),
					"Loi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private NhaCungCap showForm(NhaCungCap current) {
		JTextField maNCC = new JTextField(
				current == null ? com.fashionstore.util.MaGenerator.nextMaNCC() : current.getMaNCC());
		maNCC.setEditable(false);
		maNCC.setBackground(new Color(230, 230, 230));

		JTextField tenNCC = new JTextField(current == null ? "" : current.getTenNCC());
		JTextField sdt    = new JTextField(current == null ? "" : current.getSdt());
		JTextField email  = new JTextField(current == null ? "" : current.getEmail());
		JTextField diaChi = new JTextField(current == null ? "" : current.getDiaChi());

		// Trang thai: them moi hien "Hoat dong" read-only (DB DEFAULT), sua thi dung JComboBox
		JComboBox<String> trangThaiBox = current == null ? null
				: new JComboBox<>(new String[]{"Hoat dong", "Ngung hoat dong"});
		JTextField trangThaiReadOnly = null;
		if (trangThaiBox != null) {
			trangThaiBox.setSelectedItem(current.getTrangThaiNCC());
		} else {
			trangThaiReadOnly = new JTextField("Hoat dong");
			trangThaiReadOnly.setEditable(false);
			trangThaiReadOnly.setBackground(new Color(230, 230, 230));
		}

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
		if (trangThaiBox != null) {
			form.add(trangThaiBox);
		} else {
			form.add(trangThaiReadOnly);
		}

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them nha cung cap" : "Sua nha cung cap",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (tenNCC.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ten NCC la bat buoc.");
			return null;
		}
		if (sdt.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "SDT la bat buoc.");
			return null;
		}
		if (diaChi.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Dia chi la bat buoc.");
			return null;
		}
		return new NhaCungCap(
				maNCC.getText().trim(),
				tenNCC.getText().trim(),
				sdt.getText().trim(),
				email.getText().trim(),
				diaChi.getText().trim(),
				trangThaiBox == null ? "Hoat dong" : (String) trangThaiBox.getSelectedItem());
	}
}
