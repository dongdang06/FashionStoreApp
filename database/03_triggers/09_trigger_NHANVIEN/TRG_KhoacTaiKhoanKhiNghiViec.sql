-- Trigger tự động khóa tài khoản đăng nhập khi nhân viên nghỉ việc
CREATE OR REPLACE TRIGGER TRG_KhoacTaiKhoanKhiNghiViec
AFTER UPDATE OF TrangThaiLamViec ON NHANVIEN
FOR EACH ROW
BEGIN
    IF :NEW.TrangThaiLamViec = 'Da nghi viec' AND :OLD.TrangThaiLamViec <> 'Da nghi viec' THEN
        UPDATE TAIKHOAN
        SET TrangThai = 'Bi khoa'
        WHERE MaNV = :NEW.MaNV;
    END IF;
END;
