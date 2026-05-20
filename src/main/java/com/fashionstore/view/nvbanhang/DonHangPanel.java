 
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
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

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

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Danh sach don hang");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		javax.swing.JButton refresh = new javax.swing.JButton("\u21BB");
		refresh.addActionListener(event -> reloadData());
		javax.swing.JButton addButton = new javax.swing.JButton("Tao don");
		boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Ban hang");
		addButton.setEnabled(canEdit);
		addButton.addActionListener(event -> openTaoDonHangDialog());

		JPanel actions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
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

	private void openTaoDonHangDialog() {
		java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
		TaoDonHangDialog dialog = new TaoDonHangDialog(win);
		dialog.setOnOrderCreated(() -> reloadData());
		dialog.setVisible(true);
		reloadData(); // Reload orders in case a new one was added
	}
}

