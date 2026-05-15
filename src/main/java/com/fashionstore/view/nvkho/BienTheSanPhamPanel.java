 
package com.fashionstore.view.nvkho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
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

import com.fashionstore.controller.BienTheSanPhamController;
import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamPanel extends JPanel {
	private final BienTheSanPhamController bienTheController = new BienTheSanPhamController();
	private final List<BienTheSanPham> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma bien the", "Ma SP", "Mau sac", "Kich thuoc", "Gia ban", "Ton kho" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public BienTheSanPhamPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Bien the san pham");
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
		data.addAll(bienTheController.getAll());
		reloadData();
	}

	public void reloadData() {
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		if (data.isEmpty()) {
			data.addAll(bienTheController.getAll());
		}
		tableModel.setRowCount(0);
		for (BienTheSanPham bt : data) {
			tableModel.addRow(new Object[] {
					bt.getMaBienThe(),
					bt.getMaSP(),
					bt.getMauSac(),
					bt.getKichThuoc(),
					currency.format(bt.getGiaBan()),
					bt.getSoLuongTon()
			});
		}
	}

	private void addItem() {
		BienTheSanPham bt = showForm(null);
		if (bt == null) {
			return;
		}
		data.add(bt);
		reloadData();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		BienTheSanPham current = data.get(row);
		BienTheSanPham updated = showForm(current);
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
		int ok = JOptionPane.showConfirmDialog(this, "Xoa bien the da chon?", "Xac nhan",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			data.remove(row);
			reloadData();
		}
	}

	private BienTheSanPham showForm(BienTheSanPham current) {
		JTextField maBienThe = new JTextField(current == null ? "" : current.getMaBienThe());
		JTextField maSP = new JTextField(current == null ? "" : current.getMaSP());
		JTextField maQR = new JTextField(current == null ? "" : current.getMaQR());
		JTextField mauSac = new JTextField(current == null ? "" : current.getMauSac());
		JTextField kichThuoc = new JTextField(current == null ? "" : current.getKichThuoc());
		JTextField giaBan = new JTextField(current == null ? "" : String.valueOf(current.getGiaBan()));
		JTextField tonKho = new JTextField(current == null ? "" : String.valueOf(current.getSoLuongTon()));

		JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
		form.add(new JLabel("Ma bien the"));
		form.add(maBienThe);
		form.add(new JLabel("Ma SP"));
		form.add(maSP);
		form.add(new JLabel("Ma QR"));
		form.add(maQR);
		form.add(new JLabel("Mau sac"));
		form.add(mauSac);
		form.add(new JLabel("Kich thuoc"));
		form.add(kichThuoc);
		form.add(new JLabel("Gia ban"));
		form.add(giaBan);
		form.add(new JLabel("Ton kho"));
		form.add(tonKho);

		int result = JOptionPane.showConfirmDialog(this, form,
				current == null ? "Them bien the" : "Sua bien the",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return null;
		}
		if (maBienThe.getText().trim().isEmpty() || maSP.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Ma bien the va Ma SP la bat buoc.");
			return null;
		}
		try {
			long giaBanValue = giaBan.getText().trim().isEmpty() ? 0 : Long.parseLong(giaBan.getText().trim());
			int tonKhoValue = tonKho.getText().trim().isEmpty() ? 0 : Integer.parseInt(tonKho.getText().trim());
			return new BienTheSanPham(maBienThe.getText().trim(), maSP.getText().trim(),
					maQR.getText().trim(), mauSac.getText().trim(), kichThuoc.getText().trim(),
					giaBanValue, tonKhoValue);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Du lieu khong hop le.");
			return null;
		}
	}
}

