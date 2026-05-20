CREATE OR REPLACE TRIGGER TRG_TinhTongTienDonHang
AFTER INSERT OR UPDATE OR DELETE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_MaDH VARCHAR2(10);
    v_ChenhLech NUMBER := 0;
BEGIN
    IF INSERTING THEN
        v_MaDH := :NEW.MaDH;
        v_ChenhLech := :NEW.SoLuong * :NEW.GiaBanLucMua;
    ELSIF UPDATING THEN
        v_MaDH := :NEW.MaDH;
        v_ChenhLech := (:NEW.SoLuong * :NEW.GiaBanLucMua) - (:OLD.SoLuong * :OLD.GiaBanLucMua);
    ELSIF DELETING THEN
        v_MaDH := :OLD.MaDH;
        v_ChenhLech := - (:OLD.SoLuong * :OLD.GiaBanLucMua);
    END IF;

    UPDATE DONHANG
    SET TongTienDH = NVL(TongTienDH, 0) + v_ChenhLech
    WHERE MaDH = v_MaDH;
END;