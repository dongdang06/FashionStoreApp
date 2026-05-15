 
package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fashionstore.controller.DoanhThuController;
import com.fashionstore.model.DashboardStats;

public class DoanhThuPanel extends JPanel {
	private final DoanhThuController doanhThuController = new DoanhThuController();

	private final JLabel currentValue = new JLabel("0");
	private final JLabel lastValue = new JLabel("0");
	private final JLabel changeValue = new JLabel("0%");

	public DoanhThuPanel() {
		setLayout(new BorderLayout());
		setBackground(new Color(245, 246, 250));

		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(20, 20, 20, 20),
				BorderFactory.createLineBorder(new Color(230, 230, 235))));

		JLabel title = new JLabel("Doanh thu");
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

		JPanel stats = new JPanel(new java.awt.GridLayout(3, 2, 8, 8));
		stats.setOpaque(false);
		stats.add(label("Doanh thu thang nay"));
		stats.add(currentValue);
		stats.add(label("Doanh thu thang truoc"));
		stats.add(lastValue);
		stats.add(label("Thay doi"));
		stats.add(changeValue);

		currentValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lastValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		changeValue.setFont(new Font("Segoe UI", Font.BOLD, 13));

		card.add(title, BorderLayout.NORTH);
		card.add(stats, BorderLayout.CENTER);

		add(card, BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

		reloadData();
	}

	private JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		label.setForeground(new Color(90, 90, 100));
		return label;
	}

	public void reloadData() {
		DashboardStats stats = doanhThuController.getDashboardStats();
		NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
		currentValue.setText(currency.format(stats.getCurrentMonthRevenue()));
		lastValue.setText(currency.format(stats.getLastMonthRevenue()));
		changeValue.setText(String.format("%.1f%%", stats.getMoMChangePercent()));
	}
}

