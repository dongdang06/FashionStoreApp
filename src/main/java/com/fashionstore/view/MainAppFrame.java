package com.fashionstore.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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

    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final DonHangPanel donHangPanel = new DonHangPanel();
    private final HoaDonPanel hoaDonPanel = new HoaDonPanel();
    private final SanPhamPanel sanPhamPanel = new SanPhamPanel();
    private final com.fashionstore.view.quanly.BienTheSanPhamPanel bienTheQuanLyPanel =
            new com.fashionstore.view.quanly.BienTheSanPhamPanel();
    private final DanhMucSanPhamPanel danhMucPanel = new DanhMucSanPhamPanel();
    private final NhaCungCapPanel nhaCungCapPanel = new NhaCungCapPanel();
    private final NhanVienPanel nhanVienPanel = new NhanVienPanel();
    private final KhuyenMaiPanel khuyenMaiPanel = new KhuyenMaiPanel();
    private final com.fashionstore.view.quanly.DoanhThuPanel doanhThuQuanLyPanel =
            new com.fashionstore.view.quanly.DoanhThuPanel();
    private final com.fashionstore.view.nvketoan.DoanhThuPanel doanhThuKeToanPanel =
            new com.fashionstore.view.nvketoan.DoanhThuPanel();
    private final com.fashionstore.view.nvkho.BienTheSanPhamPanel bienTheKhoPanel =
            new com.fashionstore.view.nvkho.BienTheSanPhamPanel();
    private final com.fashionstore.view.nvkho.PhieuNhapKhoPanel phieuNhapPanel =
            new com.fashionstore.view.nvkho.PhieuNhapKhoPanel();
    private final com.fashionstore.view.nvkho.PhieuXuatTraPanel phieuXuatPanel =
            new com.fashionstore.view.nvkho.PhieuXuatTraPanel();

    public MainAppFrame() {
        setTitle("Fashion Store - All Views");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 800);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();
        buildContent();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        showPanel(PANEL_DASHBOARD);
    }

    private void buildContent() {
        contentPanel.setBackground(new Color(245, 246, 250));
        contentPanel.add(dashboardPanel, PANEL_DASHBOARD);
        contentPanel.add(donHangPanel, PANEL_DON_HANG);
        contentPanel.add(hoaDonPanel, PANEL_HOA_DON);
        contentPanel.add(sanPhamPanel, PANEL_SAN_PHAM);
        contentPanel.add(bienTheQuanLyPanel, PANEL_BIEN_THE_QL);
        contentPanel.add(danhMucPanel, PANEL_DANH_MUC);
        contentPanel.add(nhaCungCapPanel, PANEL_NHA_CUNG_CAP);
        contentPanel.add(nhanVienPanel, PANEL_NHAN_VIEN);
        contentPanel.add(khuyenMaiPanel, PANEL_KHUYEN_MAI);
        contentPanel.add(doanhThuQuanLyPanel, PANEL_DOANH_THU_QL);
        contentPanel.add(doanhThuKeToanPanel, PANEL_DOANH_THU_KT);
        contentPanel.add(bienTheKhoPanel, PANEL_BIEN_THE_KHO);
        contentPanel.add(phieuNhapPanel, PANEL_PHIEU_NHAP);
        contentPanel.add(phieuXuatPanel, PANEL_PHIEU_XUAT);
    }

    private JPanel buildSidebar() {
        Color sidebarBg = new Color(34, 28, 79);
        Color sidebarText = new Color(230, 230, 240);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("FASHION STORE");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setBorder(BorderFactory.createEmptyBorder(20, 18, 12, 10));
        sidebar.add(brand);

        JLabel user = new JLabel("Nguyen Van A", SwingConstants.LEFT);
        user.setForeground(sidebarText);
        user.setFont(new Font("Segoe UI", Font.BOLD, 12));
        user.setBorder(BorderFactory.createEmptyBorder(10, 18, 2, 10));
        sidebar.add(user);

        JLabel role = new JLabel("All modules", SwingConstants.LEFT);
        role.setForeground(new Color(170, 170, 200));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 10));
        sidebar.add(role);

        sidebar.add(createNavButton("Dashboard", PANEL_DASHBOARD));
        sidebar.add(createNavButton("Don hang", PANEL_DON_HANG));
        sidebar.add(createNavButton("Hoa don", PANEL_HOA_DON));
        sidebar.add(createNavButton("San pham", PANEL_SAN_PHAM));
        sidebar.add(createNavButton("Bien the (QL)", PANEL_BIEN_THE_QL));
        sidebar.add(createNavButton("Danh muc", PANEL_DANH_MUC));
        sidebar.add(createNavButton("Nha cung cap", PANEL_NHA_CUNG_CAP));
        sidebar.add(createNavButton("Nhan vien", PANEL_NHAN_VIEN));
        sidebar.add(createNavButton("Khuyen mai", PANEL_KHUYEN_MAI));
        sidebar.add(createNavButton("Doanh thu (QL)", PANEL_DOANH_THU_QL));
        sidebar.add(createNavButton("Doanh thu (Ke toan)", PANEL_DOANH_THU_KT));
        sidebar.add(createNavButton("Bien the (Kho)", PANEL_BIEN_THE_KHO));
        sidebar.add(createNavButton("Phieu nhap kho", PANEL_PHIEU_NHAP));
        sidebar.add(createNavButton("Phieu xuat tra", PANEL_PHIEU_XUAT));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createNavButton(String text, String panelKey) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(220, 220, 230));
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 10));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(34, 28, 79));
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addActionListener(event -> showPanel(panelKey));
        navButtons.put(panelKey, button);
        return button;
    }

    private void showPanel(String panelKey) {
        cardLayout.show(contentPanel, panelKey);
        setActiveButton(panelKey);

        if (PANEL_DASHBOARD.equals(panelKey)) {
            dashboardPanel.reloadData();
        } else if (PANEL_DON_HANG.equals(panelKey)) {
            donHangPanel.reloadData();
        } else if (PANEL_HOA_DON.equals(panelKey)) {
            hoaDonPanel.reloadData();
        } else if (PANEL_SAN_PHAM.equals(panelKey)) {
            sanPhamPanel.reloadData();
        } else if (PANEL_BIEN_THE_QL.equals(panelKey)) {
            bienTheQuanLyPanel.reloadData();
        } else if (PANEL_DANH_MUC.equals(panelKey)) {
            danhMucPanel.reloadData();
        } else if (PANEL_NHA_CUNG_CAP.equals(panelKey)) {
            nhaCungCapPanel.reloadData();
        } else if (PANEL_NHAN_VIEN.equals(panelKey)) {
            nhanVienPanel.reloadData();
        } else if (PANEL_KHUYEN_MAI.equals(panelKey)) {
            khuyenMaiPanel.reloadData();
        } else if (PANEL_DOANH_THU_QL.equals(panelKey)) {
            doanhThuQuanLyPanel.reloadData();
        } else if (PANEL_DOANH_THU_KT.equals(panelKey)) {
            doanhThuKeToanPanel.reloadData();
        } else if (PANEL_BIEN_THE_KHO.equals(panelKey)) {
            bienTheKhoPanel.reloadData();
        } else if (PANEL_PHIEU_NHAP.equals(panelKey)) {
            phieuNhapPanel.reloadData();
        } else if (PANEL_PHIEU_XUAT.equals(panelKey)) {
            phieuXuatPanel.reloadData();
        }
    }

    private void setActiveButton(String panelKey) {
        Color normal = new Color(34, 28, 79);
        Color active = new Color(47, 38, 102);
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton button = entry.getValue();
            button.setBackground(entry.getKey().equals(panelKey) ? active : normal);
        }
    }
}
