-- Compound trigger tự động cập nhật TrangThaiKD của SANPHAM
-- khi tổng tồn kho của tất cả biến thể = 0 thì chuyển sang 'Ngung ban',
-- và ngược lại 'Dang ban' khi có hàng trở lại.
CREATE OR REPLACE TRIGGER TRG_TuDongNgungBanSanPham
FOR UPDATE OF SoLuongTon ON BIENTHESANPHAM
COMPOUND TRIGGER

    -- Danh sách MaSP bị ảnh hưởng trong statement hiện tại
    TYPE t_MaSP_List IS TABLE OF BIENTHESANPHAM.MaSP%TYPE INDEX BY PLS_INTEGER;
    v_MaSP_List t_MaSP_List;
    v_Idx       PLS_INTEGER := 0;

    -- Thu thập MaSP sau mỗi row bị cập nhật
    AFTER EACH ROW IS
    BEGIN
        v_Idx := v_Idx + 1;
        v_MaSP_List(v_Idx) := :NEW.MaSP;
    END AFTER EACH ROW;

    -- Sau khi toàn bộ statement hoàn thành, tính lại trạng thái từng sản phẩm
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
