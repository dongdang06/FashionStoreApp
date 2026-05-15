 
package com.fashionstore.view.nvbanhang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.DonHangController;
import com.fashionstore.model.DonHangSummary;

public class DonHangPanel extends JPanel {
	private final DonHangController donHangController = new DonHangController();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Ma don", "Nhan vien", "Tong tien", "Trang thai" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public DonHangPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JLabel title = new JLabel("Danh sach don hang");
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
		tableModel.setRowCount(0);
		List<DonHangSummary> orders = donHangController.getRecentOrders(50);
		for (DonHangSummary summary : orders) {
			tableModel.addRow(new Object[] {
					summary.getMaDH(),
					summary.getNhanVien(),
					currency.format(summary.getTongTien()),
					summary.getTrangThai()
			});
		}
	}
}

