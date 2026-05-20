CREATE OR REPLACE TRIGGER TRG_KiemTraSoLuongXuat
BEFORE INSERT OR UPDATE ON CHITIETPHIEUXUATTRA
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
        RAISE_APPLICATION_ERROR(-20004, 'Loi: So luong xuat tra vuot qua so luong hang hien dang ton trong kho!');
    END IF;
END;

