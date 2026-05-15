package com.fashionstore.view.nvbanhang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.DoanhThuController;
import com.fashionstore.controller.DonHangController;
import com.fashionstore.model.DashboardStats;
import com.fashionstore.model.DonHangSummary;

public class DashboardPanel extends JPanel {
    private final DoanhThuController doanhThuController = new DoanhThuController();
    private final DonHangController donHangController = new DonHangController();

    private final JLabel revenueValue = new JLabel("0");
    private final JLabel revenueSub = new JLabel("0% so thang truoc");
    private final JLabel ordersValue = new JLabel("0");
    private final JLabel ordersSub = new JLabel("Cap nhat luc 00:00");
    private final JLabel productsValue = new JLabel("0");
    private final JLabel productsSub = new JLabel("0 sap het hang");
    private final JLabel employeesValue = new JLabel("0");
    private final JLabel employeesSub = new JLabel("0 vai tro khac nhau");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Ma don", "Nhan vien", "Tong tien", "Trang thai" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));
        topPanel.setLayout(new java.awt.GridLayout(1, 4, 12, 12));

        topPanel.add(createStatCard("Doanh thu thang", revenueValue, revenueSub));
        topPanel.add(createStatCard("Don hang hom nay", ordersValue, ordersSub));
        topPanel.add(createStatCard("San pham dang ban", productsValue, productsSub));
        topPanel.add(createStatCard("Nhan vien", employeesValue, employeesSub));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        JLabel tableTitle = new JLabel("Don hang gan day");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 235)));
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        reloadData();
    }

    public void reloadData() {
        DashboardStats stats = doanhThuController.getDashboardStats();
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

        revenueValue.setText(currency.format(stats.getCurrentMonthRevenue()));
        revenueSub.setText(String.format("%.1f%% so thang truoc", stats.getMoMChangePercent()));
        ordersValue.setText(String.valueOf(stats.getOrdersToday()));
        ordersSub.setText("Cap nhat luc " + java.time.LocalTime.now()
            .withSecond(0).withNano(0));
        productsValue.setText(String.valueOf(stats.getSellingProducts()));
        productsSub.setText("Dang ban");
        employeesValue.setText(String.valueOf(stats.getActiveEmployees()));
        employeesSub.setText("Dang lam viec");

        tableModel.setRowCount(0);
        List<DonHangSummary> recentOrders = donHangController.getRecentOrders(6);
        for (DonHangSummary summary : recentOrders) {
            tableModel.addRow(new Object[] {
                    summary.getMaDH(),
                    summary.getNhanVien(),
                    currency.format(summary.getTongTien()),
                    summary.getTrangThai()
            });
        }
    }

    private JPanel createStatCard(String title, JLabel value, JLabel sub) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        card.setPreferredSize(new Dimension(220, 90));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(110, 110, 120));

        value.setFont(new Font("Segoe UI", Font.BOLD, 16));
        value.setForeground(new Color(35, 35, 40));

        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(120, 150, 120));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new java.awt.GridLayout(3, 1, 2, 2));
        textPanel.add(titleLabel);
        textPanel.add(value);
        textPanel.add(sub);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            String status = value == null ? "" : value.toString();
            if ("Da thanh toan".equalsIgnoreCase(status)) {
                label.setBackground(new Color(224, 243, 224));
                label.setForeground(new Color(60, 120, 60));
            } else {
                label.setBackground(new Color(252, 237, 215));
                label.setForeground(new Color(140, 90, 30));
            }
            if (isSelected) {
                label.setBackground(new Color(220, 220, 235));
                label.setForeground(new Color(30, 30, 40));
            }
            label.setOpaque(true);
            return label;
        }
    }
}
