-- ============================================================
-- BỔ SUNG CONSTRAINTS CÒN THIẾU — FashionStoreApp
-- Chạy file này SAU KHI đã tạo xong tất cả bảng
-- ============================================================


-- ============================================================
-- 1. TAIKHOAN — Mỗi nhân viên chỉ có đúng 1 tài khoản
-- ============================================================
ALTER TABLE TAIKHOAN
ADD CONSTRAINT uq_TaiKhoan_MaNV UNIQUE (MaNV);


-- ============================================================
-- 2. DONHANG — DiemSuDung và DiemNhanDuoc không được âm
-- ============================================================
ALTER TABLE DONHANG
ADD CONSTRAINT chk_DonHang_DiemSuDung
    CHECK (DiemSuDung >= 0);

ALTER TABLE DONHANG
ADD CONSTRAINT chk_DonHang_DiemNhanDuoc
    CHECK (DiemNhanDuoc >= 0);

ALTER TABLE DONHANG
ADD CONSTRAINT chk_DonHang_TongTien
    CHECK (TongTienDH >= 0);


-- ============================================================
-- 3. KHACHHANG — Điểm tích lũy không được âm
-- ============================================================
ALTER TABLE KHACHHANG
ADD CONSTRAINT chk_KhachHang_DiemTichLuy
    CHECK (DiemTichLuy >= 0);


-- ============================================================
-- 4. CHITIETPHIEUNHAP — Số lượng nhập và giá nhập phải dương
-- ============================================================
ALTER TABLE CHITIETPHIEUNHAP
ADD CONSTRAINT chk_CTPhieuNhap_SoLuong
    CHECK (SoLuongNhap > 0);

ALTER TABLE CHITIETPHIEUNHAP
ADD CONSTRAINT chk_CTPhieuNhap_GiaNhap
    CHECK (GiaNhap >= 0);


-- ============================================================
-- 5. CHITIETPHIEUXUATTRA — Số lượng xuất trả phải dương
-- ============================================================
ALTER TABLE CHITIETPHIEUXUATTRA
ADD CONSTRAINT chk_CTPhieuXuatTra_SoLuong
    CHECK (SoLuong > 0);


-- ============================================================
-- 6. CHITIETKHUYENMAI — Giá khuyến mãi phải dương
-- ============================================================
ALTER TABLE CHITIETKHUYENMAI
ADD CONSTRAINT chk_CTKhuyenMai_GiaKM
    CHECK (GiaKhuyenMai > 0);


-- ============================================================
-- 7. KHUYENMAI — Ngày kết thúc không được trước ngày bắt đầu
-- ============================================================
ALTER TABLE KHUYENMAI
ADD CONSTRAINT chk_KhuyenMai_NgayHopLe
    CHECK (NgayKetThuc >= NgayBatDau);


-- ============================================================
-- 8. NHACUNGCAP — Tên nhà cung cấp không được NULL
-- ============================================================
ALTER TABLE NHACUNGCAP
MODIFY TenNCC NVARCHAR2(100) NOT NULL;


-- ============================================================
-- 9. HOADON — Tổng tiền hóa đơn không được âm
-- ============================================================
ALTER TABLE HOADON
ADD CONSTRAINT chk_HoaDon_TongTien
    CHECK (TongTienHD >= 0);


-- ============================================================
-- 10. PHIEUNHAP — Tổng giá trị không được âm
-- ============================================================
ALTER TABLE PHIEUNHAP
ADD CONSTRAINT chk_PhieuNhap_TongGiaTri
    CHECK (TongGiaTri >= 0);


-- ============================================================
-- KIỂM TRA SAU KHI CHẠY:
-- SELECT constraint_name, table_name, constraint_type, status
-- FROM user_constraints
-- WHERE table_name IN (
--     'TAIKHOAN','DONHANG','KHACHHANG','CHITIETPHIEUNHAP',
--     'CHITIETPHIEUXUATTRA','CHITIETKHUYENMAI','KHUYENMAI',
--     'NHACUNGCAP','HOADON','PHIEUNHAP'
-- )
-- ORDER BY table_name, constraint_type;
-- ============================================================
