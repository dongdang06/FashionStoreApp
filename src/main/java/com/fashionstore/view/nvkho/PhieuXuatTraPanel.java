package com.fashionstore.view.nvkho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.PhieuXuatTraController;
import com.fashionstore.model.ChiTietPhieuXuat;
import com.fashionstore.model.PhieuXuatTra;
import com.fashionstore.model.TaiKhoan;
import com.fashionstore.util.SessionManager;

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

		JTextField txtSearch = new JTextField(22);
		JButton btnSearch = new JButton("Tra cuu");
		btnSearch.addActionListener(e -> loadData(phieuXuatTraController.search(txtSearch.getText())));

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		header.add(searchPanel, BorderLayout.CENTER);

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> {
			txtSearch.setText("");
			reloadData();
		});
		JButton addButton = new JButton("Them");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sua");
		editButton.addActionListener(event -> editItem());
		JButton deleteButton = new JButton("Xoa");
		deleteButton.addActionListener(event -> deleteItem());
		JButton printButton = new JButton("In");
		printButton.addActionListener(event -> printItem());

		boolean canEdit = SessionManager.hasPermission("Kho");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);
		deleteButton.setEnabled(canEdit);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
		actions.add(deleteButton);
		actions.add(printButton);
		header.add(actions, BorderLayout.EAST);

		table.setRowHeight(28);
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

		add(header, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		reloadData();
	}

	public void reloadData() {
		loadData(phieuXuatTraController.getAll());
	}

	private void loadData(List<PhieuXuatTra> returnNotes) {
		data.clear();
		data.addAll(returnNotes);
		refreshTable();
	}

	private void refreshTable() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
		try {
			phieuXuatTraController.create(px);
			JOptionPane.showMessageDialog(this, "Tao phieu xuat tra hang thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void editItem() {
		PhieuXuatTra selected = getSelectedItem("sua");
		if (selected == null) {
			return;
		}
		PhieuXuatTra current = phieuXuatTraController.getById(selected.getMaPhieuTra());
		PhieuXuatTra updated = showForm(current == null ? selected : current);
		if (updated == null) {
			return;
		}
		try {
			phieuXuatTraController.update(updated);
			JOptionPane.showMessageDialog(this, "Cap nhat phieu xuat tra thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void deleteItem() {
		PhieuXuatTra selected = getSelectedItem("xoa");
		if (selected == null) {
			return;
		}
		int ok = JOptionPane.showConfirmDialog(this,
				"Xoa phieu xuat tra " + selected.getMaPhieuTra() + "?",
				"Xac nhan", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			phieuXuatTraController.delete(selected.getMaPhieuTra());
			JOptionPane.showMessageDialog(this, "Xoa phieu xuat tra thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void printItem() {
		PhieuXuatTra selected = getSelectedItem("in");
		if (selected == null) {
			return;
		}
		try {
			showPrintDialog(phieuXuatTraController.buildPrintPreview(selected.getMaPhieuTra()), "In phieu xuat tra");
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private PhieuXuatTra getSelectedItem(String action) {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can " + action + ".");
			return null;
		}
		int modelRow = table.convertRowIndexToModel(row);
		return data.get(modelRow);
	}

	private PhieuXuatTra showForm(PhieuXuatTra current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);

		JTextField maPhieuTra = new JTextField(current == null ? "" : current.getMaPhieuTra());
		maPhieuTra.setEditable(current == null);
		JTextField maNCC = new JTextField(current == null ? "" : current.getMaNCC());
		JTextField maNV = new JTextField(current == null ? getCurrentEmployeeId() : current.getMaNV());
		JTextField ngayTra = new JTextField(current == null || current.getNgayTra() == null
				? dateFormat.format(new Date()) : dateFormat.format(current.getNgayTra()));
		JTextField lyDo = new JTextField(current == null ? "" : current.getLyDo());

		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "Ma bien the", "So luong" }, 0);
		List<ChiTietPhieuXuat> details = current == null
				? new ArrayList<>() : phieuXuatTraController.getDetails(current.getMaPhieuTra());
		for (ChiTietPhieuXuat detail : details) {
			detailModel.addRow(new Object[] {
					detail.getMaBienThe(),
					detail.getSoLuong()
			});
		}
		if (detailModel.getRowCount() == 0) {
			detailModel.addRow(new Object[] { "", 1 });
		}

		JTable detailTable = new JTable(detailModel);
		detailTable.setRowHeight(26);
		detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane detailScroll = new JScrollPane(detailTable);
		detailScroll.setPreferredSize(new Dimension(520, 140));

		JButton addRow = new JButton("Them dong");
		addRow.addActionListener(e -> detailModel.addRow(new Object[] { "", 1 }));
		JButton removeRow = new JButton("Xoa dong");
		removeRow.addActionListener(e -> {
			int selectedRow = detailTable.getSelectedRow();
			if (selectedRow >= 0) {
				detailModel.removeRow(detailTable.convertRowIndexToModel(selectedRow));
			}
		});

		JPanel fields = new JPanel(new GridLayout(0, 2, 6, 6));
		fields.add(new JLabel("Ma phieu tra"));
		fields.add(maPhieuTra);
		fields.add(new JLabel("Ma NCC"));
		fields.add(maNCC);
		fields.add(new JLabel("Ma NV"));
		fields.add(maNV);
		fields.add(new JLabel("Ngay tra (dd/MM/yyyy)"));
		fields.add(ngayTra);
		fields.add(new JLabel("Ly do"));
		fields.add(lyDo);

		JPanel detailActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		detailActions.add(addRow);
		detailActions.add(removeRow);

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.add(new JLabel("Danh sach san pham xuat tra"), BorderLayout.NORTH);
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(detailActions, BorderLayout.SOUTH);

		JPanel form = new JPanel(new BorderLayout(8, 8));
		form.add(fields, BorderLayout.NORTH);
		form.add(detailPanel, BorderLayout.CENTER);
		form.setPreferredSize(new Dimension(560, 350));

		while (true) {
			int result = JOptionPane.showConfirmDialog(this, form,
					current == null ? "Them phieu xuat tra" : "Sua phieu xuat tra",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION) {
				return null;
			}
			try {
				if (detailTable.isEditing()) {
					detailTable.getCellEditor().stopCellEditing();
				}
				Date date = ngayTra.getText().trim().isEmpty() ? new Date()
						: dateFormat.parse(ngayTra.getText().trim());
				List<ChiTietPhieuXuat> parsedDetails = parseDetails(detailModel, maPhieuTra.getText().trim());
				return new PhieuXuatTra(maPhieuTra.getText().trim(), maNCC.getText().trim(),
						maNV.getText().trim(), date, lyDo.getText().trim(), parsedDetails);
			} catch (Exception ex) {
				showError(ex);
			}
		}
	}

	private List<ChiTietPhieuXuat> parseDetails(DefaultTableModel detailModel, String maPhieuTra) {
		List<ChiTietPhieuXuat> details = new ArrayList<>();
		for (int row = 0; row < detailModel.getRowCount(); row++) {
			String maBienThe = textValue(detailModel.getValueAt(row, 0));
			String soLuongText = textValue(detailModel.getValueAt(row, 1));
			if (maBienThe.isEmpty() && soLuongText.isEmpty()) {
				continue;
			}
			try {
				int soLuong = Integer.parseInt(soLuongText);
				details.add(new ChiTietPhieuXuat(maPhieuTra, maBienThe, soLuong));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("So luong phai la so hop le.");
			}
		}
		if (details.isEmpty()) {
			throw new IllegalArgumentException("Phieu xuat tra phai co it nhat mot san pham.");
		}
		return details;
	}

	private String textValue(Object value) {
		return value == null ? "" : value.toString().trim();
	}

	private String getCurrentEmployeeId() {
		TaiKhoan user = SessionManager.getCurrentUser();
		return user == null || user.getMaNV() == null ? "" : user.getMaNV();
	}

	private void showPrintDialog(String content, String title) {
		JTextArea preview = new JTextArea(content, 24, 76);
		preview.setEditable(false);
		preview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(preview);
		int option = JOptionPane.showConfirmDialog(this, scrollPane, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (option != JOptionPane.OK_OPTION) {
			return;
		}
		try {
			boolean completed = preview.print();
			if (completed) {
				JOptionPane.showMessageDialog(this, "In phieu xuat tra thanh cong.");
			}
		} catch (PrinterException ex) {
			JOptionPane.showMessageDialog(this, "Khong the in phieu: " + ex.getMessage(),
					"Loi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showError(Exception ex) {
		JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
	}
}
