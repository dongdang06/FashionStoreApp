package com.fashionstore.view.nvkho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import com.fashionstore.controller.PhieuNhapKhoController;
import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.PhieuNhapKho;
import com.fashionstore.model.TaiKhoan;
import com.fashionstore.util.MaGenerator;
import com.fashionstore.util.SessionManager;

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

		JTextField txtSearch = new JTextField(22);
		JButton btnSearch = new JButton("Tra cuu");
		btnSearch.addActionListener(e -> loadData(phieuNhapController.search(txtSearch.getText())));

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

		// Double-click to view details
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
					viewItem();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

		add(header, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		reloadData();
	}

	public void reloadData() {
		loadData(phieuNhapController.getAll());
	}

	private void viewItem() {
		PhieuNhapKho selected = getSelectedItem("xem");
		if (selected == null) {
			return;
		}
		PhieuNhapKho receipt = phieuNhapController.getById(selected.getMaPN());
		if (receipt == null) {
			JOptionPane.showMessageDialog(this, "Khong tim thay phieu nhap kho.", "Loi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		List<ChiTietPhieuNhap> details = phieuNhapController.getDetails(receipt.getMaPN());

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

		// Header info panel
		JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 6));
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thong tin phieu nhap"),
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));

		infoPanel.add(createLabel("Ma phieu nhap:", true));
		infoPanel.add(createLabel(receipt.getMaPN(), false));
		infoPanel.add(createLabel("Ngay nhap:", true));
		infoPanel.add(createLabel(receipt.getNgayNhap() == null ? "" : dateFormat.format(receipt.getNgayNhap()), false));
		infoPanel.add(createLabel("Ma nha cung cap:", true));
		infoPanel.add(createLabel(receipt.getMaNCC(), false));
		infoPanel.add(createLabel("Ma nhan vien:", true));
		infoPanel.add(createLabel(receipt.getMaNV(), false));
		infoPanel.add(createLabel("Tong gia tri:", true));
		infoPanel.add(createLabel(currency.format(receipt.getTongGiaTri()) + " VND", false));

		// Detail table
		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "STT", "Ma bien the", "So luong", "Gia nhap", "Thanh tien" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		long total = 0;
		int stt = 1;
		for (ChiTietPhieuNhap detail : details) {
			long thanhTien = detail.getThanhTien();
			total += thanhTien;
			detailModel.addRow(new Object[] {
					stt++,
					detail.getMaBienThe(),
					detail.getSoLuongNhap(),
					currency.format(detail.getGiaNhap()),
					currency.format(thanhTien)
			});
		}

		JTable detailTable = new JTable(detailModel);
		detailTable.setRowHeight(26);
		detailTable.setShowGrid(true);
		detailTable.setGridColor(new Color(220, 220, 220));
		detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		JScrollPane detailScroll = new JScrollPane(detailTable);
		detailScroll.setPreferredSize(new Dimension(560, 180));

		// Total label
		JLabel totalLabel = new JLabel("Tong cong: " + currency.format(total) + " VND");
		totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		totalLabel.setHorizontalAlignment(JLabel.RIGHT);
		totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 8));

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Chi tiet san pham nhap"),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(totalLabel, BorderLayout.SOUTH);

		// Main dialog panel
		JPanel dialogPanel = new JPanel(new BorderLayout(8, 10));
		dialogPanel.add(infoPanel, BorderLayout.NORTH);
		dialogPanel.add(detailPanel, BorderLayout.CENTER);
		dialogPanel.setPreferredSize(new Dimension(600, 400));

		JOptionPane.showMessageDialog(this, dialogPanel,
				"Chi tiet phieu nhap kho - " + receipt.getMaPN(),
				JOptionPane.PLAIN_MESSAGE);
	}

	private JLabel createLabel(String text, boolean bold) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 13));
		return label;
	}

	private void loadData(List<PhieuNhapKho> receipts) {
		data.clear();
		data.addAll(receipts);
		refreshTable();
	}

	private void refreshTable() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
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
		try {
			phieuNhapController.create(pn);
			JOptionPane.showMessageDialog(this, "Them moi phieu nhap kho thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void editItem() {
		PhieuNhapKho selected = getSelectedItem("sua");
		if (selected == null) {
			return;
		}
		PhieuNhapKho current = phieuNhapController.getById(selected.getMaPN());
		PhieuNhapKho updated = showForm(current == null ? selected : current);
		if (updated == null) {
			return;
		}
		try {
			phieuNhapController.update(updated);
			JOptionPane.showMessageDialog(this, "Cap nhat phieu nhap kho thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void deleteItem() {
		PhieuNhapKho selected = getSelectedItem("xoa");
		if (selected == null) {
			return;
		}
		int ok = JOptionPane.showConfirmDialog(this,
				"Xoa phieu nhap kho " + selected.getMaPN() + "?",
				"Xac nhan", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			phieuNhapController.delete(selected.getMaPN());
			JOptionPane.showMessageDialog(this, "Xoa phieu nhap kho thanh cong.");
			reloadData();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void printItem() {
		PhieuNhapKho selected = getSelectedItem("in");
		if (selected == null) {
			return;
		}
		try {
			showPrintDialog(phieuNhapController.buildPrintPreview(selected.getMaPN()), "In phieu nhap kho");
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private PhieuNhapKho getSelectedItem(String action) {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can " + action + ".");
			return null;
		}
		int modelRow = table.convertRowIndexToModel(row);
		return data.get(modelRow);
	}

	private PhieuNhapKho showForm(PhieuNhapKho current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);

		JTextField maPN = new JTextField(current == null ? MaGenerator.nextMaPN() : current.getMaPN());
		maPN.setEditable(false);
		JTextField ngayNhap = new JTextField(current == null || current.getNgayNhap() == null
				? dateFormat.format(new Date()) : dateFormat.format(current.getNgayNhap()));
		JTextField maNCC = new JTextField(current == null ? "" : current.getMaNCC());
		JTextField maNV = new JTextField(current == null ? getCurrentEmployeeId() : current.getMaNV());

		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "Ma bien the", "So luong", "Gia nhap" }, 0);
		List<ChiTietPhieuNhap> details = current == null
				? new ArrayList<>() : phieuNhapController.getDetails(current.getMaPN());
		for (ChiTietPhieuNhap detail : details) {
			detailModel.addRow(new Object[] {
					detail.getMaBienThe(),
					detail.getSoLuongNhap(),
					detail.getGiaNhap()
			});
		}
		if (detailModel.getRowCount() == 0) {
			detailModel.addRow(new Object[] { "", 1, 0 });
		}

		JTable detailTable = new JTable(detailModel);
		detailTable.setRowHeight(26);
		detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane detailScroll = new JScrollPane(detailTable);
		detailScroll.setPreferredSize(new Dimension(520, 150));

		JButton addRow = new JButton("Them dong");
		addRow.addActionListener(e -> detailModel.addRow(new Object[] { "", 1, 0 }));
		JButton removeRow = new JButton("Xoa dong");
		removeRow.addActionListener(e -> {
			int selectedRow = detailTable.getSelectedRow();
			if (selectedRow >= 0) {
				detailModel.removeRow(detailTable.convertRowIndexToModel(selectedRow));
			}
		});

		JPanel fields = new JPanel(new GridLayout(0, 2, 6, 6));
		fields.add(new JLabel("Ma PN"));
		fields.add(maPN);
		fields.add(new JLabel("Ngay nhap (dd/MM/yyyy)"));
		fields.add(ngayNhap);
		fields.add(new JLabel("Ma NCC"));
		fields.add(maNCC);
		fields.add(new JLabel("Ma NV"));
		fields.add(maNV);

		JPanel detailActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		detailActions.add(addRow);
		detailActions.add(removeRow);

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.add(new JLabel("Danh sach san pham nhap"), BorderLayout.NORTH);
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(detailActions, BorderLayout.SOUTH);

		JPanel form = new JPanel(new BorderLayout(8, 8));
		form.add(fields, BorderLayout.NORTH);
		form.add(detailPanel, BorderLayout.CENTER);
		form.setPreferredSize(new Dimension(560, 330));

		while (true) {
			int result = JOptionPane.showConfirmDialog(this, form,
					current == null ? "Them phieu nhap kho" : "Sua phieu nhap kho",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result != JOptionPane.OK_OPTION) {
				return null;
			}
			try {
				if (detailTable.isEditing()) {
					detailTable.getCellEditor().stopCellEditing();
				}
				Date date = ngayNhap.getText().trim().isEmpty() ? new Date()
						: dateFormat.parse(ngayNhap.getText().trim());
				List<ChiTietPhieuNhap> parsedDetails = parseDetails(detailModel, maPN.getText().trim());
				return new PhieuNhapKho(maPN.getText().trim(), date, 0,
						maNCC.getText().trim(), maNV.getText().trim(), parsedDetails);
			} catch (Exception ex) {
				showError(ex);
			}
		}
	}

	private List<ChiTietPhieuNhap> parseDetails(DefaultTableModel detailModel, String maPN) {
		List<ChiTietPhieuNhap> details = new ArrayList<>();
		for (int row = 0; row < detailModel.getRowCount(); row++) {
			String maBienThe = textValue(detailModel.getValueAt(row, 0));
			String soLuongText = textValue(detailModel.getValueAt(row, 1));
			String giaNhapText = textValue(detailModel.getValueAt(row, 2));
			if (maBienThe.isEmpty() && soLuongText.isEmpty() && giaNhapText.isEmpty()) {
				continue;
			}
			try {
				int soLuong = Integer.parseInt(soLuongText);
				long giaNhap = Long.parseLong(giaNhapText);
				details.add(new ChiTietPhieuNhap(maPN, maBienThe, soLuong, giaNhap));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("So luong va gia nhap phai la so hop le.");
			}
		}
		if (details.isEmpty()) {
			throw new IllegalArgumentException("Phieu nhap phai co it nhat mot san pham.");
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
				JOptionPane.showMessageDialog(this, "In phieu nhap kho thanh cong.");
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
