CREATE OR REPLACE TRIGGER TRG_CongTonKho
AFTER INSERT OR UPDATE OR DELETE ON CHITIETPHIEUNHAP
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        UPDATE BIENTHESANPHAM
        SET SoLuongTon = SoLuongTon + :NEW.SoLuongNhap
        WHERE MaBienThe = :NEW.MaBienThe;
    ELSIF UPDATING THEN
        IF :NEW.MaBienThe <> :OLD.MaBienThe THEN
            -- Subtract old variant stock
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon - :OLD.SoLuongNhap
            WHERE MaBienThe = :OLD.MaBienThe;
            -- Add new variant stock
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon + :NEW.SoLuongNhap
            WHERE MaBienThe = :NEW.MaBienThe;
        ELSE
            -- Same variant, adjust quantity difference
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon + (:NEW.SoLuongNhap - :OLD.SoLuongNhap)
            WHERE MaBienThe = :NEW.MaBienThe;
        END IF;
    ELSIF DELETING THEN
        UPDATE BIENTHESANPHAM
        SET SoLuongTon = SoLuongTon - :OLD.SoLuongNhap
        WHERE MaBienThe = :OLD.MaBienThe;
    END IF;
END;