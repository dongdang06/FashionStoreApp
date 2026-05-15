 
package com.fashionstore.view.nvkho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
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

import com.fashionstore.controller.PhieuXuatTraController;
import com.fashionstore.model.PhieuXuatTra;

public class PhieuXuatTraPanel extends JPanel {
	private final PhieuXuatTraController phieuXuatTraController = new PhieuXuatTraController();
	private final List<PhieuXuatTra> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma phieu tra", "Ma NCC", "Ma NV", "Ngay tra", "Ly do" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public PhieuXuatTraPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Phieu xuat tra");
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
		data.addAll(phieuXuatTraController.getAll());
		reloadData();
	}

	public void reloadData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (data.isEmpty()) {
			data.addAll(phieuXuatTraController.getAll());
		}
		tableModel.setRowCount(0);
		for (PhieuXuatTra px : data) {
			tableModel.addRow(new Object[] {
					px.getMaPhieuTra(),
					px.getMaNCC(),
					px.getMaNV(),
					px.getNgayTra() == null ? "" : dateFormat.format(px.getNgayTra()),
					px.getLyDo()
			});
		}
	}

	private void addItem() {
		PhieuXuatTra px = showForm(null);
		if (px == null) {
			return;
		}
		data.add(px);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		PhieuXuatTra current = data.get(row);
		PhieuXuatTra updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa phieu xuat tra da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private PhieuXuatTra showForm(PhieuXuatTra current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		JTextField maPhieuTra = new JTextField(current == null ? "" : current.getMaPhieuTra());
		JTextField maNCC = new JTextField(current == null ? "" : current.getMaNCC());
		JTextField maNV = new JTextField(current == null ? "" : current.getMaNV());
		JTextField ngayTra = new JTextField(current == null || current.getNgayTra() == null
				? "" : dateFormat.format(current.getNgayTra()));
		JTextField lyDo = new JTextField(current == null ? "" : current.getLyDo());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma phieu tra"));
		form.add(maPhieuTra);
		form.add(new JLabel("Ma NCC"));
		form.add(maNCC);
		form.add(new JLabel("Ma NV"));
		form.add(maNV);
		form.add(new JLabel("Ngay tra (dd/MM/yyyy)"));
		form.add(ngayTra);
		form.add(new JLabel("Ly do"));
		form.add(lyDo);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them phieu xuat tra" : "Sua phieu xuat tra",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maPhieuTra.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma phieu tra la bat buoc.");
			return null;
		}
		try {
			java.util.Date date = ngayTra.getText().trim().isEmpty() ? null
					: dateFormat.parse(ngayTra.getText().trim());
			return new PhieuXuatTra(maPhieuTra.getText().trim(), maNCC.getText().trim(),
					maNV.getText().trim(), date, lyDo.getText().trim());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Du lieu khong hop le.");
			return null;
		}
	}
}

