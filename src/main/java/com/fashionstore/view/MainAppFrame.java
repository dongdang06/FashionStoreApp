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
    private static final String PANEL_BIEN_THE = "bienthe";
    private static final String PANEL_DANH_MUC = "danhmuc";
    private static final String PANEL_NHA_CUNG_CAP = "nhacungcap";
    private static final String PANEL_NHAN_VIEN = "nhanvien";
    private static final String PANEL_KHUYEN_MAI = "khuyenmai";
    private static final String PANEL_DOANH_THU = "doanhthu";
    private static final String PANEL_PHIEU_NHAP = "phieunhap";
    private static final String PANEL_PHIEU_XUAT = "phieuxuat";
    private static final String PANEL_KHACH_HANG = "khachhang";
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

        com.fashionstore.model.TaiKhoan currentUser = com.fashionstore.util.SessionManager.getCurrentUser();
        String displayUserName = getDisplayEmployeeName(currentUser);
        String displayRole = (currentUser != null && currentUser.getVaiTro() != null) ? currentUser.getVaiTro() : "No Role";

        JLabel user = new JLabel(displayUserName, SwingConstants.LEFT);
        user.setForeground(UIHelper.TEXT_SIDEBAR);
        user.setFont(UIHelper.FONT_USER_NAME);
        user.setBorder(BorderFactory.createEmptyBorder(10, 18, 2, 10));
        sidebar.add(user);

        JLabel role = new JLabel(displayRole, SwingConstants.LEFT);
        role.setForeground(UIHelper.TEXT_SIDEBAR_MUTED);
        role.setFont(UIHelper.FONT_USER_ROLE);
        role.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 10));
        sidebar.add(role);

        // Lấy vai trò hiện tại để phân quyền menu
        String currentRole = (currentUser != null && currentUser.getVaiTro() != null)
                ? currentUser.getVaiTro().toLowerCase() : "";

        // Dashboard - tất cả vai trò đều thấy
        sidebar.add(createNavButton("\uD83C\uDFE0 Dashboard", PANEL_DASHBOARD));

        // Quản lý sản phẩm - chỉ Quản lý
        if (isQuanLy(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDCC1 Danh muc san pham", PANEL_DANH_MUC));
            sidebar.add(createNavButton("\uD83D\uDC57 San pham", PANEL_SAN_PHAM));
        }

        // Biến thể - Quản lý + NV Kho
        if (isQuanLy(currentRole) || isNVKho(currentRole)) {
            sidebar.add(createNavButton("\uD83C\uDFF7\uFE0F Bien the", PANEL_BIEN_THE));
        }

        // Khuyến mãi - chỉ Quản lý
        if (isQuanLy(currentRole)) {
            sidebar.add(createNavButton("\uD83C\uDF81 Khuyen mai", PANEL_KHUYEN_MAI));
        }

        // Nhà cung cấp - Quản lý + NV Kho
        if (isQuanLy(currentRole) || isNVKho(currentRole)) {
            sidebar.add(createNavButton("\uD83E\uDD1D Nha cung cap", PANEL_NHA_CUNG_CAP));
        }

        // Phiếu nhập/xuất kho - Quản lý + NV Kho
        if (isQuanLy(currentRole) || isNVKho(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDCE5 Phieu nhap kho", PANEL_PHIEU_NHAP));
            sidebar.add(createNavButton("\uD83D\uDCE4 Phieu xuat tra", PANEL_PHIEU_XUAT));
        }

        // Đơn hàng - Quản lý + NV Bán hàng
        if (isQuanLy(currentRole) || isNVBanHang(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDED2 Don hang", PANEL_DON_HANG));
        }

        // Hóa đơn - Quản lý + NV Bán hàng + NV Kế toán
        if (isQuanLy(currentRole) || isNVBanHang(currentRole) || isNVKeToan(currentRole)) {
            sidebar.add(createNavButton("\uD83E\uDDFE Hoa don", PANEL_HOA_DON));
        }

        // Nhân viên - chỉ Quản lý
        if (isQuanLy(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDC65 Nhan vien", PANEL_NHAN_VIEN));
        }

        // Doanh thu - Quản lý + NV Kế toán
        if (isQuanLy(currentRole) || isNVKeToan(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDCCA Doanh thu", PANEL_DOANH_THU));
        }

        // Khách hàng - Quản lý + NV Bán hàng
        if (isQuanLy(currentRole) || isNVBanHang(currentRole)) {
            sidebar.add(createNavButton("\uD83D\uDC65 Khách hàng", PANEL_KHACH_HANG));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnChangePassword = new JButton("\uD83D\uDD12 Doi mat khau");
        btnChangePassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnChangePassword.setHorizontalAlignment(SwingConstants.LEFT);
        btnChangePassword.setFont(UIHelper.FONT_NAV_BUTTON);
        btnChangePassword.setForeground(UIHelper.TEXT_SIDEBAR);
        btnChangePassword.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        btnChangePassword.setContentAreaFilled(false);
        btnChangePassword.setOpaque(true);
        btnChangePassword.setBackground(UIHelper.BG_SIDEBAR);
        btnChangePassword.setFocusPainted(false);
        btnChangePassword.setBorderPainted(false);
        btnChangePassword.addActionListener(e -> com.fashionstore.view.auth.DoiMatKhauDialog.show(this));
        btnChangePassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnChangePassword.setBackground(UIHelper.BG_SIDEBAR_HOVER);
                btnChangePassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnChangePassword.setBackground(UIHelper.BG_SIDEBAR);
                btnChangePassword.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        JButton btnLogout = new JButton("\uD83D\uDEAA Dang xuat");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setFont(UIHelper.FONT_NAV_BUTTON);
        btnLogout.setForeground(UIHelper.TEXT_SIDEBAR);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.setBackground(UIHelper.BG_SIDEBAR);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        
        btnLogout.addActionListener(e -> {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                this.dispose();
                new com.fashionstore.view.auth.DangNhapFrame().setVisible(true);
            }
        });
        
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(UIHelper.BG_SIDEBAR_HOVER);
                btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(UIHelper.BG_SIDEBAR);
                btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
        
        sidebar.add(btnChangePassword);
        sidebar.add(btnLogout);
        return sidebar;
    }

    // === Các hàm kiểm tra vai trò ===
    private boolean isQuanLy(String role) {
        return role.contains("quan ly") || role.contains("admin");
    }

    private boolean isNVBanHang(String role) {
        return role.contains("ban hang");
    }

    private boolean isNVKho(String role) {
        return role.contains("kho");
    }

    private boolean isNVKeToan(String role) {
        return role.contains("ke toan");
    }

    private String getDisplayEmployeeName(com.fashionstore.model.TaiKhoan currentUser) {
        if (currentUser == null) {
            return "Unknown";
        }
        String employeeName = new com.fashionstore.dao.NhanVienDAO().getHoTenByMaNV(currentUser.getMaNV());
        if (employeeName != null && !employeeName.trim().isEmpty()) {
            return employeeName.trim();
        }
        return currentUser.getUserName() != null ? currentUser.getUserName() : "Unknown";
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
            case PANEL_BIEN_THE: return new com.fashionstore.view.quanly.BienTheSanPhamPanel();
            case PANEL_DANH_MUC: return new DanhMucSanPhamPanel();
            case PANEL_NHA_CUNG_CAP: return new NhaCungCapPanel();
            case PANEL_NHAN_VIEN: return new NhanVienPanel();
            case PANEL_KHUYEN_MAI: return new KhuyenMaiPanel();
            case PANEL_DOANH_THU: return new com.fashionstore.view.quanly.DoanhThuPanel();
            case PANEL_PHIEU_NHAP: return new com.fashionstore.view.nvkho.PhieuNhapKhoPanel();
            case PANEL_PHIEU_XUAT: return new com.fashionstore.view.nvkho.PhieuXuatTraPanel();
            case PANEL_KHACH_HANG: return new com.fashionstore.view.quanly.KhachHangPanel();
            default: return new JPanel();
        }
    }
    
    private void reloadDataForPanel(String panelKey, JPanel activePanel) {
        if (PANEL_DASHBOARD.equals(panelKey)) ((DashboardPanel) activePanel).reloadData();
        else if (PANEL_DON_HANG.equals(panelKey)) ((DonHangPanel) activePanel).reloadData();
        else if (PANEL_HOA_DON.equals(panelKey)) ((HoaDonPanel) activePanel).reloadData();
        else if (PANEL_SAN_PHAM.equals(panelKey)) ((SanPhamPanel) activePanel).reloadData();
        else if (PANEL_BIEN_THE.equals(panelKey)) ((com.fashionstore.view.quanly.BienTheSanPhamPanel) activePanel).reloadData();
        else if (PANEL_DANH_MUC.equals(panelKey)) ((DanhMucSanPhamPanel) activePanel).reloadData();
        else if (PANEL_NHA_CUNG_CAP.equals(panelKey)) ((NhaCungCapPanel) activePanel).reloadData();
        else if (PANEL_NHAN_VIEN.equals(panelKey)) ((NhanVienPanel) activePanel).reloadData();
        else if (PANEL_KHUYEN_MAI.equals(panelKey)) ((KhuyenMaiPanel) activePanel).reloadData();
        else if (PANEL_DOANH_THU.equals(panelKey)) ((com.fashionstore.view.quanly.DoanhThuPanel) activePanel).reloadData();
        else if (PANEL_PHIEU_NHAP.equals(panelKey)) ((com.fashionstore.view.nvkho.PhieuNhapKhoPanel) activePanel).reloadData();
        else if (PANEL_PHIEU_XUAT.equals(panelKey)) ((com.fashionstore.view.nvkho.PhieuXuatTraPanel) activePanel).reloadData();
        else if (PANEL_KHACH_HANG.equals(panelKey)) ((com.fashionstore.view.quanly.KhachHangPanel) activePanel).reloadData();
    }

    private void setActiveButton(String panelKey) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton button = entry.getValue();
            button.setBackground(entry.getKey().equals(panelKey) ? UIHelper.BG_SIDEBAR_ACTIVE : UIHelper.BG_SIDEBAR);
        }
    }
}
