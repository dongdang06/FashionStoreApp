 
package com.fashionstore.view.quanly;

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

public class QuanLyMainFrame extends JFrame {
	private static final String PANEL_SAN_PHAM = "sanpham";
	private static final String PANEL_BIEN_THE = "bienthe";
	private static final String PANEL_DANH_MUC = "danhmuc";
	private static final String PANEL_NHA_CUNG_CAP = "nhacungcap";

	private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
	private final JPanel contentPanel = new JPanel(cardLayout);
	private final Map<String, JButton> navButtons = new LinkedHashMap<>();

	private final SanPhamPanel sanPhamPanel = new SanPhamPanel();
	private final BienTheSanPhamPanel bienThePanel = new BienTheSanPhamPanel();
	private final DanhMucSanPhamPanel danhMucPanel = new DanhMucSanPhamPanel();
	private final NhaCungCapPanel nhaCungCapPanel = new NhaCungCapPanel();

	public QuanLyMainFrame() {
		setTitle("Fashion Store - Quan ly");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 760);
		setLocationRelativeTo(null);

		JPanel sidebar = buildSidebar();
		buildContent();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(sidebar, BorderLayout.WEST);
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		showPanel(PANEL_SAN_PHAM);
	}

	private void buildContent() {
		contentPanel.setBackground(new Color(245, 246, 250));
		contentPanel.add(sanPhamPanel, PANEL_SAN_PHAM);
		contentPanel.add(bienThePanel, PANEL_BIEN_THE);
		contentPanel.add(danhMucPanel, PANEL_DANH_MUC);
		contentPanel.add(nhaCungCapPanel, PANEL_NHA_CUNG_CAP);
	}

	private JPanel buildSidebar() {
		Color sidebarBg = new Color(34, 28, 79);
		Color sidebarText = new Color(230, 230, 240);

		JPanel sidebar = new JPanel();
		sidebar.setBackground(sidebarBg);
		sidebar.setPreferredSize(new Dimension(230, 0));
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

		JLabel role = new JLabel("Quan ly", SwingConstants.LEFT);
		role.setForeground(new Color(170, 170, 200));
		role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		role.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 10));
		sidebar.add(role);

		sidebar.add(createNavButton("San pham", PANEL_SAN_PHAM));
		sidebar.add(createNavButton("Bien the", PANEL_BIEN_THE));
		sidebar.add(createNavButton("Danh muc", PANEL_DANH_MUC));
		sidebar.add(createNavButton("Nha cung cap", PANEL_NHA_CUNG_CAP));

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

		if (PANEL_SAN_PHAM.equals(panelKey)) {
			sanPhamPanel.reloadData();
		} else if (PANEL_BIEN_THE.equals(panelKey)) {
			bienThePanel.reloadData();
		} else if (PANEL_DANH_MUC.equals(panelKey)) {
			danhMucPanel.reloadData();
		} else if (PANEL_NHA_CUNG_CAP.equals(panelKey)) {
			nhaCungCapPanel.reloadData();
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

