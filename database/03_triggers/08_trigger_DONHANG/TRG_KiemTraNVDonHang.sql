CREATE OR REPLACE TRIGGER TRG_KiemTraNVDonHang
BEFORE INSERT OR UPDATE ON DONHANG
FOR EACH ROW
DECLARE
    v_TrangThai VARCHAR2(30);
BEGIN
    SELECT TrangThaiLamViec INTO v_TrangThai
    FROM NHANVIEN
    WHERE MaNV = :NEW.MaNV;

    IF v_TrangThai = 'Da nghi viec' THEN
        RAISE_APPLICATION_ERROR(-20010,
            'Loi: Nhan vien nay da nghi viec, khong the tao don hang!');
    END IF;
END;
