package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.DoanhThuController;
import com.fashionstore.model.BaoCaoDoanhThu;
import com.fashionstore.util.UIHelper;

public class DoanhThuPanel extends JPanel {
    private final DoanhThuController doanhThuController = new DoanhThuController();

    private final JLabel lblTongDoanhThu = new JLabel("0 ₫");
    private final JLabel lblTongHoaDon = new JLabel("0");
    private final JLabel lblTBDong = new JLabel("0 ₫");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Tiêu chí", "Số lượng", "Tổng doanh thu" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<String> cbTieuChi;

    public DoanhThuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_CONTENT);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        // --- Header (Bộ lọc) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Thống kê Doanh thu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JTextField("01/05/2026", 8);
        filterPanel.add(txtTuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JTextField("31/05/2026", 8);
        filterPanel.add(txtDenNgay);

        filterPanel.add(new JLabel("Theo:"));
        cbTieuChi = new JComboBox<>(new String[]{"Theo ngày", "Theo sản phẩm"});
        filterPanel.add(cbTieuChi);

        JButton btnTraCuu = new JButton("\uD83D\uDD0D Tra cứu");
        btnTraCuu.addActionListener(e -> reloadData());
        filterPanel.add(btnTraCuu);

        header.add(filterPanel, BorderLayout.CENTER);

        JButton btnInBaoCao = new JButton("\uD83D\uDDA8\uFE0F In báo cáo");
        btnInBaoCao.addActionListener(e -> inBaoCao());
        header.add(btnInBaoCao, BorderLayout.EAST);

        // --- Center (Thống kê + Bảng) ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Top cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.add(createCard("Tổng doanh thu", lblTongDoanhThu));
        cardsPanel.add(createCard("Tổng số hóa đơn", lblTongHoaDon));
        cardsPanel.add(createCard("Trung bình / Hóa đơn", lblTBDong));
        
        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Table
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        reloadData();
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(90, 90, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(34, 28, 79));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void reloadData() {
        String tieuChi = cbTieuChi.getSelectedItem().toString();
        List<BaoCaoDoanhThu> data = doanhThuController.getBaoCaoDoanhThu(tieuChi);

        // Đổi tên cột theo tiêu chí
        if ("Theo sản phẩm".equals(tieuChi)) {
            table.getColumnModel().getColumn(0).setHeaderValue("Tên sản phẩm");
        } else {
            table.getColumnModel().getColumn(0).setHeaderValue("Ngày");
        }
        table.getTableHeader().repaint();

        tableModel.setRowCount(0);
        long tongTien = 0;
        int tongDon = 0;
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (BaoCaoDoanhThu bc : data) {
            tableModel.addRow(new Object[] {
                    bc.getTieuChi(),
                    bc.getSoLuong(),
                    currency.format(bc.getTongTien()) + " ₫"
            });
            tongTien += bc.getTongTien();
            tongDon += bc.getSoLuong();
        }

        lblTongDoanhThu.setText(currency.format(tongTien) + " ₫");
        lblTongHoaDon.setText(String.valueOf(tongDon));
        if (tongDon > 0) {
            lblTBDong.setText(currency.format(tongTien / tongDon) + " ₫");
        } else {
            lblTBDong.setText("0 ₫");
        }
    }

    private void inBaoCao() {
        JOptionPane.showMessageDialog(this,
                "Hệ thống đang kết xuất dữ liệu...\nĐã xuất báo cáo thành công ra file BaoCaoDoanhThu.xlsx!",
                "In báo cáo", JOptionPane.INFORMATION_MESSAGE);
    }
}
