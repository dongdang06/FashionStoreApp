package com.fashionstore.view.nvbanhang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.fashionstore.controller.BienTheSanPhamController;
import com.fashionstore.controller.KhachHangController;
import com.fashionstore.controller.SanPhamController;
import com.fashionstore.controller.KhuyenMaiController;
import com.fashionstore.controller.ChiTietKhuyenMaiController;
import com.fashionstore.dao.DonHangDAO;
import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.ChiTietDonHang;
import com.fashionstore.model.DonHang;
import com.fashionstore.model.KhachHang;
import com.fashionstore.model.KhuyenMai;
import com.fashionstore.model.ChiTietKhuyenMai;
import com.fashionstore.model.SanPham;
import com.fashionstore.util.SessionManager;

public class TaoDonHangDialog extends JDialog {
	private final BienTheSanPhamController bienTheController = new BienTheSanPhamController();
	private final SanPhamController sanPhamController = new SanPhamController();
	private final KhachHangController khachHangController = new KhachHangController();
	private final KhuyenMaiController khuyenMaiController = new KhuyenMaiController();
	private final ChiTietKhuyenMaiController ctKhuyenMaiController = new ChiTietKhuyenMaiController();

	private DefaultTableModel productTableModel;
	private JTable productTable;
	private TableRowSorter<DefaultTableModel> productSorter;

	private DefaultTableModel cartTableModel;
	private JTable cartTable;

	private JLabel lblTotal;
	private long totalAmount = 0;

	private Map<String, String> productNames = new HashMap<>();
	private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

	private Map<String, Long> activePromoPrices = new HashMap<>();
	private Map<String, String> activePromoCodes = new HashMap<>();
	private Map<String, KhuyenMai> activePromotions = new HashMap<>();

	public TaoDonHangDialog(java.awt.Window owner) {
		super(owner, "Tạo Đơn Hàng (Bán Hàng POS)", ModalityType.APPLICATION_MODAL);
		setSize(1200, 700);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		loadProductNames();
		loadActivePromotions();
		initComponents();
		loadProducts();
	}

	private void loadActivePromotions() {
		java.util.Date today = new java.util.Date();
		List<KhuyenMai> allKM = khuyenMaiController.getAll();
		List<KhuyenMai> activeList = new ArrayList<>();
		for (KhuyenMai km : allKM) {
			boolean dateOk = (km.getNgayBatDau() == null || !today.before(km.getNgayBatDau())) &&
							 (km.getNgayKetThuc() == null || !today.after(km.getNgayKetThuc()));
			if (dateOk && !"Ket thuc".equalsIgnoreCase(km.getTrangThaiKM())) {
				activeList.add(km);
				activePromotions.put(km.getMaKM(), km);
			}
		}

		for (KhuyenMai km : activeList) {
			List<ChiTietKhuyenMai> details = ctKhuyenMaiController.getByMaKM(km.getMaKM());
			for (ChiTietKhuyenMai ct : details) {
				String maBT = ct.getMaBienThe();
				long promoPrice = ct.getGiaKhuyenMai();
				if (!activePromoPrices.containsKey(maBT) || promoPrice < activePromoPrices.get(maBT)) {
					activePromoPrices.put(maBT, promoPrice);
					activePromoCodes.put(maBT, km.getMaKM());
				}
			}
		}
	}

	private void loadProductNames() {
		List<SanPham> products = sanPhamController.getAll();
		for (SanPham sp : products) {
			productNames.put(sp.getMaSP(), sp.getTenSP());
		}
	}

	private void initComponents() {
		// --- LEFT PANEL: PRODUCTS ---
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
		leftPanel.setBackground(new Color(245, 246, 250));

		// Search bar
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchPanel.setOpaque(false);
		searchPanel.add(new JLabel("Tra cứu:"));
		JTextField txtSearch = new JTextField(20);
		searchPanel.add(txtSearch);

		productTableModel = new DefaultTableModel(
				new Object[] { "Mã BT", "Tên SP", "Màu", "Size", "Tồn", "Giá" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		productTable = new JTable(productTableModel);
		productTable.setRowHeight(28);
		productSorter = new TableRowSorter<>(productTableModel);
		productTable.setRowSorter(productSorter);

		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
			public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
			private void filter() {
				String text = txtSearch.getText();
				if (text.trim().length() == 0) {
					productSorter.setRowFilter(null);
				} else {
					productSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}
			}
		});

		productTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					addToCart();
				}
			}
		});

		leftPanel.add(searchPanel, BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

		// --- RIGHT PANEL: CART ---
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBorder(BorderFactory.createTitledBorder("Giỏ hàng"));
		rightPanel.setBackground(new Color(245, 246, 250));

		cartTableModel = new DefaultTableModel(
				new Object[] { "Mã BT", "Tên SP", "SL", "Đơn Giá", "Thành Tiền" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 2; // Only quantity is editable
			}
		};
		cartTable = new JTable(cartTableModel);
		cartTable.setRowHeight(28);

		cartTableModel.addTableModelListener(e -> {
			if (e.getColumn() == 2) {
				updateCartTotal();
			}
		});

		// Cart Actions
		JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cartActions.setOpaque(false);
		JButton btnRemove = new JButton("Xóa dòng");
		btnRemove.addActionListener(e -> removeFromCart());
		cartActions.add(btnRemove);

		rightPanel.add(cartActions, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

		// Payment Section
		JPanel paymentPanel = new JPanel(new GridLayout(2, 1));
		paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		lblTotal = new JLabel("Tổng tiền: 0 đ");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTotal.setForeground(Color.RED);
		lblTotal.setHorizontalAlignment(JLabel.RIGHT);

		JButton btnPay = new JButton("THANH TOÁN");
		btnPay.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnPay.setBackground(new Color(46, 204, 113));
		btnPay.setForeground(Color.WHITE);
		btnPay.setPreferredSize(new Dimension(0, 50));
		btnPay.addActionListener(e -> processPayment());

		paymentPanel.add(lblTotal);
		paymentPanel.add(btnPay);
		rightPanel.add(paymentPanel, BorderLayout.SOUTH);

		// --- SPLIT PANE ---
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerSize(5);

		add(splitPane, BorderLayout.CENTER);
	}

	private void loadProducts() {
		List<BienTheSanPham> variants = bienTheController.getAll();
		productTableModel.setRowCount(0);
		for (BienTheSanPham v : variants) {
			String name = productNames.getOrDefault(v.getMaSP(), "Unknown");
			long price = v.getGiaBan();
			if (activePromoPrices.containsKey(v.getMaBienThe())) {
				price = activePromoPrices.get(v.getMaBienThe());
				name = "🔥 [KM] " + name;
			}
			productTableModel.addRow(new Object[] {
					v.getMaBienThe(),
					name,
					v.getMauSac(),
					v.getKichThuoc(),
					v.getSoLuongTon(),
					price
			});
		}
	}

	private void addToCart() {
		int viewRow = productTable.getSelectedRow();
		if (viewRow < 0) return;
		int modelRow = productTable.convertRowIndexToModel(viewRow);

		String maBT = (String) productTableModel.getValueAt(modelRow, 0);
		String tenSP = (String) productTableModel.getValueAt(modelRow, 1) + " (" 
				+ productTableModel.getValueAt(modelRow, 2) + " - " 
				+ productTableModel.getValueAt(modelRow, 3) + ")";
		int tonKho = (int) productTableModel.getValueAt(modelRow, 4);
		long giaBan = (long) productTableModel.getValueAt(modelRow, 5);

		if (tonKho <= 0) {
			JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!");
			return;
		}

		// Check if already in cart
		for (int i = 0; i < cartTableModel.getRowCount(); i++) {
			if (cartTableModel.getValueAt(i, 0).equals(maBT)) {
				int qty = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
				if (qty >= tonKho) {
					JOptionPane.showMessageDialog(this, "Không đủ số lượng tồn kho!");
					return;
				}
				cartTableModel.setValueAt(qty + 1, i, 2);
				return;
			}
		}

		// Add new
		cartTableModel.addRow(new Object[] { maBT, tenSP, 1, giaBan, giaBan });
		updateCartTotal();
	}

	private void removeFromCart() {
		int row = cartTable.getSelectedRow();
		if (row >= 0) {
			cartTableModel.removeRow(row);
			updateCartTotal();
		}
	}

	private boolean isUpdatingCart = false;

	private int getStockForVariant(String maBT) {
		for (int i = 0; i < productTableModel.getRowCount(); i++) {
			if (productTableModel.getValueAt(i, 0).equals(maBT)) {
				return (int) productTableModel.getValueAt(i, 4);
			}
		}
		return 0;
	}

	private void updateCartTotal() {
		if (isUpdatingCart) return;
		isUpdatingCart = true;
		try {
			totalAmount = 0;
			for (int i = 0; i < cartTableModel.getRowCount(); i++) {
				try {
					String maBT = cartTableModel.getValueAt(i, 0).toString();
					int tonKho = getStockForVariant(maBT);
					int qty = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
					if (qty < 1) {
						qty = 1;
						cartTableModel.setValueAt(1, i, 2);
					} else if (qty > tonKho) {
						JOptionPane.showMessageDialog(this, "Không đủ số lượng tồn kho cho sản phẩm " + maBT + " (Tồn: " + tonKho + ")!");
						qty = tonKho;
						cartTableModel.setValueAt(tonKho, i, 2);
					}
					long price = (long) cartTableModel.getValueAt(i, 3);
					long subtotal = qty * price;
					cartTableModel.setValueAt(subtotal, i, 4);
					totalAmount += subtotal;
				} catch (Exception ex) {
					cartTableModel.setValueAt(1, i, 2);
				}
			}
			lblTotal.setText("Tổng tiền: " + currencyFormat.format(totalAmount) + " đ");
		} finally {
			isUpdatingCart = false;
		}
	}

	private Runnable onOrderCreated; // callback khi tạo đơn thành công

	public void setOnOrderCreated(Runnable callback) {
		this.onOrderCreated = callback;
	}

	private void processPayment() {
		if (cartTableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
			return;
		}

		// Bước nhập mã KH: không bắt buộc, chỉ dùng để tích điểm
		String maKH = JOptionPane.showInputDialog(this,
				"Nhập mã khách hàng để tích điểm (bỏ trống nếu không cần):",
				"Thông tin khách hàng (không bắt buộc)",
				JOptionPane.QUESTION_MESSAGE);

		// Nếu người dùng nhấn Cancel → hủy thanh toán
		if (maKH == null) return;

		// Xử lý mã KH: nếu rỗng thì đặt null (khách vãng lai)
		maKH = maKH.trim().isEmpty() ? null : maKH.trim().toUpperCase();

		// Tra cứu thông tin khách hàng nếu có mã KH
		KhachHang khachHang = null;
		if (maKH != null) {
			khachHang = khachHangController.getCustomerByMa(maKH);
			if (khachHang == null) {
				JOptionPane.showMessageDialog(this,
						"Không tìm thấy khách hàng với mã: " + maKH,
						"Lỗi", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		// === TỰ ĐỘNG XỬ LÝ KHUYẾN MÃI ===
		List<BienTheSanPham> variants = bienTheController.getAll();
		Map<String, Long> originalPrices = new HashMap<>();
		for (BienTheSanPham v : variants) {
			originalPrices.put(v.getMaBienThe(), v.getGiaBan());
		}

		long originalTotalAmount = 0;
		java.util.List<ChiTietDonHang> chiTietList = new java.util.ArrayList<>();
		java.util.Map<String, Long> discountByKM = new java.util.HashMap<>();

		for (int i = 0; i < cartTableModel.getRowCount(); i++) {
			String maBT = (String) cartTableModel.getValueAt(i, 0);
			int qty = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
			long originalPrice = originalPrices.getOrDefault(maBT, 0L);
			long promoPrice = activePromoPrices.getOrDefault(maBT, originalPrice);

			originalTotalAmount += originalPrice * qty;

			long unitDiscount = originalPrice - promoPrice;
			if (unitDiscount > 0) {
				String maKM = activePromoCodes.get(maBT);
				if (maKM != null) {
					discountByKM.put(maKM, discountByKM.getOrDefault(maKM, 0L) + (unitDiscount * qty));
				}
			}
			chiTietList.add(new ChiTietDonHang(null, maBT, qty, promoPrice));
		}

		java.util.Map<String, Long> cappedDiscountByKM = new java.util.HashMap<>();
		for (Map.Entry<String, Long> entry : discountByKM.entrySet()) {
			String maKM = entry.getKey();
			long totalDiscount = entry.getValue();
			KhuyenMai km = activePromotions.get(maKM);
			long maxDiscount = km != null ? km.getMucGiamToiDa() : 0;

			if (maxDiscount > 0 && totalDiscount > maxDiscount) {
				cappedDiscountByKM.put(maKM, maxDiscount);
				long excess = totalDiscount - maxDiscount;
				// Phân bổ lại phần vượt mức giảm tối đa của chương trình KM này
				for (ChiTietDonHang ct : chiTietList) {
					String maBT = ct.getMaBienThe();
					if (maKM.equals(activePromoCodes.get(maBT))) {
						long originalPrice = originalPrices.getOrDefault(maBT, 0L);
						long promoPrice = activePromoPrices.getOrDefault(maBT, originalPrice);
						long maxAddBackPerUnit = originalPrice - promoPrice;
						long totalMaxAddBack = maxAddBackPerUnit * ct.getSoLuong();

						if (excess <= totalMaxAddBack) {
							long unitAddBack = (excess + ct.getSoLuong() - 1) / ct.getSoLuong();
							ct.setGiaBanLucMua(Math.min(originalPrice, promoPrice + unitAddBack));
							excess = 0;
							break;
						} else {
							ct.setGiaBanLucMua(originalPrice);
							excess -= totalMaxAddBack;
						}
					}
				}
			} else {
				cappedDiscountByKM.put(maKM, totalDiscount);
			}
		}

		long totalAmountAfterKM = chiTietList.stream().mapToLong(ct -> ct.getSoLuong() * ct.getGiaBanLucMua()).sum();
		long thucGiamKM = originalTotalAmount - totalAmountAfterKM;

		// Chọn chương trình khuyến mãi chính (đóng góp nhiều tiền giảm nhất) để gán vào đơn hàng
		String mainMaKM = null;
		long maxPromoDiscount = 0;
		for (Map.Entry<String, Long> entry : cappedDiscountByKM.entrySet()) {
			if (entry.getValue() > maxPromoDiscount) {
				maxPromoDiscount = entry.getValue();
				mainMaKM = entry.getKey();
			}
		}

		// === XỬ LÝ ĐIỂM TÍCH LUỸ ===
		int diemSuDung = 0;
		long giamGiaDiem = 0;

		if (khachHang != null && khachHang.getDiemTichLuy() > 0) {
			int diemHienCo = khachHang.getDiemTichLuy();
			// Tối đa điểm có thể dùng: không vượt quá tổng tiền sau khuyến mãi quy đổi
			// 10 điểm = 1,000 VND → maxDiem = totalAmountAfterKM / 100
			int maxDiemTheoTien = (int) (totalAmountAfterKM / 100);
			int maxDiem = Math.min(diemHienCo, maxDiemTheoTien);

			long giaTriDiem = (long) diemHienCo * 100; // quy đổi ra VND

			if (maxDiem > 0) {
				int chon = JOptionPane.showConfirmDialog(this,
						"Khách hàng: " + khachHang.getHoTen() + " (" + maKH + ")\n"
						+ "Điểm tích luỹ hiện có: " + currencyFormat.format(diemHienCo) + " điểm\n"
						+ "Giá trị quy đổi tối đa: " + currencyFormat.format((long) maxDiem * 100) + " VND"
						+ " (10 điểm = 1,000 VND)\n\n"
						+ "Bạn có muốn sử dụng điểm tích luỹ để giảm giá không?",
						"Sử dụng điểm tích luỹ",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (chon == JOptionPane.YES_OPTION) {
					// Cho nhập số điểm muốn dùng
					String input = JOptionPane.showInputDialog(this,
							"Điểm hiện có: " + currencyFormat.format(diemHienCo) + " điểm\n"
							+ "Tối đa có thể dùng: " + currencyFormat.format(maxDiem) + " điểm"
							+ " (giảm " + currencyFormat.format((long) maxDiem * 100) + " VND)\n\n"
							+ "Nhập số điểm muốn sử dụng:",
							String.valueOf(maxDiem));

					if (input != null && !input.trim().isEmpty()) {
						try {
							diemSuDung = Integer.parseInt(input.trim());
							if (diemSuDung < 0) {
								JOptionPane.showMessageDialog(this, "Số điểm không được âm!");
								return;
							}
							if (diemSuDung > diemHienCo) {
								JOptionPane.showMessageDialog(this,
										"Không đủ điểm! Bạn chỉ có " + currencyFormat.format(diemHienCo) + " điểm.");
								return;
							}
							if (diemSuDung > maxDiemTheoTien) {
								JOptionPane.showMessageDialog(this,
										"Số điểm vượt quá giá trị đơn hàng sau khi giảm giá!\n"
										+ "Tối đa có thể dùng: " + currencyFormat.format(maxDiemTheoTien) + " điểm.");
								return;
							}
							giamGiaDiem = (long) diemSuDung * 100;
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(this, "Vui lòng nhập số điểm hợp lệ!");
							return;
						}
					}
				}
			}
		}

		long tongThanhToan = totalAmountAfterKM - giamGiaDiem;
		int diemNhanDuoc = (maKH != null) ? (int) (tongThanhToan / 100000) : 0;

		// Hỏi phương thức thanh toán
		Object[] options = { "Tiền mặt", "Chuyển khoản" };
		int option = JOptionPane.showOptionDialog(this,
				"Chọn phương thức thanh toán:",
				"Phương thức thanh toán",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);

		// Nếu đóng dialog hoặc không chọn -> hủy giao dịch
		if (option < 0) return;
		String phuongThucTT = (option == 0) ? "Tien mat" : "Chuyen khoan";
		String phuongThucTT_vn = (option == 0) ? "Tiền mặt" : "Chuyển khoản";

		// Hiện xác nhận thanh toán
		StringBuilder thongTin = new StringBuilder();
		if (maKH != null) {
			thongTin.append("Khách hàng: ").append(khachHang.getHoTen())
					.append(" (").append(maKH).append(")");
		} else {
			thongTin.append("Khách vãng lai (không tích điểm)");
		}
		thongTin.append("\nTổng tiền hàng gốc: ").append(currencyFormat.format(originalTotalAmount)).append(" đ");

		if (thucGiamKM > 0) {
			if (mainMaKM != null) {
				KhuyenMai mainKM = activePromotions.get(mainMaKM);
				thongTin.append("\nKhuyến mãi chính: ").append(mainKM.getTenKM())
						.append(" (").append(mainMaKM).append(")");
			}
			thongTin.append("\n  - Tổng giảm giá khuyến mãi: -").append(currencyFormat.format(thucGiamKM)).append(" đ");
		}

		if (diemSuDung > 0) {
			thongTin.append("\nĐiểm sử dụng: ").append(currencyFormat.format(diemSuDung))
					.append(" điểm (giảm -").append(currencyFormat.format(giamGiaDiem)).append(" đ)");
		}
		thongTin.append("\n─────────────────────────");
		thongTin.append("\nTổng thanh toán: ").append(currencyFormat.format(tongThanhToan)).append(" đ");

		if (maKH != null) {
			thongTin.append("\nĐiểm tích luỹ nhận được: +").append(diemNhanDuoc).append(" điểm");
		}

		thongTin.append("\nPhương thức: ").append(phuongThucTT_vn);
		thongTin.append("\n\nXác nhận chốt đơn?");

		int confirm = JOptionPane.showConfirmDialog(this,
				thongTin.toString(),
				"Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) return;

		// Tạo đối tượng DonHang
		String maNV = SessionManager.getCurrentUser() != null
				? SessionManager.getCurrentUser().getMaNV() : null;

		DonHang dh = new DonHang();
		dh.setMaKH(maKH);
		dh.setMaNV(maNV);
		dh.setMaKM(mainMaKM);
		dh.setDiemSuDung(diemSuDung);
		dh.setDiemNhanDuoc(diemNhanDuoc);

		// Lưu vào DB
		try {
			DonHangDAO dao = new DonHangDAO();
			dao.saveDonHang(dh, chiTietList, phuongThucTT);

			String msg = "✅ Thanh toán thành công!\nMã đơn hàng: " + dh.getMaDH();
			if (thucGiamKM > 0) {
				msg += "\nKhuyến mãi: -" + currencyFormat.format(thucGiamKM) + " đ";
			}
			if (maKH != null) {
				if (diemSuDung > 0) {
					msg += "\nĐiểm đã sử dụng: " + currencyFormat.format(diemSuDung)
							+ " (giảm " + currencyFormat.format(giamGiaDiem) + " đ)";
				}
				msg += "\nĐiểm tích lũy nhận được: +" + diemNhanDuoc;
			} else {
				msg += "\n(Khách vãng lai - không tích điểm)";
			}
			JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);

			// Thông báo cho panel cha reload danh sách đơn hàng
			if (onOrderCreated != null) {
				onOrderCreated.run();
			}
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Lỗi khi lưu đơn hàng:\n" + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}
