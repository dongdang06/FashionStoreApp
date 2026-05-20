-- 1. DANHMUC
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM01', N'Áo', NULL);
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM02', N'Quần', NULL);
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM03', N'Váy', NULL);
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM04', N'Áo Thun', 'DM01');
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM05', N'Áo Sơ Mi', 'DM01');
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM06', N'Quần Jeans', 'DM02');
INSERT INTO DANHMUC (MaDM, TenDM, MaDMCha) VALUES ('DM07', N'Quần Short', 'DM02');

-- 2. NHANVIEN
INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, TrangThaiLamViec)
VALUES ('NV001', N'Nguyễn Văn An', 'an.nguyen@fashion.com', '0901111111', N'Quản lý', 'Dang lam viec');

INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, TrangThaiLamViec)
VALUES ('NV002', N'Trần Thị Bình', 'binh.tran@fashion.com', '0902222222', N'Nhân viên bán hàng', 'Dang lam viec');

INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, TrangThaiLamViec)
VALUES ('NV003', N'Lê Minh Cường', 'cuong.le@fashion.com', '0903333333', N'Nhân viên kho', 'Dang lam viec');

INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, TrangThaiLamViec)
VALUES ('NV004', N'Phạm Thị Dung', 'dung.pham@fashion.com', '0904444444', N'Nhân viên kế toán', 'Dang lam viec');

INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, TrangThaiLamViec)
VALUES ('NV005', N'Hoàng Văn Em', 'em.hoang@fashion.com', '0905555555', N'Nhân viên bán hàng', 'Da nghi viec');

-- 3. TAIKHOAN (phải có VaiTro theo constraint)
INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, TrangThai, VaiTro)
VALUES ('TK001', 'NV001', 'admin', '123456', 'Hoat dong', 'Quan ly');

INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, TrangThai, VaiTro)
VALUES ('TK002', 'NV002', 'binhtt', '123456', 'Hoat dong', 'Nhan vien ban hang');

INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, TrangThai, VaiTro)
VALUES ('TK003', 'NV003', 'cuonglm', '123456', 'Hoat dong', 'Nhan vien kho');

INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, TrangThai, VaiTro)
VALUES ('TK004', 'NV004', 'dungpt', '123456', 'Hoat dong', 'Nhan vien ke toan');

-- 4. NHACUNGCAP
INSERT INTO NHACUNGCAP (MaNCC, TenNCC, SDT, Email, DiaChi, TrangThaiNCC)
VALUES ('NCC01', N'Công Ty Vải Việt', '0281111111', 'vaiviet@gmail.com', N'123 Nguyễn Trãi, Q1, HCM', 'Hoat dong');

INSERT INTO NHACUNGCAP (MaNCC, TenNCC, SDT, Email, DiaChi, TrangThaiNCC)
VALUES ('NCC02', N'Xưởng May Phương Nam', '0282222222', 'phuongnam@gmail.com', N'456 Lê Lợi, Q3, HCM', 'Hoat dong');

INSERT INTO NHACUNGCAP (MaNCC, TenNCC, SDT, Email, DiaChi, TrangThaiNCC)
VALUES ('NCC03', N'Fashion Import Co.', '0283333333', 'fashionimport@gmail.com', N'789 Hai Bà Trưng, Q1, HCM', 'Hoat dong');

-- 5. KHACHHANG
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy)
VALUES ('KH001', N'Nguyễn Thị Lan', '0911111111', 150);

INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy)
VALUES ('KH002', N'Trần Văn Minh', '0922222222', 80);

INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy)
VALUES ('KH003', N'Lê Thị Nga', '0933333333', 320);

INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy)
VALUES ('KH004', N'Phạm Quốc Dũng', '0944444444', 0);

INSERT INTO KHACHHANG (MaKH, HoTen, SDT, DiemTichLuy)
VALUES ('KH005', N'Võ Thị Hoa', '0955555555', 210);

-- 6. SANPHAM
INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP001', 'DM04', N'Áo Thun Basic Cotton', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP002', 'DM04', N'Áo Thun Oversize Unisex', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP003', 'DM05', N'Áo Sơ Mi Trắng Công Sở', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP004', 'DM05', N'Áo Sơ Mi Kẻ Casual', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP005', 'DM06', N'Quần Jeans Slim Fit', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP006', 'DM06', N'Quần Jeans Rách Thời Trang', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP007', 'DM07', N'Quần Short Jean Nam', 'Dang ban');

INSERT INTO SANPHAM (MaSP, MaDM, TenSP, TrangThaiKD)
VALUES ('SP008', 'DM03', N'Váy Hoa Midi', 'Ngung ban');

-- 7. BIENTHESANPHAM
-- SP001 - Áo Thun Basic Cotton
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT001', 'SP001', N'Trắng', 'S', 150000, 50);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT002', 'SP001', N'Trắng', 'M', 150000, 60);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT003', 'SP001', N'Đen', 'S', 150000, 45);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT004', 'SP001', N'Đen', 'M', 150000, 55);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT005', 'SP001', N'Xám', 'L', 150000, 40);

-- SP002 - Áo Thun Oversize
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT006', 'SP002', N'Xanh Navy', 'M', 220000, 30);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT007', 'SP002', N'Xanh Navy', 'L', 220000, 35);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT008', 'SP002', N'Be', 'M', 220000, 25);

-- SP003 - Áo Sơ Mi Trắng
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT009', 'SP003', N'Trắng', 'S', 350000, 20);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT010', 'SP003', N'Trắng', 'M', 350000, 25);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT011', 'SP003', N'Trắng', 'L', 350000, 20);

-- SP004 - Áo Sơ Mi Kẻ
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT012', 'SP004', N'Xanh Kẻ', 'M', 280000, 18);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT013', 'SP004', N'Xanh Kẻ', 'L', 280000, 15);

-- SP005 - Quần Jeans Slim
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT014', 'SP005', N'Xanh Đậm', '28', 450000, 30);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT015', 'SP005', N'Xanh Đậm', '30', 450000, 35);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT016', 'SP005', N'Xanh Nhạt', '30', 450000, 25);

-- SP006 - Quần Jeans Rách
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT017', 'SP006', N'Xanh Rách', '28', 520000, 20);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT018', 'SP006', N'Xanh Rách', '30', 520000, 22);

-- SP007 - Quần Short
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT019', 'SP007', N'Xanh', 'M', 250000, 40);
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT020', 'SP007', N'Đen', 'L', 250000, 38);

-- SP008 - Váy Hoa (Ngưng bán)
INSERT INTO BIENTHESANPHAM (MaBienThe, MaSP, MauSac, KichThuoc, GiaBan, SoLuongTon)
VALUES ('BT021', 'SP008', N'Hoa Đỏ', 'S', 480000, 5);

-- 8. KHUYENMAI
INSERT INTO KHUYENMAI (MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM)
VALUES ('KM001', N'Khuyến Mãi Hè 2025', DATE '2025-06-01', DATE '2025-08-31', 100000, 'Ket thuc');

INSERT INTO KHUYENMAI (MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM)
VALUES ('KM002', N'Sale Tháng 5', DATE '2025-05-01', DATE '2025-05-31', 50000, 'Ket thuc');

INSERT INTO KHUYENMAI (MaKM, TenKM, NgayBatDau, NgayKetThuc, MucGiamToiDa, TrangThaiKM)
VALUES ('KM003', N'Flash Sale Cuối Năm', DATE '2025-12-20', DATE '2025-12-31', 200000, 'Ket thuc');

-- 9. CHITIETKHUYENMAI
INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES ('KM001', 'BT001', 120000);
INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES ('KM001', 'BT002', 120000);
INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES ('KM001', 'BT014', 390000);
INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES ('KM002', 'BT009', 299000);
INSERT INTO CHITIETKHUYENMAI (MaKM, MaBienThe, GiaKhuyenMai) VALUES ('KM003', 'BT017', 420000);

-- 10. DONHANG
INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
VALUES ('DH001', DATE '2025-05-10', 700000, 'KH001', NULL, 0, 7, 'NV002');

INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
VALUES ('DH002', DATE '2025-05-15', 450000, 'KH002', NULL, 0, 4, 'NV002');

INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
VALUES ('DH003', DATE '2025-06-05', 300000, 'KH003', 'KM001', 0, 3, 'NV002');

INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
VALUES ('DH004', DATE '2025-06-20', 1050000, 'KH001', NULL, 50, 10, 'NV002');

INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
VALUES ('DH005', DATE '2025-07-01', 520000, 'KH005', NULL, 0, 5, 'NV002');

-- 11. CHITIETDONHANG
-- DH001: 2 áo thun trắng S + 1 quần jeans 28
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH001', 'BT001', 2, 150000);
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH001', 'BT014', 1, 450000);

-- DH002: 1 quần jeans slim 30
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH002', 'BT015', 1, 450000);

-- DH003: 2 áo thun đen S (giá KM)
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH003', 'BT003', 2, 150000);

-- DH004: 1 áo sơ mi trắng M + 1 quần jeans rách 30
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH004', 'BT010', 1, 350000);
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH004', 'BT018', 1, 520000);

-- DH005: 1 quần jeans rách 28
INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua) VALUES ('DH005', 'BT017', 1, 520000);

-- 12. HOADON
INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
VALUES ('HD001', 'DH001', DATE '2025-05-10', 700000, 'Tien mat', NULL, 'NV002');

INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
VALUES ('HD002', 'DH002', DATE '2025-05-15', 450000, 'Chuyen khoan', NULL, 'NV002');

INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
VALUES ('HD003', 'DH003', DATE '2025-06-05', 300000, 'Tien mat', N'Khuyến mãi hè', 'NV002');

INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
VALUES ('HD004', 'DH004', DATE '2025-06-20', 1050000, 'Chuyen khoan', NULL, 'NV002');

INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
VALUES ('HD005', 'DH005', DATE '2025-07-01', 520000, 'Tien mat', NULL, 'NV002');

-- 13. PHIEUNHAP
INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV)
VALUES ('PN001', DATE '2025-04-01', 22400000, 'NCC01', 'NV003');

INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV)
VALUES ('PN002', DATE '2025-04-15', 47200000, 'NCC02', 'NV003');

INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV)
VALUES ('PN003', DATE '2025-05-05', 19800000, 'NCC01', 'NV003');

-- 14. CHITIETPHIEUNHAP
-- PN001
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN001', 'BT001', 100, 80000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN001', 'BT002', 100, 80000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN001', 'BT003', 80, 80000);

-- PN002
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN002', 'BT014', 60, 220000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN002', 'BT015', 60, 220000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN002', 'BT017', 40, 260000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN002', 'BT018', 40, 260000);

-- PN003
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN003', 'BT009', 40, 180000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN003', 'BT010', 40, 180000);
INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap) VALUES ('PN003', 'BT011', 30, 180000);

-- 15. PHIEUXUATTRA
INSERT INTO PHIEUXUATTRA (MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo)
VALUES ('PT001', 'NCC01', 'NV003', DATE '2025-04-20', N'Hàng bị lỗi vải');

-- 16. CHITIETPHIEUXUATTRA
INSERT INTO CHITIETPHIEUXUATTRA (MaPhieuTra, MaBienThe, SoLuong, GiaXuat) VALUES ('PT001', 'BT003', 5, 80000);

COMMIT;
