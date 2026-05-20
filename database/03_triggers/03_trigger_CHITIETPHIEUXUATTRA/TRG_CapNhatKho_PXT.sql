CREATE OR REPLACE TRIGGER TRG_CapNhatKho_PXT
AFTER INSERT OR UPDATE OR DELETE ON CHITIETPHIEUXUATTRA
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        UPDATE BIENTHESANPHAM
        SET SoLuongTon = SoLuongTon - :NEW.SoLuong
        WHERE MaBienThe = :NEW.MaBienThe;
    ELSIF UPDATING THEN
        IF :NEW.MaBienThe <> :OLD.MaBienThe THEN
            -- Restore old variant stock
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon + :OLD.SoLuong
            WHERE MaBienThe = :OLD.MaBienThe;
            -- Subtract new variant stock
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon - :NEW.SoLuong
            WHERE MaBienThe = :NEW.MaBienThe;
        ELSE
            -- Same variant, adjust quantity difference
            UPDATE BIENTHESANPHAM
            SET SoLuongTon = SoLuongTon - (:NEW.SoLuong - :OLD.SoLuong)
            WHERE MaBienThe = :NEW.MaBienThe;
        END IF;
    ELSIF DELETING THEN
        UPDATE BIENTHESANPHAM
        SET SoLuongTon = SoLuongTon + :OLD.SoLuong
        WHERE MaBienThe = :OLD.MaBienThe;
    END IF;
END;
