package com.fashionstore.util;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class TableBadgeRenderer extends JPanel implements TableCellRenderer {
    private JLabel badge;

    public TableBadgeRenderer() {
        // Căn giữa viên thuốc
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 7)); 
        setOpaque(true);
        
        badge = new JLabel();
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12)); 
        badge.putClientProperty(FlatClientProperties.STYLE, "arc: 15; font: bold -1");
        add(badge);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        String status = value != null ? value.toString() : "";
        badge.setText(status);
        
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        // ĐÃ BỔ SUNG CÁC TRẠNG THÁI MỚI VÀO ĐÂY
        if (status.equalsIgnoreCase("Đang làm") || status.equalsIgnoreCase("Đang bán") 
         || status.equalsIgnoreCase("Đã thanh toán") || status.equalsIgnoreCase("Còn hàng") 
         || status.equalsIgnoreCase("Đã xuất")) {
            
            badge.setBackground(new Color(234, 243, 222)); // Xanh lá
            badge.setForeground(new Color(39, 80, 10));
            
        } else if (status.equalsIgnoreCase("Đã nghỉ") || status.equalsIgnoreCase("Ngừng bán") 
                || status.equalsIgnoreCase("Hết hàng") || status.equalsIgnoreCase("Đã hủy")) {
            
            badge.setBackground(new Color(241, 239, 232)); // Xám
            badge.setForeground(new Color(68, 68, 65));
            
        } else if (status.equalsIgnoreCase("Khuyến mãi") || status.equalsIgnoreCase("Chờ thanh toán")) {
            
            badge.setBackground(new Color(250, 238, 218)); // Vàng/Cam
            badge.setForeground(new Color(99, 56, 6));
            
        } else {
            badge.setBackground(Color.WHITE);
            badge.setForeground(Color.BLACK);
        }
        
        return this;
    }
}