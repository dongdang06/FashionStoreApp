-- BỔ SUNG CỘT VaiTro VÀO BẢNG TAIKHOAN
ALTER TABLE TAIKHOAN
ADD VaiTro NVARCHAR2(30) DEFAULT 'Nhan vien ban hang'
    CHECK (VaiTro IN ('Quan ly', 'Nhan vien ban hang', 'Nhan vien kho', 'Nhan vien ke toan'));
