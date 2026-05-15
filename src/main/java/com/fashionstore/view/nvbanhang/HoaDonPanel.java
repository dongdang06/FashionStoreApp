 
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
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

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

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Danh sach hoa don");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		javax.swing.JButton refresh = new javax.swing.JButton("Lam moi");
		refresh.addActionListener(event -> reloadData());
		javax.swing.JButton addButton = new javax.swing.JButton("Tao moi");
		addButton.addActionListener(event -> javax.swing.JOptionPane.showMessageDialog(this, "Chuc nang tao moi dang phat trien."));
		javax.swing.JButton printButton = new javax.swing.JButton("In");
		printButton.addActionListener(event -> javax.swing.JOptionPane.showMessageDialog(this, "Chuc nang in dang phat trien."));

		JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(printButton);
		header.add(actions, BorderLayout.EAST);

		JTable table = new JTable(tableModel);
		table.setRowHeight(28);
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		JTextField txtSearch = new JTextField(20);
		javax.swing.JButton btnSearch = new javax.swing.JButton("Tra cuu");
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

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

		add(header, BorderLayout.NORTH);
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

