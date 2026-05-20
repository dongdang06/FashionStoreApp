-- Trigger kiểm tra giá khuyến mãi phải nhỏ hơn giá bán hiện tại của biến thể
CREATE OR REPLACE TRIGGER TRG_KiemTraGiaKhuyenMai
BEFORE INSERT OR UPDATE ON CHITIETKHUYENMAI
FOR EACH ROW
DECLARE
    v_GiaBan NUMBER;
BEGIN
    SELECT GiaBan INTO v_GiaBan
    FROM BIENTHESANPHAM
    WHERE MaBienThe = :NEW.MaBienThe;

    IF :NEW.GiaKhuyenMai >= v_GiaBan THEN
        RAISE_APPLICATION_ERROR(-20008,
            'Loi: Gia khuyen mai phai nho hon gia ban hien tai cua bien the san pham!');
    END IF;
END;
