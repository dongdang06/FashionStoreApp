package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.BienTheSanPhamController;
import com.fashionstore.controller.ChiTietKhuyenMaiController;
import com.fashionstore.controller.SanPhamController;
import com.fashionstore.model.BienTheSanPham;
import com.fashionstore.model.ChiTietKhuyenMai;
import com.fashionstore.model.KhuyenMai;
import com.fashionstore.model.SanPham;

public class ChiTietKhuyenMaiDialog extends JDialog {
    private final KhuyenMai khuyenMai;
    private final ChiTietKhuyenMaiController ctController = new ChiTietKhuyenMaiController();
    private final BienTheSanPhamController bienTheController = new BienTheSanPhamController();
    private final SanPhamController sanPhamController = new SanPhamController();

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnAdd;
    private JButton btnDelete;

    private Map<String, String> productNames = new HashMap<>();
    private Map<String, BienTheSanPham> variantMap = new HashMap<>();
    private NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public ChiTietKhuyenMaiDialog(java.awt.Window owner, KhuyenMai khuyenMai) {
        super(owner, "Quản lý Chi tiết Khuyến mãi: " + khuyenMai.getTenKM(), ModalityType.APPLICATION_MODAL);
        this.khuyenMai = khuyenMai;
        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        loadDataReference();
        initComponents();
        loadDetails();
    }

    private void loadDataReference() {
        List<SanPham> products = sanPhamController.getAll();
        for (SanPham sp : products) {
            productNames.put(sp.getMaSP(), sp.getTenSP());
        }

        List<BienTheSanPham> variants = bienTheController.getAll();
        for (BienTheSanPham v : variants) {
            variantMap.put(v.getMaBienThe(), v);
        }
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblTitle = new JLabel("Chi tiết sản phẩm khuyến mãi cho: " + khuyenMai.getTenKM() + " (" + khuyenMai.getMaKM() + ")");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnAdd = new JButton("Thêm SP khuyến mãi");
        btnDelete = new JButton("Xóa SP");
        
        btnAdd.addActionListener(e -> addPromoProduct());
        btnDelete.addActionListener(e -> deletePromoProduct());

        actionPanel.add(btnAdd);
        actionPanel.add(btnDelete);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new Object[] { "Mã biến thể", "Tên sản phẩm", "Màu sắc", "Kích thước", "Giá gốc", "Giá khuyến mãi" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadDetails() {
        tableModel.setRowCount(0);
        List<ChiTietKhuyenMai> details = ctController.getByMaKM(khuyenMai.getMaKM());
        for (ChiTietKhuyenMai ct : details) {
            BienTheSanPham variant = variantMap.get(ct.getMaBienThe());
            if (variant != null) {
                String spName = productNames.getOrDefault(variant.getMaSP(), "N/A");
                tableModel.addRow(new Object[] {
                        ct.getMaBienThe(),
                        spName,
                        variant.getMauSac(),
                        variant.getKichThuoc(),
                        currencyFormat.format(variant.getGiaBan()) + " đ",
                        currencyFormat.format(ct.getGiaKhuyenMai()) + " đ"
                });
            }
        }
    }

    private void addPromoProduct() {
        // Create selection panel
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        List<BienTheSanPham> allVariants = bienTheController.getAll();
        // Remove variants already in this promotion
        List<ChiTietKhuyenMai> currentDetails = ctController.getByMaKM(khuyenMai.getMaKM());
        allVariants.removeIf(v -> currentDetails.stream().anyMatch(d -> d.getMaBienThe().equals(v.getMaBienThe())));

        if (allVariants.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tất cả biến thể sản phẩm đã được áp dụng trong chương trình này!");
            return;
        }

        JComboBox<String> cbVariants = new JComboBox<>();
        Map<Integer, BienTheSanPham> selectionMap = new HashMap<>();
        for (int i = 0; i < allVariants.size(); i++) {
            BienTheSanPham v = allVariants.get(i);
            String spName = productNames.getOrDefault(v.getMaSP(), "N/A");
            String displayStr = v.getMaBienThe() + " - " + spName + " (" + v.getMauSac() + " - " + v.getKichThuoc() + ") [Gốc: " + currencyFormat.format(v.getGiaBan()) + " đ]";
            cbVariants.addItem(displayStr);
            selectionMap.put(i, v);
        }

        JTextField txtPromoPrice = new JTextField();

        panel.add(new JLabel("Chọn biến thể sản phẩm:"));
        panel.add(cbVariants);
        panel.add(new JLabel("Nhập giá khuyến mãi (VND):"));
        panel.add(txtPromoPrice);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm sản phẩm khuyến mãi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int selectedIdx = cbVariants.getSelectedIndex();
            if (selectedIdx < 0) return;

            BienTheSanPham selectedVariant = selectionMap.get(selectedIdx);
            String promoPriceStr = txtPromoPrice.getText().trim();

            if (promoPriceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giá khuyến mãi không được trống!");
                return;
            }

            try {
                long promoPrice = Long.parseLong(promoPriceStr);
                if (promoPrice <= 0) {
                    JOptionPane.showMessageDialog(this, "Giá khuyến mãi phải lớn hơn 0!");
                    return;
                }
                if (promoPrice >= selectedVariant.getGiaBan()) {
                    JOptionPane.showMessageDialog(this, "Giá khuyến mãi phải nhỏ hơn giá gốc (" + currencyFormat.format(selectedVariant.getGiaBan()) + " đ)!");
                    return;
                }

                ChiTietKhuyenMai ct = new ChiTietKhuyenMai(khuyenMai.getMaKM(), selectedVariant.getMaBienThe(), promoPrice);
                if (ctController.save(ct)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadDetails();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập giá khuyến mãi hợp lệ!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePromoProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
            return;
        }

        String maBienThe = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa sản phẩm khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (ctController.delete(khuyenMai.getMaKM(), maBienThe)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadDetails();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
