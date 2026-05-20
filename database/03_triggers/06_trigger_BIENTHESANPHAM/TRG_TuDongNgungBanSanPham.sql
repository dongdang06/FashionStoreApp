CREATE OR REPLACE TRIGGER TRG_TuDongNgungBanSanPham
FOR UPDATE OF SoLuongTon ON BIENTHESANPHAM
COMPOUND TRIGGER

    TYPE t_MaSP_List IS TABLE OF BIENTHESANPHAM.MaSP%TYPE INDEX BY PLS_INTEGER;
    v_MaSP_List t_MaSP_List;
    v_Idx       PLS_INTEGER := 0;

    AFTER EACH ROW IS
    BEGIN
        v_Idx := v_Idx + 1;
        v_MaSP_List(v_Idx) := :NEW.MaSP;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
        v_TongTon NUMBER;
    BEGIN
        FOR i IN 1..v_MaSP_List.COUNT LOOP
            SELECT NVL(SUM(SoLuongTon), 0) INTO v_TongTon
            FROM BIENTHESANPHAM
            WHERE MaSP = v_MaSP_List(i);

            IF v_TongTon = 0 THEN
                UPDATE SANPHAM
                SET TrangThaiKD = 'Ngung ban'
                WHERE MaSP = v_MaSP_List(i);
            ELSE
                UPDATE SANPHAM
                SET TrangThaiKD = 'Dang ban'
                WHERE MaSP = v_MaSP_List(i);
            END IF;
        END LOOP;
    END AFTER STATEMENT;

END TRG_TuDongNgungBanSanPham;
