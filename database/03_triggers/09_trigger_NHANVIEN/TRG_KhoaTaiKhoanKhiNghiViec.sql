CREATE OR REPLACE TRIGGER TRG_KhoaTaiKhoanKhiNghiViec
AFTER UPDATE OF TrangThaiLamViec ON NHANVIEN
FOR EACH ROW
BEGIN
    IF :NEW.TrangThaiLamViec = 'Da nghi viec' AND :OLD.TrangThaiLamViec <> 'Da nghi viec' THEN
        UPDATE TAIKHOAN
        SET TrangThai = 'Bi khoa'
        WHERE MaNV = :NEW.MaNV;
    END IF;
END;
