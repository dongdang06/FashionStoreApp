CREATE OR REPLACE TRIGGER TRG_ChanXoaBienTheCoTonKho
BEFORE DELETE ON BIENTHESANPHAM
FOR EACH ROW
BEGIN
    IF :OLD.SoLuongTon > 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'Loi: Khong the xoa bien the san pham nay vi van con hang ton trong kho!');
    END IF;
END;