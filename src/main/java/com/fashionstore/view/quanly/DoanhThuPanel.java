package com.fashionstore.view.quanly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.fashionstore.controller.DoanhThuController;
import com.fashionstore.model.BaoCaoDoanhThu;
import com.fashionstore.util.UIHelper;

public class DoanhThuPanel extends JPanel {
    private final DoanhThuController doanhThuController = new DoanhThuController();

    private final JLabel lblTongDoanhThu = new JLabel("0 ₫");
    private final JLabel lblTongHoaDon = new JLabel("0");
    private final JLabel lblTongChiPhi = new JLabel("0 ₫");
    private final JLabel lblTongLoiNhuan = new JLabel("0 ₫");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Tiêu chí", "Số lượng", "Tổng doanh thu", "Tổng chi phí", "Tổng lợi nhuận" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private com.toedter.calendar.JDateChooser txtTuNgay;
    private com.toedter.calendar.JDateChooser txtDenNgay;
    private JComboBox<String> cbTieuChi;

    public DoanhThuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_CONTENT);
        setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        // --- Header (Bộ lọc) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Thống kê Doanh thu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Từ ngày:"));
        txtTuNgay = new com.toedter.calendar.JDateChooser();
        txtTuNgay.setDateFormatString("dd/MM/yyyy");
        try {
            txtTuNgay.setDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse("01/05/2026"));
        } catch (Exception e) {
        }
        txtTuNgay.setPreferredSize(new java.awt.Dimension(130, 26));
        filterPanel.add(txtTuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        txtDenNgay = new com.toedter.calendar.JDateChooser();
        txtDenNgay.setDateFormatString("dd/MM/yyyy");
        try {
            txtDenNgay.setDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse("31/05/2026"));
        } catch (Exception e) {
        }
        txtDenNgay.setPreferredSize(new java.awt.Dimension(130, 26));
        filterPanel.add(txtDenNgay);

        filterPanel.add(new JLabel("Theo:"));
        cbTieuChi = new JComboBox<>(new String[] { "Theo ngày", "Theo sản phẩm" });
        filterPanel.add(cbTieuChi);

        JButton btnTraCuu = new JButton("\uD83D\uDD0D Tra cứu");
        btnTraCuu.addActionListener(e -> reloadData());
        filterPanel.add(btnTraCuu);

        header.add(filterPanel, BorderLayout.CENTER);

        JButton btnInBaoCao = new JButton("\uD83D\uDDA8\uFE0F In báo cáo");
        boolean canEdit = com.fashionstore.util.SessionManager.hasPermission("Ke toan");
        btnInBaoCao.setEnabled(canEdit);
        btnInBaoCao.addActionListener(e -> inBaoCao());
        header.add(btnInBaoCao, BorderLayout.EAST);

        // --- Center (Thống kê + Bảng) ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Top cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.add(createCard("Tổng doanh thu", lblTongDoanhThu));
        cardsPanel.add(createCard("Tổng số hóa đơn", lblTongHoaDon));
        cardsPanel.add(createCard("Tổng chi phí", lblTongChiPhi));
        cardsPanel.add(createCard("Tổng lợi nhuận", lblTongLoiNhuan));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Table
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        reloadData();
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 235)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(90, 90, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(new Color(34, 28, 79));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void reloadData() {
        String tieuChi = cbTieuChi.getSelectedItem().toString();
        java.util.Date tuNgay = txtTuNgay.getDate();
        java.util.Date denNgay = txtDenNgay.getDate();
        List<BaoCaoDoanhThu> data = doanhThuController.getBaoCaoDoanhThu(tieuChi, tuNgay, denNgay);

        // Đổi tên cột theo tiêu chí
        if ("Theo sản phẩm".equals(tieuChi)) {
            table.getColumnModel().getColumn(0).setHeaderValue("Tên sản phẩm");
        } else {
            table.getColumnModel().getColumn(0).setHeaderValue("Ngày");
        }
        table.getTableHeader().repaint();

        tableModel.setRowCount(0);
        long tongTien = 0;
        int tongDon = 0;
        long tongChiPhi = 0;
        long tongLoiNhuan = 0;
        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (BaoCaoDoanhThu bc : data) {
            tableModel.addRow(new Object[] {
                    bc.getTieuChi(),
                    bc.getSoLuong(),
                    currency.format(bc.getTongTien()) + " ₫",
                    currency.format(bc.getTongChiPhi()) + " ₫",
                    currency.format(bc.getTongLoiNhuan()) + " ₫"
            });
            tongTien += bc.getTongTien();
            tongDon += bc.getSoLuong();
            tongChiPhi += bc.getTongChiPhi();
            tongLoiNhuan += bc.getTongLoiNhuan();
        }

        lblTongDoanhThu.setText(currency.format(tongTien) + " ₫");
        lblTongHoaDon.setText(String.valueOf(tongDon));
        lblTongChiPhi.setText(currency.format(tongChiPhi) + " ₫");
        lblTongLoiNhuan.setText(currency.format(tongLoiNhuan) + " ₫");
        if (tongLoiNhuan > 0) {
            lblTongLoiNhuan.setForeground(new Color(0, 150, 0));
        } else if (tongLoiNhuan < 0) {
            lblTongLoiNhuan.setForeground(Color.RED);
        } else {
            lblTongLoiNhuan.setForeground(new Color(34, 28, 79));
        }
    }

    private void inBaoCao() {
        // Thu thập dữ liệu hiện tại
        String tieuChi = cbTieuChi.getSelectedItem().toString();
        java.util.Date tuNgay = txtTuNgay.getDate();
        java.util.Date denNgay = txtDenNgay.getDate();
        List<BaoCaoDoanhThu> data = doanhThuController.getBaoCaoDoanhThu(tieuChi, tuNgay, denNgay);

        NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strTuNgay = tuNgay != null ? sdf.format(tuNgay) : "---";
        String strDenNgay = denNgay != null ? sdf.format(denNgay) : "---";

        long tongTien = 0, tongCp = 0, tongLn = 0;
        int tongDon = 0;
        for (BaoCaoDoanhThu bc : data) {
            tongTien += bc.getTongTien();
            tongCp += bc.getTongChiPhi();
            tongLn += bc.getTongLoiNhuan();
            tongDon += bc.getSoLuong();
        }
        final long fTongTien = tongTien, fTongCp = tongCp, fTongLn = tongLn;
        final int fTongDon = tongDon;

        // ─── Preview Panel ───────────────────────────────────────────
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawReport(g, getWidth(), data, tieuChi, strTuNgay, strDenNgay,
                        fTongTien, fTongCp, fTongLn, fTongDon, currency);
            }
        };
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setPreferredSize(new Dimension(680, 800));

        JScrollPane scrollPreview = new JScrollPane(previewPanel);
        scrollPreview.setPreferredSize(new Dimension(720, 560));

        // ─── Dialog ──────────────────────────────────────────────────
        JDialog dialog = new JDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Xem trước báo cáo – Thống kê Doanh thu", true);
        dialog.setLayout(new BorderLayout(8, 8));

        // Thanh tiêu đề dialog
        JLabel dlgTitle = new JLabel("  Xem trước báo cáo");
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dlgTitle.setForeground(Color.WHITE);
        dlgTitle.setOpaque(true);
        dlgTitle.setBackground(new Color(34, 28, 79));
        dlgTitle.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        dlgTitle.setPreferredSize(new Dimension(0, 42));
        dialog.add(dlgTitle, BorderLayout.NORTH);
        dialog.add(scrollPreview, BorderLayout.CENTER);

        // Nút bên dưới
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnPanel.setBackground(new Color(245, 246, 250));

        JButton btnPrint = new JButton("\uD83D\uDDA8 In báo cáo");
        btnPrint.setBackground(new Color(34, 28, 79));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.addActionListener(ev -> {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("BaoCaoDoanhThu");
            pj.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0)
                    return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double scaleX = pageFormat.getImageableWidth() / previewPanel.getWidth();
                double scaleY = pageFormat.getImageableHeight() / previewPanel.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2.scale(scale, scale);
                drawReport(g2, previewPanel.getWidth(), data, tieuChi, strTuNgay, strDenNgay,
                        fTongTien, fTongCp, fTongLn, fTongDon, currency);
                return Printable.PAGE_EXISTS;
            });
            if (pj.printDialog()) {
                try {
                    pj.print();
                    JOptionPane.showMessageDialog(dialog,
                            "Đã gửi lệnh in thành công!", "In báo cáo",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Lỗi khi in: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(ev -> dialog.dispose());
        btnPanel.add(btnClose);
        btnPanel.add(btnPrint);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Vẽ nội dung báo cáo lên Graphics (dùng chung cho preview và máy in).
     */
    private void drawReport(Graphics g, int pageWidth,
            List<BaoCaoDoanhThu> data, String tieuChi,
            String strTuNgay, String strDenNgay,
            long tongTien, long tongCp, long tongLn, int tongDon,
            NumberFormat currency) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int margin = 40;
        int w = pageWidth - margin * 2;
        int y = margin;

        // ── Header công ty ────────────────────────────────────────────
        g2.setColor(new Color(34, 28, 79));
        g2.fillRoundRect(margin, y, w, 64, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("FASHION STORE – BÁO CÁO DOANH THU", margin + 20, y + 28);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.drawString("Kỳ báo cáo: " + strTuNgay + "  →  " + strDenNgay
                + "       Phân loại: " + tieuChi, margin + 20, y + 50);
        y += 80;

        // ── Ngày xuất ─────────────────────────────────────────────────
        g2.setColor(new Color(120, 120, 130));
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        String now = new SimpleDateFormat("HH:mm  dd/MM/yyyy").format(new Date());
        g2.drawString("Ngày xuất: " + now, pageWidth - margin - 200, y);
        y += 20;

        // ── 4 thẻ tổng kết ────────────────────────────────────────────
        int cardW = (w - 30) / 4;
        int[][] cardColors = {
                { 63, 81, 181 }, // Doanh thu – xanh chàm
                { 30, 136, 229 }, // Số hóa đơn – xanh dương
                { 239, 108, 0 }, // Chi phí – cam
                { tongLn >= 0 ? 46 : 198, tongLn >= 0 ? 125 : 40, tongLn >= 0 ? 50 : 40 } // Lợi nhuận
        };
        String[] cardTitles = { "Tổng doanh thu", "Tổng hóa đơn", "Tổng chi phí", "Tổng lợi nhuận" };
        String[] cardValues = {
                currency.format(tongTien) + " ₫",
                String.valueOf(tongDon),
                currency.format(tongCp) + " ₫",
                currency.format(tongLn) + " ₫"
        };
        for (int i = 0; i < 4; i++) {
            int cx = margin + i * (cardW + 10);
            g2.setColor(new Color(cardColors[i][0], cardColors[i][1], cardColors[i][2]));
            g2.fillRoundRect(cx, y, cardW, 60, 10, 10);
            g2.setColor(new Color(255, 255, 255, 160));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString(cardTitles[i], cx + 10, y + 18);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString(cardValues[i], cx + 10, y + 42);
        }
        y += 76;

        // ── Bảng dữ liệu ─────────────────────────────────────────────
        boolean byDate = "Theo ngày".equals(tieuChi);
        String[] headers = byDate
                ? new String[] { "Ngày", "Số HĐ", "Doanh thu", "Chi phí", "Lợi nhuận" }
                : new String[] { "Sản phẩm", "SL bán", "Doanh thu", "Chi phí", "Lợi nhuận" };
        int[] colWidths = { byDate ? 90 : 190, 60, 140, 140, 140 };

        // Điều chỉnh tổng độ rộng khớp w
        int totalCol = 0;
        for (int cw : colWidths)
            totalCol += cw;
        float scale = (float) w / totalCol;
        for (int i = 0; i < colWidths.length; i++)
            colWidths[i] = (int) (colWidths[i] * scale);

        // Header row
        g2.setColor(new Color(34, 28, 79));
        g2.fillRect(margin, y, w, 28);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        int cx = margin + 8;
        for (int i = 0; i < headers.length; i++) {
            g2.drawString(headers[i], cx, y + 19);
            cx += colWidths[i];
        }
        y += 28;

        // Data rows
        for (int r = 0; r < data.size(); r++) {
            BaoCaoDoanhThu bc = data.get(r);
            g2.setColor(r % 2 == 0 ? Color.WHITE : new Color(245, 246, 252));
            g2.fillRect(margin, y, w, 26);
            g2.setColor(new Color(50, 50, 60));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            String[] cells = {
                    bc.getTieuChi(),
                    String.valueOf(bc.getSoLuong()),
                    currency.format(bc.getTongTien()) + " ₫",
                    currency.format(bc.getTongChiPhi()) + " ₫",
                    currency.format(bc.getTongLoiNhuan()) + " ₫"
            };
            cx = margin + 8;
            for (int i = 0; i < cells.length; i++) {
                if (i == 4) { // Lợi nhuận – màu
                    g2.setColor(bc.getTongLoiNhuan() >= 0 ? new Color(0, 130, 0) : Color.RED);
                } else {
                    g2.setColor(new Color(50, 50, 60));
                }
                g2.drawString(cells[i], cx, y + 18);
                cx += colWidths[i];
            }
            // Gạch ngăn hàng
            g2.setColor(new Color(220, 220, 228));
            g2.drawLine(margin, y + 26, margin + w, y + 26);
            y += 26;
        }

        // Dòng tổng cộng
        g2.setColor(new Color(240, 240, 248));
        g2.fillRect(margin, y, w, 30);
        g2.setColor(new Color(34, 28, 79));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String[] totals = {
                "TỔNG CỘNG",
                String.valueOf(tongDon),
                currency.format(tongTien) + " ₫",
                currency.format(tongCp) + " ₫",
                currency.format(tongLn) + " ₫"
        };
        cx = margin + 8;
        for (int i = 0; i < totals.length; i++) {
            if (i == 4)
                g2.setColor(tongLn >= 0 ? new Color(0, 120, 0) : Color.RED);
            else
                g2.setColor(new Color(34, 28, 79));
            g2.drawString(totals[i], cx, y + 21);
            cx += colWidths[i];
        }
        y += 46;

        // ── Chữ ký ───────────────────────────────────────────────────
        g2.setColor(new Color(100, 100, 110));
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        g2.drawString("Người lập báo cáo", margin + 20, y + 14);
        g2.drawString("Kế toán trưởng", pageWidth - margin - 130, y + 14);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString("(Ký, ghi rõ họ tên)", margin + 10, y + 28);
        g2.drawString("(Ký, ghi rõ họ tên)", pageWidth - margin - 140, y + 28);
    }
}
