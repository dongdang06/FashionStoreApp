package com.fashionstore.view.nvbanhang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
        setBackground(new Color(240, 242, 245)); // Nền xám nhạt hiện đại

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JLabel title = new JLabel("Tổng quan kinh doanh \uD83D\uDCC8"); // Icon biểu đồ
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 30, 40));
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Hôm nay: "
                + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 110));
        header.add(subtitle, BorderLayout.EAST);

        // --- Top Stats (4 Cards) ---
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        topPanel.setLayout(new GridLayout(1, 4, 15, 15));

        topPanel.add(createStatCard("Doanh thu tháng", "💰", revenueValue, revenueSub, new Color(227, 242, 253),
                new Color(13, 71, 161)));
        topPanel.add(createStatCard("Đơn hàng hôm nay", "🛒", ordersValue, ordersSub, new Color(232, 245, 233),
                new Color(27, 94, 32)));
        topPanel.add(createStatCard("Sản phẩm đang bán", "📦", productsValue, productsSub, new Color(243, 229, 245),
                new Color(74, 20, 140)));
        topPanel.add(createStatCard("Nhân viên active", "👥", employeesValue, employeesSub, new Color(255, 243, 224),
                new Color(230, 81, 0)));

        // --- Center Panel (Biểu đồ + Bảng) ---
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Biểu đồ bên trái
        centerPanel.add(createBarChart(), BorderLayout.WEST);

        // Bảng đơn hàng bên phải
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        // Bo góc mềm mại cho table card
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel tableTitle = new JLabel("Đơn hàng mới nhất");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(40, 40, 50));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTable table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(100, 100, 110));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 245));
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);

        // Custom renderer cho cột trạng thái
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        reloadData();
    }

    private JPanel createBarChart() {
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setPreferredSize(new Dimension(350, 0));
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel title = new JLabel("Doanh thu 7 ngày qua");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(40, 40, 50));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        chartContainer.add(title, BorderLayout.NORTH);

        JPanel chartArea = new JPanel(new GridLayout(1, 7, 12, 0));
        chartArea.setOpaque(false);

        // Mock data 7 ngày
        int[] data = { 12, 18, 14, 25, 20, 30, 22 };
        String[] days = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        int max = 35;

        for (int i = 0; i < 7; i++) {
            JPanel barWrapper = new JPanel(new BorderLayout());
            barWrapper.setOpaque(false);

            JPanel barArea = new JPanel(new BorderLayout());
            barArea.setOpaque(false);

            // Tính chiều cao tương đối
            double ratio = data[i] / (double) max;
            int height = Math.max(10, (int) (ratio * 200));

            // Vẽ cột
            JPanel bar = new JPanel() {
                @Override
                protected void paintComponent(java.awt.Graphics g) {
                    super.paintComponent(g);
                    java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                    g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    // Hiệu ứng Gradient nhẹ cho cột
                    java.awt.GradientPaint gp = new java.awt.GradientPaint(
                            0, 0, new Color(100, 149, 237),
                            0, getHeight(), new Color(65, 105, 225));
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8); // Cột bo tròn
                }
            };
            bar.setOpaque(false);
            bar.setPreferredSize(new Dimension(30, height));

            // Hiệu ứng hover cho cột
            bar.setToolTipText("Doanh thu: " + data[i] + "M");

            barArea.add(Box.createVerticalGlue(), BorderLayout.CENTER);
            barArea.add(bar, BorderLayout.SOUTH);

            JLabel lblDay = new JLabel(days[i], SwingConstants.CENTER);
            lblDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDay.setForeground(new Color(130, 130, 140));
            lblDay.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            barWrapper.add(barArea, BorderLayout.CENTER);
            barWrapper.add(lblDay, BorderLayout.SOUTH);
            chartArea.add(barWrapper);
        }

        chartContainer.add(chartArea, BorderLayout.CENTER);
        return chartContainer;
    }

    private JPanel createStatCard(String title, String icon, JLabel valueLabel, JLabel subLabel, Color bgColor,
            Color textColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        // Cột bên trái: Text
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(textColor.darker());
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(textColor);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subLabel);

        // Cột bên phải: Icon lớn mờ mờ
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 100)); // Hơi
                                                                                                                // trong
                                                                                                                // suốt
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    public void reloadData() {
        DashboardStats stats = doanhThuController.getDashboardStats();
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

        revenueValue.setText(currency.format(stats.getCurrentMonthRevenue()) + "₫");
        revenueSub.setText((stats.getMoMChangePercent() >= 0 ? "▲ Tăng " : "▼ Giảm ")
                + String.format("%.1f%% so với tháng trước", Math.abs(stats.getMoMChangePercent())));

        ordersValue.setText(String.valueOf(stats.getOrdersToday()));
        ordersSub.setText("Đã giao " + (stats.getOrdersToday() > 0 ? (stats.getOrdersToday() - 1) : 0) + " đơn");

        productsValue.setText(String.valueOf(stats.getSellingProducts()));
        productsSub.setText("Mặt hàng đang kinh doanh");

        employeesValue.setText(String.valueOf(stats.getActiveEmployees()));
        employeesSub.setText("Nhân viên đang làm việc");

        tableModel.setRowCount(0);
        List<DonHangSummary> recentOrders = donHangController.getRecentOrders(10);
        for (DonHangSummary summary : recentOrders) {
            tableModel.addRow(new Object[] {
                    summary.getMaDH(),
                    summary.getNhanVien(),
                    currency.format(summary.getTongTien()) + "₫",
                    summary.getTrangThai()
            });
        }
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Thêm padding cho label (giả lập badge)
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            String status = value == null ? "" : value.toString();
            if ("Da thanh toan".equalsIgnoreCase(status)) {
                label.setBackground(new Color(220, 245, 224));
                label.setForeground(new Color(40, 130, 60));
                label.setText("● Đã thanh toán");
            } else if ("Huy".equalsIgnoreCase(status) || "Da huy".equalsIgnoreCase(status)) {
                label.setBackground(new Color(255, 230, 230));
                label.setForeground(new Color(200, 50, 50));
                label.setText("● Đã hủy");
            } else {
                label.setBackground(new Color(255, 245, 215));
                label.setForeground(new Color(180, 110, 20));
                label.setText("● Chờ xử lý");
            }
            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }
            label.setOpaque(true);

            // Bọc label vào 1 panel để tạo padding cho cell (cách viền)
            JPanel wrapper = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
            wrapper.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            wrapper.add(label);
            return wrapper;
        }
    }
}
