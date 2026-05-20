-- Trigger kiểm tra nhà cung cấp trong phiếu nhập phải đang hợp tác
CREATE OR REPLACE TRIGGER TRG_KiemTraNCCPhieuNhap
BEFORE INSERT OR UPDATE ON PHIEUNHAP
FOR EACH ROW
DECLARE
    v_TrangThai VARCHAR2(20);
BEGIN
    SELECT TrangThaiNCC INTO v_TrangThai
    FROM NHACUNGCAP
    WHERE MaNCC = :NEW.MaNCC;

    IF v_TrangThai = 'Ngung hop tac' THEN
        RAISE_APPLICATION_ERROR(-20011,
            'Loi: Nha cung cap nay da ngung hop tac, khong the tao phieu nhap hang!');
    END IF;
END;
