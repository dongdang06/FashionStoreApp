CREATE OR REPLACE TRIGGER TRG_KiemTraSoLuongMua
BEFORE INSERT OR UPDATE ON CHITIETDONHANG
FOR EACH ROW
DECLARE
    v_SoLuongTon NUMBER;
    v_SoLuongYeuCau NUMBER;
BEGIN
    SELECT SoLuongTon INTO v_SoLuongTon
    FROM BIENTHESANPHAM
    WHERE MaBienThe = :NEW.MaBienThe;

    IF INSERTING THEN
        v_SoLuongYeuCau := :NEW.SoLuong;
    ELSIF UPDATING THEN
        IF :NEW.MaBienThe <> :OLD.MaBienThe THEN
            v_SoLuongYeuCau := :NEW.SoLuong;
        ELSE
            v_SoLuongYeuCau := :NEW.SoLuong - :OLD.SoLuong;
        END IF;
    END IF;

    IF v_SoLuongYeuCau > v_SoLuongTon THEN
        RAISE_APPLICATION_ERROR(-20002, 'Loi: So luong hang trong kho khong du de dap ung don hang!');
    END IF;
END;
