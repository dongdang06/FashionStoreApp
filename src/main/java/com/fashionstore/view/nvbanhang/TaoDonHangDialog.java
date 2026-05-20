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
import com.fashionstore.controller.SanPhamController;
import com.fashionstore.dao.DonHangDAO;
import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.ChiTietDonHang;
import com.fashionstore.model.DonHang;
import com.fashionstore.model.SanPham;
import com.fashionstore.util.SessionManager;

public class TaoDonHangDialog extends JDialog {
	private final BienTheSanPhamController bienTheController = new BienTheSanPhamController();
	private final SanPhamController sanPhamController = new SanPhamController();

	private DefaultTableModel productTableModel;
	private JTable productTable;
	private TableRowSorter<DefaultTableModel> productSorter;

	private DefaultTableModel cartTableModel;
	private JTable cartTable;

	private JLabel lblTotal;
	private long totalAmount = 0;

	private Map<String, String> productNames = new HashMap<>();
	private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

	public TaoDonHangDialog(java.awt.Window owner) {
		super(owner, "Tạo Đơn Hàng (Bán Hàng POS)", ModalityType.APPLICATION_MODAL);
		setSize(1200, 700);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		loadProductNames();
		initComponents();
		loadProducts();
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
			productTableModel.addRow(new Object[] {
					v.getMaBienThe(),
					name,
					v.getMauSac(),
					v.getKichThuoc(),
					v.getSoLuongTon(),
					v.getGiaBan()
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

	private void updateCartTotal() {
		totalAmount = 0;
		for (int i = 0; i < cartTableModel.getRowCount(); i++) {
			try {
				int qty = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
				if (qty < 1) {
					qty = 1;
					cartTableModel.setValueAt(1, i, 2);
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
	}

	private void processPayment() {
		if (cartTableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
			return;
		}

		// Yêu cầu nhập mã khách hàng
		String maKH = JOptionPane.showInputDialog(this,
				"Nhập mã khách hàng (VD: KH001):", "Thông tin khách hàng",
				JOptionPane.QUESTION_MESSAGE);
		if (maKH == null || maKH.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khách hàng!");
			return;
		}
		maKH = maKH.trim().toUpperCase();

		int confirm = JOptionPane.showConfirmDialog(this,
				"Khách hàng [" + maKH + "] thanh toán " + currencyFormat.format(totalAmount) + " đ?\nXác nhận chốt đơn?",
				"Thanh toán", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) return;

		// Tạo đối tượng DonHang
		String maNV = SessionManager.getCurrentUser() != null
				? SessionManager.getCurrentUser().getMaNV() : null;

		DonHang dh = new DonHang();
		dh.setMaKH(maKH);
		dh.setMaNV(maNV);
		dh.setMaKM(null);
		dh.setDiemSuDung(0);
		// Điểm nhận được = tổng tiền / 10000 (quy tắc: mỗi 10,000đ = 1 điểm)
		dh.setDiemNhanDuoc((int) (totalAmount / 10000));

		// Tạo danh sách chi tiết từ giỏ hàng
		java.util.List<ChiTietDonHang> chiTietList = new java.util.ArrayList<>();
		for (int i = 0; i < cartTableModel.getRowCount(); i++) {
			String maBT   = (String) cartTableModel.getValueAt(i, 0);
			int    soLuong = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());
			long   gia     = (long) cartTableModel.getValueAt(i, 3);
			chiTietList.add(new ChiTietDonHang(null, maBT, soLuong, gia));
		}

		// Lưu vào DB
		try {
			DonHangDAO dao = new DonHangDAO();
			dao.saveDonHang(dh, chiTietList);
			JOptionPane.showMessageDialog(this,
					"Thanh toán thành công!\nMã đơn hàng: " + dh.getMaDH() +
					"\nĐiểm tích lũy nhận được: " + dh.getDiemNhanDuoc());
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Lỗi khi lưu đơn hàng:\n" + ex.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}
