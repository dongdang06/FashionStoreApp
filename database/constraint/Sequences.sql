-- ============================================================
-- SEQUENCES — Sinh mã tự động cho các bảng chính
-- Thay thế cho UUID random đang dùng trong Java
-- Sử dụng: 'DH' || LPAD(seq_DonHang.NEXTVAL, 5, '0') → DH00001
-- ============================================================

-- Đơn hàng
CREATE SEQUENCE seq_DonHang
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Hóa đơn
CREATE SEQUENCE seq_HoaDon
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Phiếu nhập kho
CREATE SEQUENCE seq_PhieuNhap
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Phiếu xuất trả
CREATE SEQUENCE seq_PhieuXuatTra
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Tài khoản (dùng trong proc_TaoTaiKhoanNhanVien)
CREATE SEQUENCE seq_TaiKhoan
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Khách hàng
CREATE SEQUENCE seq_KhachHang
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- ============================================================
-- VÍ DỤ SỬ DỤNG TRONG INSERT:
-- INSERT INTO DONHANG(MaDH, ...) 
-- VALUES ('DH' || LPAD(seq_DonHang.NEXTVAL, 5, '0'), ...);
-- → Kết quả: DH00001, DH00002, ...
-- ============================================================
