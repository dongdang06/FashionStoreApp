package com.fashionstore.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.fashionstore.util.UIHelper;
import com.fashionstore.view.nvbanhang.DashboardPanel;
import com.fashionstore.view.nvbanhang.DonHangPanel;
import com.fashionstore.view.nvbanhang.HoaDonPanel;
import com.fashionstore.view.quanly.DanhMucSanPhamPanel;
import com.fashionstore.view.quanly.KhuyenMaiPanel;
import com.fashionstore.view.quanly.NhaCungCapPanel;
import com.fashionstore.view.quanly.NhanVienPanel;
import com.fashionstore.view.quanly.SanPhamPanel;

public class MainAppFrame extends JFrame {
    private static final String PANEL_DASHBOARD = "dashboard";
    private static final String PANEL_DON_HANG = "donhang";
    private static final String PANEL_HOA_DON = "hoadon";
    private static final String PANEL_SAN_PHAM = "sanpham";
    private static final String PANEL_BIEN_THE_QL = "bienthe_ql";
    private static final String PANEL_DANH_MUC = "danhmuc";
    private static final String PANEL_NHA_CUNG_CAP = "nhacungcap";
    private static final String PANEL_NHAN_VIEN = "nhanvien";
    private static final String PANEL_KHUYEN_MAI = "khuyenmai";
    private static final String PANEL_DOANH_THU_QL = "doanhthu_ql";
    private static final String PANEL_DOANH_THU_KT = "doanhthu_kt";
    private static final String PANEL_BIEN_THE_KHO = "bienthe_kho";
    private static final String PANEL_PHIEU_NHAP = "phieunhap";
    private static final String PANEL_PHIEU_XUAT = "phieuxuat";

    private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    
    // Lưu các panel đã khởi tạo (Lazy Loading)
    private final Map<String, JPanel> loadedPanels = new HashMap<>();

    public MainAppFrame() {
        setTitle("Fashion Store - All Views");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 800);
        setLocationRelativeTo(null);

        contentPanel.setBackground(UIHelper.BG_CONTENT);

        JPanel sidebar = buildSidebar();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPanel(PANEL_DASHBOARD);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIHelper.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("FASHION STORE");
        brand.setForeground(UIHelper.TEXT_SIDEBAR);
        brand.setFont(UIHelper.FONT_APP_TITLE);
        brand.setBorder(BorderFactory.createEmptyBorder(20, 18, 12, 10));
        sidebar.add(brand);

        JLabel user = new JLabel("Nguyen Van A", SwingConstants.LEFT);
        user.setForeground(UIHelper.TEXT_SIDEBAR);
        user.setFont(UIHelper.FONT_USER_NAME);
        user.setBorder(BorderFactory.createEmptyBorder(10, 18, 2, 10));
        sidebar.add(user);

        JLabel role = new JLabel("All modules", SwingConstants.LEFT);
        role.setForeground(UIHelper.TEXT_SIDEBAR_MUTED);
        role.setFont(UIHelper.FONT_USER_ROLE);
        role.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 10));
        sidebar.add(role);

        sidebar.add(createNavButton("\uD83C\uDFE0 Dashboard", PANEL_DASHBOARD));
        sidebar.add(createNavButton("\uD83D\uDCC1 Danh muc san pham", PANEL_DANH_MUC));
        sidebar.add(createNavButton("\uD83D\uDC57 San pham", PANEL_SAN_PHAM));
        sidebar.add(createNavButton("\uD83C\uDFF7\uFE0F Bien the (QL)", PANEL_BIEN_THE_QL));
        sidebar.add(createNavButton("\uD83D\uDCE6 Bien the (Kho)", PANEL_BIEN_THE_KHO));
        sidebar.add(createNavButton("\uD83C\uDF81 Khuyen mai", PANEL_KHUYEN_MAI));
        sidebar.add(createNavButton("\uD83E\uDD1D Nha cung cap", PANEL_NHA_CUNG_CAP));
        sidebar.add(createNavButton("\uD83D\uDCE5 Phieu nhap kho", PANEL_PHIEU_NHAP));
        sidebar.add(createNavButton("\uD83D\uDCE4 Phieu xuat tra", PANEL_PHIEU_XUAT));
        sidebar.add(createNavButton("\uD83D\uDED2 Don hang", PANEL_DON_HANG));
        sidebar.add(createNavButton("\uD83E\uDDFE Hoa don", PANEL_HOA_DON));
        sidebar.add(createNavButton("\uD83D\uDC65 Nhan vien", PANEL_NHAN_VIEN));
        sidebar.add(createNavButton("\uD83D\uDCCA Doanh thu (QL)", PANEL_DOANH_THU_QL));
        sidebar.add(createNavButton("\uD83D\uDCB0 Doanh thu (Ke toan)", PANEL_DOANH_THU_KT));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createNavButton(String text, String panelKey) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(UIHelper.FONT_NAV_BUTTON);
        button.setForeground(UIHelper.TEXT_SIDEBAR);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(UIHelper.BG_SIDEBAR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addActionListener(event -> showPanel(panelKey));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!button.getBackground().equals(UIHelper.BG_SIDEBAR_ACTIVE)) {
                    button.setBackground(UIHelper.BG_SIDEBAR_HOVER);
                }
                button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!button.getBackground().equals(UIHelper.BG_SIDEBAR_ACTIVE)) {
                    button.setBackground(UIHelper.BG_SIDEBAR);
                }
                button.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
        
        navButtons.put(panelKey, button);
        return button;
    }

    private void showPanel(String panelKey) {
        // Chỉ khởi tạo Panel nếu nó chưa được load
        if (!loadedPanels.containsKey(panelKey)) {
            JPanel newPanel = createPanel(panelKey);
            loadedPanels.put(panelKey, newPanel);
            contentPanel.add(newPanel, panelKey);
        }

        cardLayout.show(contentPanel, panelKey);
        setActiveButton(panelKey);

        // Gọi hàm reloadData của Panel đang hiển thị
        JPanel activePanel = loadedPanels.get(panelKey);
        reloadDataForPanel(panelKey, activePanel);
    }
    
    private JPanel createPanel(String panelKey) {
        switch (panelKey) {
            case PANEL_DASHBOARD: return new DashboardPanel();
            case PANEL_DON_HANG: return new DonHangPanel();
            case PANEL_HOA_DON: return new HoaDonPanel();
            case PANEL_SAN_PHAM: return new SanPhamPanel();
            case PANEL_BIEN_THE_QL: return new com.fashionstore.view.quanly.BienTheSanPhamPanel();
            case PANEL_DANH_MUC: return new DanhMucSanPhamPanel();
            case PANEL_NHA_CUNG_CAP: return new NhaCungCapPanel();
            case PANEL_NHAN_VIEN: return new NhanVienPanel();
            case PANEL_KHUYEN_MAI: return new KhuyenMaiPanel();
            case PANEL_DOANH_THU_QL: return new com.fashionstore.view.quanly.DoanhThuPanel();
            case PANEL_DOANH_THU_KT: return new com.fashionstore.view.quanly.DoanhThuPanel();
            case PANEL_BIEN_THE_KHO: return new com.fashionstore.view.nvkho.BienTheSanPhamPanel();
            case PANEL_PHIEU_NHAP: return new com.fashionstore.view.nvkho.PhieuNhapKhoPanel();
            case PANEL_PHIEU_XUAT: return new com.fashionstore.view.nvkho.PhieuXuatTraPanel();
            default: return new JPanel();
        }
    }
    
    private void reloadDataForPanel(String panelKey, JPanel activePanel) {
        if (PANEL_DASHBOARD.equals(panelKey)) ((DashboardPanel) activePanel).reloadData();
        else if (PANEL_DON_HANG.equals(panelKey)) ((DonHangPanel) activePanel).reloadData();
        else if (PANEL_HOA_DON.equals(panelKey)) ((HoaDonPanel) activePanel).reloadData();
        else if (PANEL_SAN_PHAM.equals(panelKey)) ((SanPhamPanel) activePanel).reloadData();
        else if (PANEL_BIEN_THE_QL.equals(panelKey)) ((com.fashionstore.view.quanly.BienTheSanPhamPanel) activePanel).reloadData();
        else if (PANEL_DANH_MUC.equals(panelKey)) ((DanhMucSanPhamPanel) activePanel).reloadData();
        else if (PANEL_NHA_CUNG_CAP.equals(panelKey)) ((NhaCungCapPanel) activePanel).reloadData();
        else if (PANEL_NHAN_VIEN.equals(panelKey)) ((NhanVienPanel) activePanel).reloadData();
        else if (PANEL_KHUYEN_MAI.equals(panelKey)) ((KhuyenMaiPanel) activePanel).reloadData();
        else if (PANEL_DOANH_THU_QL.equals(panelKey)) ((com.fashionstore.view.quanly.DoanhThuPanel) activePanel).reloadData();
        else if (PANEL_DOANH_THU_KT.equals(panelKey)) ((com.fashionstore.view.quanly.DoanhThuPanel) activePanel).reloadData();
        else if (PANEL_BIEN_THE_KHO.equals(panelKey)) ((com.fashionstore.view.nvkho.BienTheSanPhamPanel) activePanel).reloadData();
        else if (PANEL_PHIEU_NHAP.equals(panelKey)) ((com.fashionstore.view.nvkho.PhieuNhapKhoPanel) activePanel).reloadData();
        else if (PANEL_PHIEU_XUAT.equals(panelKey)) ((com.fashionstore.view.nvkho.PhieuXuatTraPanel) activePanel).reloadData();
    }

    private void setActiveButton(String panelKey) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton button = entry.getValue();
            button.setBackground(entry.getKey().equals(panelKey) ? UIHelper.BG_SIDEBAR_ACTIVE : UIHelper.BG_SIDEBAR);
        }
    }
}
