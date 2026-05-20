CREATE OR REPLACE TRIGGER TRG_KiemTraSoLuongXuat
BEFORE INSERT ON CHITIETPHIEUXUATTRA
FOR EACH ROW
DECLARE
    v_SoLuongTon NUMBER;
BEGIN
    SELECT SoLuongTon INTO v_SoLuongTon
    FROM BIENTHESANPHAM
    WHERE MaBienThe = :NEW.MaBienThe;

    IF :NEW.SoLuong > v_SoLuongTon THEN
        RAISE_APPLICATION_ERROR(-20004, 'Loi: So luong xuat tra vuot qua so luong hang hien dang ton trong kho!');
    END IF;
END;
