 
package com.fashionstore.view.nvbanhang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.HoaDonController;
import com.fashionstore.model.HoaDonSummary;

public class HoaDonPanel extends JPanel {
	private final HoaDonController hoaDonController = new HoaDonController();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma HD", "Ma don", "Ngay xuat", "Tong tien", "Phuong thuc", "Nhan vien" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public HoaDonPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JLabel title = new JLabel("Danh sach hoa don");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		title.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JTable table = new JTable(tableModel);
		table.setRowHeight(28);
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

		add(title, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		reloadData();
	}

	public void reloadData() {
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		tableModel.setRowCount(0);
		List<HoaDonSummary> invoices = hoaDonController.getRecentInvoices(50);
		for (HoaDonSummary summary : invoices) {
			tableModel.addRow(new Object[] {
					summary.getMaHD(),
					summary.getMaDH(),
					summary.getNgayXuat() == null ? "" : dateFormat.format(summary.getNgayXuat()),
					currency.format(summary.getTongTien()),
					summary.getPhuongThucTT(),
					summary.getNhanVien()
			});
		}
	}
}

