-- Trigger kiểm tra mã khuyến mãi áp dụng cho đơn hàng còn hiệu lực
CREATE OR REPLACE TRIGGER TRG_KiemTraKhuyenMaiConHieuLuc
BEFORE INSERT OR UPDATE ON DONHANG
FOR EACH ROW
DECLARE
    v_NgayBatDau DATE;
    v_NgayKetThuc DATE;
BEGIN
    IF :NEW.MaKM IS NOT NULL THEN
        SELECT NgayBatDau, NgayKetThuc
        INTO v_NgayBatDau, v_NgayKetThuc
        FROM KHUYENMAI
        WHERE MaKM = :NEW.MaKM;

        IF TRUNC(SYSDATE) < TRUNC(v_NgayBatDau) THEN
            RAISE_APPLICATION_ERROR(-20009,
                'Loi: Chuong trinh khuyen mai nay chua bat dau!');
        END IF;

        IF TRUNC(SYSDATE) > TRUNC(v_NgayKetThuc) THEN
            RAISE_APPLICATION_ERROR(-20009,
                'Loi: Chuong trinh khuyen mai nay da ket thuc!');
        END IF;
    END IF;
END;
