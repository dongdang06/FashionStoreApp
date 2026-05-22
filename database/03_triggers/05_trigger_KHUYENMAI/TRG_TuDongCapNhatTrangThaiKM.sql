CREATE OR REPLACE TRIGGER TRG_TuDongCapNhatTrangThaiKM
BEFORE INSERT OR UPDATE ON KHUYENMAI
FOR EACH ROW
BEGIN
    IF TRUNC(:NEW.NgayKetThuc) < TRUNC(SYSDATE) THEN
        :NEW.TrangThaiKM := 'Ket thuc';
    ELSIF TRUNC(:NEW.NgayBatDau) > TRUNC(SYSDATE) THEN
        :NEW.TrangThaiKM := 'Chua bat dau';
    ELSE
        :NEW.TrangThaiKM := 'Dang dien ra';
    END IF;
END;
