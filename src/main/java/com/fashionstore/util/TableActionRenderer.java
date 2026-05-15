package com.fashionstore.util;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class TableActionRenderer extends JPanel implements TableCellRenderer {
    public TableActionRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        setOpaque(true);
        
        JButton btnEdit = new JButton("✎");
        btnEdit.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 2,5,2,5");
        
        JButton btnDelete = new JButton("🗑");
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 10; padding: 2,5,2,5; foreground: #dc3545");
        
        add(btnEdit);
        add(btnDelete);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }
}