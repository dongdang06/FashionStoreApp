CREATE OR REPLACE TRIGGER TRG_KiemTraGiaNhap
BEFORE INSERT OR UPDATE ON CHITIETPHIEUNHAP
FOR EACH ROW
DECLARE
    v_GiaBan NUMBER;
BEGIN
    SELECT GiaBan INTO v_GiaBan
    FROM BIENTHESANPHAM
    WHERE MaBienThe = :NEW.MaBienThe;

    IF :NEW.GiaNhap >= v_GiaBan THEN
        RAISE_APPLICATION_ERROR(-20003, 'Loi: Gia nhap khong duoc lon hon hoac bang gia ban hien tai!');
    END IF;
END;