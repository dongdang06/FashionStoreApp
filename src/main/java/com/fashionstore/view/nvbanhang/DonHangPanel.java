
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
			new Object[] { "Mã đơn", "Ngày mua", "Nhân viên", "Tổng tiền", "Trạng thái" }, 0) {
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

		JLabel title = new JLabel("Danh sách đơn hàng");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		javax.swing.JButton refresh = new javax.swing.JButton("\u21BB");
		refresh.addActionListener(event -> reloadData());
		javax.swing.JButton addButton = new javax.swing.JButton("Tạo đơn");
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

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
					viewItem(table);
				}
			}
		});

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		JTextField txtSearch = new JTextField(20);

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
				String text = txtSearch.getText();
				if (text.trim().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
		});

		javax.swing.JButton btnSearch = new javax.swing.JButton("Tra cứu");
		txtSearch.addActionListener(e -> btnSearch.doClick());
		btnSearch.addActionListener(e -> {
			String text = txtSearch.getText();
			if (text.trim().length() == 0) {
				sorter.setRowFilter(null);
			} else {
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				if (table.getRowCount() == 0) {
					javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp", "Thông báo", javax.swing.JOptionPane.WARNING_MESSAGE);
				}
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
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		tableModel.setRowCount(0);
		List<DonHangSummary> orders = donHangController.getRecentOrders(50);
		for (DonHangSummary summary : orders) {
			tableModel.addRow(new Object[] {
					summary.getMaDH(),
					summary.getNgayMua() != null ? dateFormat.format(summary.getNgayMua()) : "",
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

	private void viewItem(JTable table) {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		String maDH = tableModel.getValueAt(modelRow, 0).toString();

		com.fashionstore.model.DonHang order = donHangController.getById(maDH);
		if (order == null) {
			javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đơn hàng.", "Lỗi",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			return;
		}

		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

		// Load employee name
		String empName = new com.fashionstore.dao.NhanVienDAO().getHoTenByMaNV(order.getMaNV());
		if (empName == null)
			empName = order.getMaNV();

		// Load customer name
		String custName = "Khách vãng lai";
		if (order.getMaKH() != null) {
			com.fashionstore.model.KhachHang kh = new com.fashionstore.dao.KhachHangDAO()
					.getCustomerByMa(order.getMaKH());
			if (kh != null) {
				custName = kh.getHoTen() + " (" + kh.getSdt() + ")";
			} else {
				custName = order.getMaKH();
			}
		}

		List<Object[]> details = donHangController.getDetailsForDisplay(maDH);
		long originalTotal = 0;
		long promoDiscount = 0;
		long calculatedTotal = 0;
		for (Object[] detail : details) {
			int qty = (Integer) detail[4];
			long price = (Long) detail[5];
			long originalPrice = (Long) detail[6];
			originalTotal += qty * originalPrice;
			promoDiscount += qty * (originalPrice - price);
			calculatedTotal += qty * price;
		}
		long pointsDiscount = (long) order.getDiemSuDung() * 100;
		long totalDiscount = promoDiscount + pointsDiscount;
		long finalPayment = calculatedTotal - pointsDiscount;
		if (finalPayment < 0) {
			finalPayment = 0;
		}

		// Header info panel
		JPanel infoPanel = new JPanel(new java.awt.GridLayout(0, 2, 8, 6));
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thông tin đơn hàng"),
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));

		infoPanel.add(new JLabel("Mã đơn hàng:"));
		infoPanel.add(new JLabel(order.getMaDH()));
		infoPanel.add(new JLabel("Ngày mua:"));
		infoPanel.add(new JLabel(order.getNgayMua() == null ? "" : dateFormat.format(order.getNgayMua())));
		infoPanel.add(new JLabel("Nhân viên bán hàng:"));
		infoPanel.add(new JLabel(empName));
		infoPanel.add(new JLabel("Khách hàng:"));
		infoPanel.add(new JLabel(custName));
		infoPanel.add(new JLabel("Mã khuyến mãi:"));
		infoPanel.add(new JLabel(order.getMaKM() == null ? "Không áp dụng" : order.getMaKM()));
		infoPanel.add(new JLabel("Điểm tích lũy nhận được:"));
		infoPanel.add(new JLabel("+" + order.getDiemNhanDuoc() + " điểm"));

		JLabel lblGocTitle = new JLabel("Tổng tiền hàng gốc:");
		lblGocTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel lblGocVal = new JLabel(currency.format(originalTotal) + " đ");
		lblGocVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
		infoPanel.add(lblGocTitle);
		infoPanel.add(lblGocVal);

		if (promoDiscount > 0) {
			infoPanel.add(new JLabel("Giảm giá khuyến mãi:"));
			infoPanel.add(new JLabel("-" + currency.format(promoDiscount) + " đ"));
		}

		if (pointsDiscount > 0) {
			infoPanel.add(new JLabel("Giảm giá tích lũy (điểm):"));
			infoPanel.add(new JLabel("-" + currency.format(pointsDiscount) + " đ (sử dụng " + order.getDiemSuDung() + " điểm)"));
		}

		if (totalDiscount > 0) {
			JLabel lblGiamTitle = new JLabel("Tổng số tiền được giảm:");
			lblGiamTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
			JLabel lblGiamVal = new JLabel("-" + currency.format(totalDiscount) + " đ");
			lblGiamVal.setForeground(new Color(39, 174, 96));
			lblGiamVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
			infoPanel.add(lblGiamTitle);
			infoPanel.add(lblGiamVal);
		}

		JLabel lblThanhToanTitle = new JLabel("Tổng thanh toán:");
		lblThanhToanTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		JLabel lblThanhToanVal = new JLabel(currency.format(finalPayment) + " đ");
		lblThanhToanVal.setForeground(Color.RED);
		lblThanhToanVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
		infoPanel.add(lblThanhToanTitle);
		infoPanel.add(lblThanhToanVal);

		String phuongThuc = donHangController.getPhuongThucTT(order.getMaDH());
		infoPanel.add(new JLabel("Hình thức thanh toán:"));
		infoPanel.add(new JLabel(phuongThuc));

		// Details table
		DefaultTableModel detailModel = new DefaultTableModel(
				new Object[] { "STT", "Mã biến thể", "Tên sản phẩm", "Màu sắc", "Kích thước", "Số lượng", "Đơn giá",
						"Thành tiền" },
				0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		int stt = 1;
		for (Object[] detail : details) {
			String maBienThe = (String) detail[0];
			String tenSP = (String) detail[1];
			String mau = (String) detail[2];
			String size = (String) detail[3];
			int qty = (Integer) detail[4];
			long price = (Long) detail[5];
			long lineTotal = qty * price;

			detailModel.addRow(new Object[] {
					stt++,
					maBienThe,
					tenSP,
					mau,
					size,
					qty,
					currency.format(price),
					currency.format(lineTotal)
			});
		}

		JTable detailTable = new JTable(detailModel);
		detailTable.setRowHeight(26);
		detailTable.setShowGrid(true);
		detailTable.setGridColor(new Color(220, 220, 220));
		detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		JScrollPane detailScroll = new JScrollPane(detailTable);
		detailScroll.setPreferredSize(new java.awt.Dimension(680, 200));

		// Total label
		JLabel totalLabel = new JLabel("Tổng cộng chi tiết: " + currency.format(calculatedTotal) + " VND");
		totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		totalLabel.setHorizontalAlignment(JLabel.RIGHT);
		totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 8));

		JPanel detailPanel = new JPanel(new BorderLayout(6, 6));
		detailPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Chi tiết sản phẩm đã mua"),
				BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		detailPanel.add(detailScroll, BorderLayout.CENTER);
		detailPanel.add(totalLabel, BorderLayout.SOUTH);

		// Main dialog panel
		JPanel dialogPanel = new JPanel(new BorderLayout(8, 10));
		dialogPanel.add(infoPanel, BorderLayout.NORTH);
		dialogPanel.add(detailPanel, BorderLayout.CENTER);
		dialogPanel.setPreferredSize(new java.awt.Dimension(720, 480));

		javax.swing.JOptionPane.showMessageDialog(this, dialogPanel,
				"Chi tiết đơn hàng - " + maDH,
				javax.swing.JOptionPane.PLAIN_MESSAGE);
	}
}
