-- Trigger kiểm tra nhân viên lập phiếu xuất trả phải đang làm việc
CREATE OR REPLACE TRIGGER TRG_KiemTraNVPhieuXuatTra
BEFORE INSERT OR UPDATE ON PHIEUXUATTRA
FOR EACH ROW
DECLARE
    v_TrangThai VARCHAR2(30);
BEGIN
    SELECT TrangThaiLamViec INTO v_TrangThai
    FROM NHANVIEN
    WHERE MaNV = :NEW.MaNV;

    IF v_TrangThai = 'Da nghi viec' THEN
        RAISE_APPLICATION_ERROR(-20010,
            'Loi: Nhan vien nay da nghi viec, khong the tao phieu xuat tra hang!');
    END IF;
END;
