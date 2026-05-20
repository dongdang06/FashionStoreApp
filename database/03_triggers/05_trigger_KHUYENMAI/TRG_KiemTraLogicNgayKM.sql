CREATE OR REPLACE TRIGGER TRG_KiemTraLogicNgayKM
BEFORE INSERT OR UPDATE ON KHUYENMAI
FOR EACH ROW
BEGIN
    IF TRUNC(:NEW.NgayBatDau) < TRUNC(SYSDATE) THEN
        RAISE_APPLICATION_ERROR(-20005, 'Loi: Ngay bat dau khuyen mai khong duoc nho hon ngay hien tai!');
    END IF;

    IF :NEW.NgayKetThuc <= :NEW.NgayBatDau THEN
        RAISE_APPLICATION_ERROR(-20006, 'Loi: Ngay ket thuc khuyen mai phai lon hon ngay bat dau!');
    END IF;
END;
