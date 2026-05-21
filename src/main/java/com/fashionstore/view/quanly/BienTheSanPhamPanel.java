package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
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

import com.fashionstore.controller.BienTheSanPhamController;
import com.fashionstore.model.BienTheSanPham;

public class BienTheSanPhamPanel extends JPanel {
    private final BienTheSanPhamController bienTheController = new BienTheSanPhamController();
    private final List<BienTheSanPham> data = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Ma bien the", "Ma SP", "Mau sac", "Kich thuoc", "Gia nhap", "Gia ban", "Ton kho" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public BienTheSanPhamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 8, 18));

        JLabel title = new JLabel("Bien the san pham");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);

        JButton refresh = new JButton("\u21BB");
        refresh.addActionListener(event -> reloadFromSource());
        JButton addButton = new JButton("Them");
        addButton.addActionListener(event -> addItem());
        JButton editButton = new JButton("Sua");
        editButton.addActionListener(event -> editItem());
        JButton deleteButton = new JButton("Xoa");
        deleteButton.addActionListener(event -> deleteItem());

        boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Kho");
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
        javax.swing.JButton btnSearch = new javax.swing.JButton("Tra cuu");
        btnSearch.addActionListener(e -> {
            String text = txtSearch.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
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
        data.addAll(bienTheController.getAll());
        reloadData();
    }

    public void reloadData() {
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
        if (data.isEmpty()) {
            data.addAll(bienTheController.getAll());
        }
        tableModel.setRowCount(0);
        for (BienTheSanPham bt : data) {
            tableModel.addRow(new Object[] {
                    bt.getMaBienThe(),
                    bt.getMaSP(),
                    bt.getMauSac(),
                    bt.getKichThuoc(),
                    bt.getGiaNhap() > 0 ? currency.format(bt.getGiaNhap()) : "Chua nhap",
                    currency.format(bt.getGiaBan()),
                    bt.getSoLuongTon()
            });
        }
    }

    private void addItem() {
        BienTheSanPham bt = showForm(null);
        if (bt == null) {
            return;
        }
        try {
            bienTheController.add(bt);
            data.add(bt);
            reloadData();
            JOptionPane.showMessageDialog(this, "Them bien the thanh cong.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chon dong can sua.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        BienTheSanPham current = data.get(modelRow);
        BienTheSanPham updated = showForm(current);
        if (updated == null) {
            return;
        }
        try {
            bienTheController.edit(updated);
            data.set(modelRow, updated);
            reloadData();
            JOptionPane.showMessageDialog(this, "Cap nhat bien the thanh cong.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chon dong can xoa.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        BienTheSanPham current = data.get(modelRow);
        int ok = JOptionPane.showConfirmDialog(this,
                "Xoa bien the \"" + current.getMaBienThe() + "\"?", "Xac nhan",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            bienTheController.remove(current.getMaBienThe());
            data.remove(modelRow);
            reloadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Khong the xoa bien the.\nLoi: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BienTheSanPham showForm(BienTheSanPham current) {
        // Load danh sach san pham tu database
        com.fashionstore.controller.SanPhamController spController = new com.fashionstore.controller.SanPhamController();
        List<com.fashionstore.model.SanPham> dsSanPham = spController.getAll();

        // Ma bien the (tu dong sinh, khong chinh sua)
        JTextField maBienThe = new JTextField(
                current == null ? com.fashionstore.util.MaGenerator.nextMaBienThe() : current.getMaBienThe());
        maBienThe.setEditable(false);
        maBienThe.setBackground(new Color(230, 230, 230));

        // ComboBox chon Ma SP
        javax.swing.DefaultComboBoxModel<String> comboModel = new javax.swing.DefaultComboBoxModel<>();
        int selectedIndex = 0;
        for (int i = 0; i < dsSanPham.size(); i++) {
            com.fashionstore.model.SanPham sp = dsSanPham.get(i);
            comboModel.addElement(sp.getMaSP() + " - " + sp.getTenSP());
            if (current != null && sp.getMaSP().equals(current.getMaSP())) {
                selectedIndex = i;
            }
        }
        javax.swing.JComboBox<String> maSPCombo = new javax.swing.JComboBox<>(comboModel);
        maSPCombo.setSelectedIndex(dsSanPham.isEmpty() ? -1 : selectedIndex);

        JTextField mauSac = new JTextField(current == null ? "" : current.getMauSac());
        JTextField kichThuoc = new JTextField(current == null ? "" : current.getKichThuoc());
        JTextField giaBan = new JTextField(current == null ? "" : String.valueOf(current.getGiaBan()));
        // Ton kho: luon read-only, duoc DB tu cap nhat qua trigger CHITIETPHIEUNHAP
        JTextField tonKho = new JTextField(current == null ? "0" : String.valueOf(current.getSoLuongTon()));
        tonKho.setEditable(false);
        tonKho.setBackground(new Color(230, 230, 230));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Ma bien the"));
        form.add(maBienThe);
        form.add(new JLabel("San pham"));
        form.add(maSPCombo);
        form.add(new JLabel("Mau sac"));
        form.add(mauSac);
        form.add(new JLabel("Kich thuoc"));
        form.add(kichThuoc);
        form.add(new JLabel("Gia ban"));
        form.add(giaBan);
        form.add(new JLabel("Ton kho"));
        form.add(tonKho);

        int result = JOptionPane.showConfirmDialog(this, form,
                current == null ? "Them bien the" : "Sua bien the",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        if (maSPCombo.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Vui long chon san pham.");
            return null;
        }
        // Lay MaSP tu san pham da chon
        String selectedMaSP = dsSanPham.get(maSPCombo.getSelectedIndex()).getMaSP();
        try {
            long giaBanValue = giaBan.getText().trim().isEmpty() ? 0 : Long.parseLong(giaBan.getText().trim());
            // Neu them moi: ton kho = 0 (read-only). Neu sua: lay tu field.
            int tonKhoValue = tonKho.getText().trim().isEmpty() ? 0 : Integer.parseInt(tonKho.getText().trim());
            return new BienTheSanPham(maBienThe.getText().trim(), selectedMaSP,
                    mauSac.getText().trim(), kichThuoc.getText().trim(),
                    giaBanValue, tonKhoValue);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Du lieu khong hop le.");
            return null;
        }
    }
}
