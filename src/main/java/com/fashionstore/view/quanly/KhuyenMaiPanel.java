
package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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

import com.fashionstore.controller.KhuyenMaiController;
import com.fashionstore.controller.BienTheSanPhamController;
import com.fashionstore.controller.SanPhamController;
import com.fashionstore.controller.ChiTietKhuyenMaiController;
import com.fashionstore.model.KhuyenMai;
import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.SanPham;
import com.fashionstore.model.ChiTietKhuyenMai;
import java.awt.Dimension;

public class KhuyenMaiPanel extends JPanel {
	private final KhuyenMaiController khuyenMaiController = new KhuyenMaiController();
	private final List<KhuyenMai> data = new ArrayList<>();
	private final DefaultTableModel tableModel = new DefaultTableModel(
			new Object[] { "Mã KM", "Tên KM", "Bắt đầu", "Kết thúc", "Mức giảm", "Trạng thái" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private final JTable table = new JTable(tableModel);

	public KhuyenMaiPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

		JLabel title = new JLabel("Khuyến mãi");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.add(title, BorderLayout.WEST);

		JButton refresh = new JButton("\u21BB");
		refresh.addActionListener(event -> reloadFromSource());
		JButton addButton = new JButton("Thêm");
		addButton.addActionListener(event -> addItem());
		JButton editButton = new JButton("Sửa");
		editButton.addActionListener(event -> editItem());
		JButton deleteButton = new JButton("Xóa");
		deleteButton.addActionListener(event -> deleteItem());

		boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Quan ly");
		addButton.setEnabled(canEdit);
		editButton.setEnabled(canEdit);
		deleteButton.setEnabled(canEdit);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		actions.setOpaque(false);
		actions.add(refresh);
		actions.add(addButton);
		actions.add(editButton);
		actions.add(deleteButton);
		javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(
				tableModel);
		table.setRowSorter(sorter);

		javax.swing.JPanel searchPanel = new javax.swing.JPanel(
				new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
		searchPanel.setOpaque(false);
		javax.swing.JTextField txtSearch = new javax.swing.JTextField(20);

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
					sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
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
				sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
				if (table.getRowCount() == 0) {
					JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp", "Thông báo", JOptionPane.WARNING_MESSAGE);
				}
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
		data.addAll(khuyenMaiController.getAll());
		reloadData();
	}

	public void reloadData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		if (data.isEmpty()) {
			data.addAll(khuyenMaiController.getAll());
		}
		tableModel.setRowCount(0);
		for (KhuyenMai km : data) {
			tableModel.addRow(new Object[] {
					km.getMaKM(),
					km.getTenKM(),
					km.getNgayBatDau() == null ? "" : dateFormat.format(km.getNgayBatDau()),
					km.getNgayKetThuc() == null ? "" : dateFormat.format(km.getNgayKetThuc()),
					currency.format(km.getMucGiamToiDa()),
					km.getTrangThaiKM()
			});
		}
	}

	private static class PromoWrapper {
		final KhuyenMai khuyenMai;
		final List<ChiTietKhuyenMai> details;

		PromoWrapper(KhuyenMai khuyenMai, List<ChiTietKhuyenMai> details) {
			this.khuyenMai = khuyenMai;
			this.details = details;
		}
	}

	private void addItem() {
		PromoWrapper wrapper = showForm(null);
		if (wrapper == null) {
			return;
		}
		try {
			if (khuyenMaiController.save(wrapper.khuyenMai)) {
				com.fashionstore.controller.ChiTietKhuyenMaiController ctController = new com.fashionstore.controller.ChiTietKhuyenMaiController();
				for (ChiTietKhuyenMai ct : wrapper.details) {
					ctController.save(ct);
				}
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!");
				data.clear();
				reloadData();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		KhuyenMai current = data.get(modelRow);
		PromoWrapper wrapper = showForm(current);
		if (wrapper == null) {
			return;
		}
		try {
			if (khuyenMaiController.update(wrapper.khuyenMai)) {
				com.fashionstore.controller.ChiTietKhuyenMaiController ctController = new com.fashionstore.controller.ChiTietKhuyenMaiController();
				// Xoá chi tiết cũ trước
				ctController.deleteByMaKM(wrapper.khuyenMai.getMaKM());
				// Lưu chi tiết mới
				for (ChiTietKhuyenMai ct : wrapper.details) {
					ctController.save(ct);
				}
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!");
				data.clear();
				reloadData();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteItem() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa.");
			return;
		}
		int modelRow = table.convertRowIndexToModel(row);
		KhuyenMai current = data.get(modelRow);
		int ok = JOptionPane.showConfirmDialog(this, "Xóa khuyến mãi đã chọn (Sẽ xóa cả chi tiết khuyến mãi)?",
				"Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			try {
				com.fashionstore.controller.ChiTietKhuyenMaiController ctController = new com.fashionstore.controller.ChiTietKhuyenMaiController();
				ctController.deleteByMaKM(current.getMaKM());
				if (khuyenMaiController.delete(current.getMaKM())) {
					JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!");
					data.clear();
					reloadData();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private PromoWrapper showForm(KhuyenMai current) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		JTextField maKM = new JTextField(
				current == null ? com.fashionstore.util.MaGenerator.nextMaKM() : current.getMaKM());
		maKM.setEditable(false);
		JTextField tenKM = new JTextField(current == null ? "" : current.getTenKM());
		com.toedter.calendar.JDateChooser batDauChooser = new com.toedter.calendar.JDateChooser();
		batDauChooser.setDateFormatString("dd/MM/yyyy");
		if (current != null && current.getNgayBatDau() != null) {
			batDauChooser.setDate(current.getNgayBatDau());
		}

		com.toedter.calendar.JDateChooser ketThucChooser = new com.toedter.calendar.JDateChooser();
		ketThucChooser.setDateFormatString("dd/MM/yyyy");
		if (current != null && current.getNgayKetThuc() != null) {
			ketThucChooser.setDate(current.getNgayKetThuc());
		}

		JTextField mucGiam = new JTextField(current == null ? "" : String.valueOf(current.getMucGiamToiDa()));
		JTextField trangThai = new JTextField();
		trangThai.setEditable(false);

		Runnable updateStatus = () -> {
			java.util.Date startDate = batDauChooser.getDate();
			java.util.Date endDate = ketThucChooser.getDate();
			if (startDate != null) {
				java.util.Calendar cal = java.util.Calendar.getInstance();
				
				// Get today's date truncated to midnight
				cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
				cal.set(java.util.Calendar.MINUTE, 0);
				cal.set(java.util.Calendar.SECOND, 0);
				cal.set(java.util.Calendar.MILLISECOND, 0);
				java.util.Date today = cal.getTime();

				// Truncate start date
				cal.setTime(startDate);
				cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
				cal.set(java.util.Calendar.MINUTE, 0);
				cal.set(java.util.Calendar.SECOND, 0);
				cal.set(java.util.Calendar.MILLISECOND, 0);
				java.util.Date truncatedStart = cal.getTime();

				// Truncate end date if not null
				java.util.Date truncatedEnd = null;
				if (endDate != null) {
					cal.setTime(endDate);
					cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
					cal.set(java.util.Calendar.MINUTE, 0);
					cal.set(java.util.Calendar.SECOND, 0);
					cal.set(java.util.Calendar.MILLISECOND, 0);
					truncatedEnd = cal.getTime();
				}

				if (truncatedEnd != null && truncatedEnd.before(today)) {
					trangThai.setText("Ket thuc");
				} else if (truncatedStart.after(today)) {
					trangThai.setText("Chua bat dau");
				} else {
					trangThai.setText("Dang dien ra");
				}
			} else {
				trangThai.setText("Dang dien ra");
			}
		};

		java.beans.PropertyChangeListener dateListener = evt -> {
			if ("date".equals(evt.getPropertyName())) {
				updateStatus.run();
			}
		};
		batDauChooser.addPropertyChangeListener("date", dateListener);
		ketThucChooser.addPropertyChangeListener("date", dateListener);
		updateStatus.run();

		JPanel infoPanel = new JPanel(new GridLayout(0, 2, 6, 6));
		infoPanel.add(new JLabel("Mã KM:"));
		infoPanel.add(maKM);
		infoPanel.add(new JLabel("Tên chương trình KM:"));
		infoPanel.add(tenKM);
		infoPanel.add(new JLabel("Ngày bắt đầu (dd/MM/yyyy):"));
		infoPanel.add(batDauChooser);
		infoPanel.add(new JLabel("Ngày kết thúc (dd/MM/yyyy):"));
		infoPanel.add(ketThucChooser);
		infoPanel.add(new JLabel("Mức giảm tối đa (VND):"));
		infoPanel.add(mucGiam);
		infoPanel.add(new JLabel("Trạng thái:"));
		infoPanel.add(trangThai);

		// Product selection table (apply by product variants)
		DefaultTableModel productModel = new DefaultTableModel(
				new Object[] { "Áp dụng", "Mã biến thể", "Tên sản phẩm", "Giá gốc", "Giá KM" }, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return Boolean.class;
				if (columnIndex == 3 || columnIndex == 4)
					return Long.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0 || column == 4;
			}
		};

		BienTheSanPhamController bienTheController = new BienTheSanPhamController();
		SanPhamController sanPhamController = new SanPhamController();
		ChiTietKhuyenMaiController ctController = new ChiTietKhuyenMaiController();

		List<SanPham> products = sanPhamController.getAll();
		List<BienTheSanPham> variants = bienTheController.getAll();

		java.util.Map<String, String> productNames = new java.util.HashMap<>();
		for (SanPham sp : products) {
			productNames.put(sp.getMaSP(), sp.getTenSP());
		}

		java.util.Map<String, Long> existingMap = new java.util.HashMap<>();
		if (current != null) {
			List<ChiTietKhuyenMai> existingDetails = ctController.getByMaKM(current.getMaKM());
			for (ChiTietKhuyenMai ct : existingDetails) {
				existingMap.put(ct.getMaBienThe(), ct.getGiaKhuyenMai());
			}
		}

		for (BienTheSanPham v : variants) {
			boolean selected = existingMap.containsKey(v.getMaBienThe());
			Long promoPrice = selected ? existingMap.get(v.getMaBienThe()) : 0L;
			String prodName = productNames.getOrDefault(v.getMaSP(), "Unknown");
			String displayName = prodName + " (" + v.getMauSac() + " - " + v.getKichThuoc() + ")";

			productModel.addRow(new Object[] {
					selected,
					v.getMaBienThe(),
					displayName,
					v.getGiaBan(),
					promoPrice
			});
		}

		JTable productTable = new JTable(productModel);
		productTable.setRowHeight(26);
		JScrollPane tableScroll = new JScrollPane(productTable);
		tableScroll.setPreferredSize(new Dimension(750, 300));

		JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
		tablePanel.add(new JLabel("Chọn sản phẩm áp dụng và nhập giá khuyến mãi cụ thể:"), BorderLayout.NORTH);
		tablePanel.add(tableScroll, BorderLayout.CENTER);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.add(infoPanel, BorderLayout.NORTH);
		mainPanel.add(tablePanel, BorderLayout.CENTER);

		int result = JOptionPane.showConfirmDialog(this, mainPanel,
				current == null ? "Thêm khuyến mãi" : "Sửa khuyến mãi",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result != JOptionPane.OK_OPTION) {
			return null;
		}

		if (productTable.isEditing()) {
			productTable.getCellEditor().stopCellEditing();
		}

		if (tenKM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên chương trình KM là bắt buộc.");
			return null;
		}

		try {
			long mucGiamValue = mucGiam.getText().trim().isEmpty() ? 0 : Long.parseLong(mucGiam.getText().trim());
			java.util.Date ngayBatDau = batDauChooser.getDate();
			java.util.Date ngayKetThuc = ketThucChooser.getDate();

			if (ngayBatDau == null) {
				JOptionPane.showMessageDialog(this, "Ngày bắt đầu là bắt buộc.");
				return null;
			}
			if (ngayKetThuc == null) {
				JOptionPane.showMessageDialog(this, "Ngày kết thúc là bắt buộc.");
				return null;
			}
			if (ngayBatDau.after(ngayKetThuc)) {
				JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc.");
				return null;
			}

			KhuyenMai km = new KhuyenMai(maKM.getText().trim(), tenKM.getText().trim(),
					ngayBatDau, ngayKetThuc, mucGiamValue, trangThai.getText().trim());

			List<ChiTietKhuyenMai> details = new ArrayList<>();
			for (int i = 0; i < productModel.getRowCount(); i++) {
				Boolean selected = (Boolean) productModel.getValueAt(i, 0);
				if (selected != null && selected) {
					String maBT = (String) productModel.getValueAt(i, 1);
					String displayName = (String) productModel.getValueAt(i, 2);
					long originalPrice = (Long) productModel.getValueAt(i, 3);
					Object val = productModel.getValueAt(i, 4);
					long giaKM = 0;
					if (val instanceof Number) {
						giaKM = ((Number) val).longValue();
					} else if (val != null) {
						giaKM = Long.parseLong(val.toString().trim());
					}

					if (giaKM <= 0) {
						JOptionPane.showMessageDialog(this,
								"Giá khuyến mãi của sản phẩm " + displayName + " phải lớn hơn 0!");
						return null;
					}

					if (giaKM >= originalPrice) {
						JOptionPane.showMessageDialog(this,
								"Giá khuyến mãi của sản phẩm " + displayName + " (" + giaKM
										+ ") phải nhỏ hơn giá gốc (" + originalPrice + ")!");
						return null;
					}

					long discountAmount = originalPrice - giaKM;
					if (mucGiamValue > 0 && discountAmount > mucGiamValue) {
						JOptionPane.showMessageDialog(this,
								"Mức giảm của sản phẩm " + displayName + " (" + discountAmount
										+ " VND) vượt quá mức giảm tối đa của chương trình (" + mucGiamValue + " VND)!");
						return null;
					}

					details.add(new ChiTietKhuyenMai(km.getMaKM(), maBT, giaKM));
				}
			}

			return new PromoWrapper(km, details);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
}
