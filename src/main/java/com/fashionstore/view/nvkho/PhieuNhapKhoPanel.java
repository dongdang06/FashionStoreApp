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
			new Object[] { "Mã PN", "Ngày nhập", "Tổng giá trị", "Mã NCC", "Mã NV" }, 0) {
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

		JLabel title = new JLabel("Phiếu nhập kho");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JTextField txtSearch = new JTextField(22);
		JButton btnSearch = new JButton("Tra cứu");

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
				loadData(phieuNhapController.search(txtSearch.getText()));
			}
		});

		txtSearch.addActionListener(e -> btnSearch.doClick());
		btnSearch.addActionListener(e -> {
			String text = txtSearch.getText();
			List<PhieuNhapKho> results = phieuNhapController.search(text);
			loadData(results);
			if (results.isEmpty() && !text.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp", "Thông báo", JOptionPane.WARNING_MESSAGE);
			}
		});

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
		JButton addButton = new JButton("Thêm");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sửa");
		editButton.addActionListener(event -> editItem());
		JButton printButton = new JButton("In");
		printButton.addActionListener(event -> printItem());

		boolean canEdit = SessionManager.hasPermission("Kho");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
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
			JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		List<ChiTietPhieuNhap> details = phieuNhapController.getDetails(receipt.getMaPN());

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

		// Header info panel
		JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 6));
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thông tin phiếu nhập"),
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));

		infoPanel.add(createLabel("Mã phiếu nhập:", true));
		infoPanel.add(createLabel(receipt.getMaPN(), false));
		infoPanel.add(createLabel("Ngày nhập:", true));
		infoPanel
				.add(createLabel(receipt.getNgayNhap() == null ? "" : dateFormat.format(receipt.getNgayNhap()), false));
		infoPanel.add(createLabel("Mã nhà cung cấp:", true));
		infoPanel.add(createLabel(receipt.getMaNCC(), false));
		infoPanel.add(createLabel("Mã nhân viên:", true));
		infoPanel.add(createLabel(receipt.getMaNV(), false));
		infoPanel.add(createLabel("Tổng giá trị:", true));
		infoPanel.add(createLabel(currency.format(receipt.getTongGiaTri()) + " VND", false));

		// Detail table
		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "STT", "Mã biến thể", "Số lượng", "Giá nhập", "Thành tiền" }, 0) {
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
		JLabel totalLabel = new JLabel("Tổng cộng: " + currency.format(total) + " VND");
		totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		totalLabel.setHorizontalAlignment(JLabel.RIGHT);
		totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 8));

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Chi tiết sản phẩm nhập"),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(totalLabel, BorderLayout.SOUTH);

		// Main dialog panel
		JPanel dialogPanel = new JPanel(new BorderLayout(8, 10));
		dialogPanel.add(infoPanel, BorderLayout.NORTH);
		dialogPanel.add(detailPanel, BorderLayout.CENTER);
		dialogPanel.setPreferredSize(new Dimension(600, 400));

		JOptionPane.showMessageDialog(this, dialogPanel,
				"Chi tiết phiếu nhập kho - " + receipt.getMaPN(),
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
			JOptionPane.showMessageDialog(this, "Thêm mới phiếu nhập kho thành công.");
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
			JOptionPane.showMessageDialog(this, "Cập nhật phiếu nhập kho thành công.");
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
			showPrintDialog(phieuNhapController.buildPrintPreview(selected.getMaPN()), "In phiếu nhập kho");
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private PhieuNhapKho getSelectedItem(String action) {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần " + action + ".");
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
				? dateFormat.format(new Date())
				: dateFormat.format(current.getNgayNhap()));

		// Supplier dropdown
		com.fashionstore.controller.NhaCungCapController nccController = new com.fashionstore.controller.NhaCungCapController();
		List<com.fashionstore.model.NhaCungCap> listNCC = nccController.getAll();
		javax.swing.JComboBox<String> cbNCC = new javax.swing.JComboBox<>();
		for (com.fashionstore.model.NhaCungCap ncc : listNCC) {
			cbNCC.addItem(ncc.getMaNCC() + " - " + ncc.getTenNCC());
		}
		if (current != null) {
			for (int i = 0; i < cbNCC.getItemCount(); i++) {
				if (cbNCC.getItemAt(i).startsWith(current.getMaNCC() + " -")) {
					cbNCC.setSelectedIndex(i);
					break;
				}
			}
		}

		JTextField maNV = new JTextField(current == null ? getCurrentEmployeeId() : current.getMaNV());
		maNV.setEditable(false); // Lock employee code!

		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "Mã biến thể", "Số lượng", "Giá nhập" }, 0);
		List<ChiTietPhieuNhap> details = current == null
				? new ArrayList<>()
				: phieuNhapController.getDetails(current.getMaPN());
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

		// Variant dropdown editor for table
		com.fashionstore.controller.BienTheSanPhamController bienTheController = new com.fashionstore.controller.BienTheSanPhamController();
		com.fashionstore.controller.SanPhamController sanPhamController = new com.fashionstore.controller.SanPhamController();
		List<com.fashionstore.model.BienTheSanPham> listVariants = bienTheController.getAll();
		List<com.fashionstore.model.SanPham> listProducts = sanPhamController.getAll();

		java.util.Map<String, String> productNames = new java.util.HashMap<>();
		for (com.fashionstore.model.SanPham sp : listProducts) {
			productNames.put(sp.getMaSP(), sp.getTenSP());
		}

		javax.swing.JComboBox<String> cbVariants = new javax.swing.JComboBox<>();
		for (com.fashionstore.model.BienTheSanPham bt : listVariants) {
			String prodName = productNames.getOrDefault(bt.getMaSP(), "Unknown");
			cbVariants.addItem(bt.getMaBienThe() + " - " + prodName + " (" + bt.getMauSac() + " - " + bt.getKichThuoc() + ")");
		}
		detailTable.getColumnModel().getColumn(0).setCellEditor(new javax.swing.DefaultCellEditor(cbVariants));

		JScrollPane detailScroll = new JScrollPane(detailTable);
		detailScroll.setPreferredSize(new Dimension(520, 150));

		JButton addRow = new JButton("Thêm dòng");
		addRow.addActionListener(e -> detailModel.addRow(new Object[] { "", 1, 0 }));
		JButton removeRow = new JButton("Xóa dòng");
		removeRow.addActionListener(e -> {
			int selectedRow = detailTable.getSelectedRow();
			if (selectedRow >= 0) {
				detailModel.removeRow(detailTable.convertRowIndexToModel(selectedRow));
			}
		});

		JPanel fields = new JPanel(new GridLayout(0, 2, 6, 6));
		fields.add(new JLabel("Mã PN"));
		fields.add(maPN);
		fields.add(new JLabel("Ngày nhập (dd/MM/yyyy)"));
		fields.add(ngayNhap);
		fields.add(new JLabel("Mã NCC"));
		fields.add(cbNCC);
		fields.add(new JLabel("Mã NV"));
		fields.add(maNV);

		JPanel detailActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		detailActions.add(addRow);
		detailActions.add(removeRow);

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.add(new JLabel("Danh sách sản phẩm nhập"), BorderLayout.NORTH);
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(detailActions, BorderLayout.SOUTH);

		JPanel form = new JPanel(new BorderLayout(8, 8));
		form.add(fields, BorderLayout.NORTH);
		form.add(detailPanel, BorderLayout.CENTER);
		form.setPreferredSize(new Dimension(560, 330));

		while (true) {
			int result = JOptionPane.showConfirmDialog(this, form,
					current == null ? "Thêm phiếu nhập kho" : "Sửa phiếu nhập kho",
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

				String selectedNCC = cbNCC.getSelectedItem() != null ? cbNCC.getSelectedItem().toString() : "";
				String nccCode = selectedNCC.contains(" -") ? selectedNCC.split(" -")[0].trim() : selectedNCC.trim();

				return new PhieuNhapKho(maPN.getText().trim(), date, 0,
						nccCode, maNV.getText().trim(), parsedDetails);
			} catch (Exception ex) {
				showError(ex);
			}
		}
	}

	private List<ChiTietPhieuNhap> parseDetails(DefaultTableModel detailModel, String maPN) {
		List<ChiTietPhieuNhap> details = new ArrayList<>();
		for (int row = 0; row < detailModel.getRowCount(); row++) {
			String maBienTheRaw = textValue(detailModel.getValueAt(row, 0));
			String maBienThe = maBienTheRaw.contains(" -") ? maBienTheRaw.split(" -")[0].trim() : maBienTheRaw;
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
				throw new IllegalArgumentException("Số lượng và giá nhập phải là số hợp lệ.");
			}
		}
		if (details.isEmpty()) {
			throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một sản phẩm.");
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
				JOptionPane.showMessageDialog(this, "In phiếu nhập kho thành công.");
			}
		} catch (PrinterException ex) {
			JOptionPane.showMessageDialog(this, "Không thể in phiếu: " + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showError(Exception ex) {
		JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
	}
}
