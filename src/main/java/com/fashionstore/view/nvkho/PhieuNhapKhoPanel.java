 
package com.fashionstore.view.nvkho;

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

import com.fashionstore.controller.PhieuNhapKhoController;
import com.fashionstore.model.PhieuNhapKho;

public class PhieuNhapKhoPanel extends JPanel {
	private final PhieuNhapKhoController phieuNhapController = new PhieuNhapKhoController();
	private final List<PhieuNhapKho> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma PN", "Ngay nhap", "Tong gia tri", "Ma NCC", "Ma NV" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public PhieuNhapKhoPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Phieu nhap kho");
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
		data.addAll(phieuNhapController.getAll());
		reloadData();
	}

	public void reloadData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		if (data.isEmpty()) {
			data.addAll(phieuNhapController.getAll());
		}
		tableModel.setRowCount(0);
		for (PhieuNhapKho pn : data) {
			tableModel.addRow(new Object[] {
					pn.getMaPN(),
					pn.getNgayNhap() == null ? "" : dateFormat.format(pn.getNgayNhap()),
					currency.format(pn.getTongGiaTri()),
					pn.getMaNCC(),
					pn.getMaNV()
			});
		}
	}

	private void addItem() {
		PhieuNhapKho pn = showForm(null);
		if (pn == null) {
			return;
		}
		data.add(pn);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		PhieuNhapKho current = data.get(row);
		PhieuNhapKho updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa phieu nhap da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private PhieuNhapKho showForm(PhieuNhapKho current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		JTextField maPN = new JTextField(current == null ? "" : current.getMaPN());
		JTextField ngayNhap = new JTextField(current == null || current.getNgayNhap() == null
				? "" : dateFormat.format(current.getNgayNhap()));
		JTextField tongGiaTri = new JTextField(current == null ? "" : String.valueOf(current.getTongGiaTri()));
		JTextField maNCC = new JTextField(current == null ? "" : current.getMaNCC());
		JTextField maNV = new JTextField(current == null ? "" : current.getMaNV());

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma PN"));
		form.add(maPN);
		form.add(new JLabel("Ngay nhap (dd/MM/yyyy)"));
		form.add(ngayNhap);
		form.add(new JLabel("Tong gia tri"));
		form.add(tongGiaTri);
		form.add(new JLabel("Ma NCC"));
		form.add(maNCC);
		form.add(new JLabel("Ma NV"));
		form.add(maNV);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them phieu nhap" : "Sua phieu nhap",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maPN.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma PN la bat buoc.");
			return null;
		}
		try {
			long tongGiaTriValue = tongGiaTri.getText().trim().isEmpty() ? 0
					: Long.parseLong(tongGiaTri.getText().trim());
			java.util.Date date = ngayNhap.getText().trim().isEmpty() ? null
					: dateFormat.parse(ngayNhap.getText().trim());
			return new PhieuNhapKho(maPN.getText().trim(), date, tongGiaTriValue,
					maNCC.getText().trim(), maNV.getText().trim());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Du lieu khong hop le.");
			return null;
		}
	}
}

