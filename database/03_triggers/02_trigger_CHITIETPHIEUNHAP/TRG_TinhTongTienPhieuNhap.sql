CREATE OR REPLACE TRIGGER TRG_TinhTongTienPhieuNhap
AFTER INSERT OR UPDATE OR DELETE ON CHITIETPHIEUNHAP
FOR EACH ROW
DECLARE
    v_MaPN VARCHAR2(10);
    v_ChenhLech NUMBER := 0;
BEGIN
    IF INSERTING THEN
        v_MaPN := :NEW.MaPN;
        v_ChenhLech := :NEW.SoLuongNhap * :NEW.GiaNhap;
    ELSIF UPDATING THEN
        v_MaPN := :NEW.MaPN;
        v_ChenhLech := (:NEW.SoLuongNhap * :NEW.GiaNhap) - (:OLD.SoLuongNhap * :OLD.GiaNhap);
    ELSIF DELETING THEN
        v_MaPN := :OLD.MaPN;
        v_ChenhLech := - (:OLD.SoLuongNhap * :OLD.GiaNhap);
    END IF;

    UPDATE PHIEUNHAP
    SET TongGiaTri = NVL(TongGiaTri, 0) + v_ChenhLech
    WHERE MaPN = v_MaPN;
END;
