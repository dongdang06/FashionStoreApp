CREATE OR REPLACE TRIGGER TRG_TuDongCapNhatTrangThaiKM
BEFORE INSERT OR UPDATE ON KHUYENMAI
FOR EACH ROW
BEGIN
    IF TRUNC(:NEW.NgayKetThuc) < TRUNC(SYSDATE) THEN
        :NEW.TrangThaiKM := 'Ket thuc';
    ELSE
        :NEW.TrangThaiKM := 'Dang dien ra';
    END IF;
END;
