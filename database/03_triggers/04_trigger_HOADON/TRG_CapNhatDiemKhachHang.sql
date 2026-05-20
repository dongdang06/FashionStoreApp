CREATE OR REPLACE TRIGGER TRG_CapNhatDiemKhachHang
AFTER INSERT ON HOADON
FOR EACH ROW
DECLARE
    v_MaKH VARCHAR2(10);
    v_DiemSuDung NUMBER;
    v_DiemNhanDuoc NUMBER;
BEGIN
    SELECT MaKH, NVL(DiemSuDung, 0) INTO v_MaKH, v_DiemSuDung
    FROM DONHANG
    WHERE MaDH = :NEW.MaDH;

    IF v_MaKH IS NOT NULL THEN        
        UPDATE KHACHHANG
        SET DiemTichLuy = NVL(DiemTichLuy, 0) - v_DiemSuDung + v_DiemNhanDuoc
        WHERE MaKH = v_MaKH;
    END IF;
END;