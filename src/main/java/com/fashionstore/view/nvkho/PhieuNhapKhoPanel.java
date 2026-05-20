 
package com.fashionstore.view.nvkho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.fashionstore.controller.PhieuNhapKhoController;
import com.fashionstore.dao.PhieuNhapKhoDAO;
import com.fashionstore.model.ChiTietPhieuNhap;
import com.fashionstore.model.PhieuNhapKho;
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

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Them");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sua");
		editButton.addActionListener(event -> editItem());
		JButton printButton = new JButton("In");
		printButton.addActionListener(event -> printItem());

		boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Kho");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
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
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			}
		});
		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		header.add(searchPanel, BorderLayout.CENTER);

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
		showPhieuNhapDialog();
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can sua.");
			return;
		}
		JOptionPane.showMessageDialog(this, "Chuc nang sua phieu nhap dang phat trien.");
	}

	private void printItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chon dong can in.");
			return;
		}
		JOptionPane.showMessageDialog(this, "Chuc nang in dang phat trien.");
	}

	/**
	 * Dialog nhập phiếu nhập kho: thông tin phiếu + bảng chi tiết sản phẩm.
	 * Khi lưu sẽ gọi PhieuNhapKhoDAO.save() → trigger DB tự cộng tồn kho.
	 */
	private void showPhieuNhapDialog() {
		JDialog dialog = new JDialog(
				(java.awt.Window) javax.swing.SwingUtilities.getWindowAncestor(this),
				"Them Phieu Nhap Kho",
				java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setSize(760, 540);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new BorderLayout(10, 10));

		// --- Thông tin phiếu ---
		JPanel fields = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
		fields.setBorder(BorderFactory.createTitledBorder("Thong tin phieu nhap"));
		fields.setBackground(Color.WHITE);
		fields.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thong tin phieu nhap"),
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));

		JTextField txtMaNCC = new JTextField();
		String maNVLogin = SessionManager.getCurrentUser() != null
				? SessionManager.getCurrentUser().getMaNV() : "";
		JTextField txtMaNV = new JTextField(maNVLogin);
		txtMaNV.setEditable(false);
		txtMaNV.setBackground(new Color(230, 230, 230));

		fields.add(new JLabel("Ma NCC:"));
		fields.add(txtMaNCC);
		fields.add(new JLabel("Ma NV (tu dong):"));
		fields.add(txtMaNV);

		// --- Bảng chi tiết ---
		DefaultTableModel ctModel = new DefaultTableModel(
				new Object[] { "Ma Bien The", "So Luong Nhap", "Gia Nhap (VND)" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) { return true; }
		};
		ctModel.addRow(new Object[] { "", "", "" }); // dòng trống đầu tiên

		JTable ctTable = new JTable(ctModel);
		ctTable.setRowHeight(26);
		ctTable.getColumnModel().getColumn(0).setPreferredWidth(140);
		ctTable.getColumnModel().getColumn(1).setPreferredWidth(130);
		ctTable.getColumnModel().getColumn(2).setPreferredWidth(160);

		JPanel ctPanel = new JPanel(new BorderLayout(6, 6));
		ctPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Chi tiet san pham nhap"),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		ctPanel.setBackground(Color.WHITE);

		JPanel ctButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		ctButtons.setOpaque(false);
		JButton btnAddRow = new JButton("+ Them dong");
		JButton btnDelRow = new JButton("- Xoa dong");
		btnAddRow.addActionListener(e -> ctModel.addRow(new Object[] { "", "", "" }));
		btnDelRow.addActionListener(e -> {
			int r = ctTable.getSelectedRow();
			if (r >= 0) ctModel.removeRow(r);
		});
		ctButtons.add(btnAddRow);
		ctButtons.add(btnDelRow);
		ctPanel.add(ctButtons, BorderLayout.NORTH);
		ctPanel.add(new JScrollPane(ctTable), BorderLayout.CENTER);

		// --- Nút Lưu / Hủy ---
		JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
		JButton btnSave   = new JButton("Luu phieu nhap");
		JButton btnCancel = new JButton("Huy");
		btnSave.setBackground(new Color(46, 204, 113));
		btnSave.setForeground(Color.WHITE);
		btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnSave.setPreferredSize(new Dimension(150, 36));
		btnCancel.setPreferredSize(new Dimension(80, 36));
		bottomButtons.add(btnCancel);
		bottomButtons.add(btnSave);

		btnCancel.addActionListener(e -> dialog.dispose());

		btnSave.addActionListener(e -> {
			// Commit ô đang edit
			if (ctTable.isEditing()) ctTable.getCellEditor().stopCellEditing();

			String maNCC = txtMaNCC.getText().trim().toUpperCase();
			String maNV  = txtMaNV.getText().trim();

			if (maNCC.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Vui long nhap Ma NCC!");
				return;
			}

			// Đọc chi tiết từ bảng
			List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();
			for (int r = 0; r < ctModel.getRowCount(); r++) {
				Object v0 = ctModel.getValueAt(r, 0);
				Object v1 = ctModel.getValueAt(r, 1);
				Object v2 = ctModel.getValueAt(r, 2);
				String maBT = (v0 == null ? "" : v0.toString().trim());
				String slStr = (v1 == null ? "" : v1.toString().trim());
				String giStr = (v2 == null ? "" : v2.toString().trim().replace(",", "").replace(".", ""));
				if (maBT.isEmpty()) continue;
				try {
					int sl   = Integer.parseInt(slStr);
					long gia = Long.parseLong(giStr);
					if (sl <= 0 || gia <= 0) throw new NumberFormatException();
					chiTietList.add(new ChiTietPhieuNhap(null, maBT.toUpperCase(), sl, gia));
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(dialog,
							"Dong " + (r + 1) + ": So luong va gia nhap phai la so nguyen duong!");
					return;
				}
			}

			if (chiTietList.isEmpty()) {
				JOptionPane.showMessageDialog(dialog, "Vui long nhap it nhat 1 san pham!");
				return;
			}

			PhieuNhapKho pn = new PhieuNhapKho(null, new java.util.Date(), 0L, maNCC, maNV);
			try {
				new PhieuNhapKhoDAO().save(pn, chiTietList);
				JOptionPane.showMessageDialog(dialog,
						"Luu phieu nhap thanh cong!\nMa PN: " + pn.getMaPN());
				dialog.dispose();
				reloadFromSource();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(dialog,
						"Loi khi luu:\n" + ex.getMessage(),
						"Loi", JOptionPane.ERROR_MESSAGE);
			}
		});

		// Ghép layout
		JPanel center = new JPanel(new BorderLayout(8, 8));
		center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		center.add(fields, BorderLayout.NORTH);
		center.add(ctPanel, BorderLayout.CENTER);

		dialog.add(center, BorderLayout.CENTER);
		dialog.add(bottomButtons, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}
}
