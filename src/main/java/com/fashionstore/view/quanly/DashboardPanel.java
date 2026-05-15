package com.fashionstore.view.quanly;

import com.fashionstore.util.TableBadgeRenderer;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 1. Phần Thống kê (4 thẻ)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.add(createStatCard("Doanh thu tháng", "128.5M", "+12% so tháng trước", new Color(40, 167, 69)));
        statsPanel.add(createStatCard("Đơn hàng hôm nay", "34", "Cập nhật lúc 10:30", Color.GRAY));
        statsPanel.add(createStatCard("Sản phẩm đang bán", "256", "8 sắp hết hàng", new Color(255, 193, 7)));
        statsPanel.add(createStatCard("Nhân viên", "12", "4 vai trò khác nhau", Color.GRAY));
        
        // 2. Bảng Đơn hàng gần đây
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #ffffff; border: 1,1,1,1,#e3e3e3");
        
        JLabel lblTableTitle = new JLabel("  Đơn hàng gần đây");
        lblTableTitle.setPreferredSize(new Dimension(0, 40));
        lblTableTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold");
        tableContainer.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"Mã đơn", "Nhân viên", "Tổng tiền", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa; font: bold;");
        
        model.addRow(new Object[]{"DH-0041", "Trần Thị B", "1,250,000đ", "Đã thanh toán"});
        model.addRow(new Object[]{"DH-0040", "Lê Văn C", "890,000đ", "Chờ thanh toán"});
        table.getColumnModel().getColumn(3).setCellRenderer(new TableBadgeRenderer()); // Gắn Badge

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scroll, BorderLayout.CENTER);

        add(statsPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, String sub, Color subColor) {
        JPanel card = new JPanel(new GridLayout(3, 1));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #ffffff; border: 1,1,1,1,#e3e3e3");
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.putClientProperty(FlatClientProperties.STYLE, "font: h1");
        
        JLabel lblSub = new JLabel(sub);
        lblSub.setForeground(subColor);
        lblSub.putClientProperty(FlatClientProperties.STYLE, "font: small");

        card.add(lblTitle);
        card.add(lblValue);
        card.add(lblSub);
        return card;
    }
}