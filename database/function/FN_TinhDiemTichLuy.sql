-- Function tính số điểm tích lũy từ tổng tiền hóa đơn
-- 1 điểm cho mỗi 100,000 VND
CREATE OR REPLACE FUNCTION FN_TinhDiemTichLuy(p_TongTien IN NUMBER)
RETURN NUMBER IS
BEGIN
    IF p_TongTien IS NULL OR p_TongTien <= 0 THEN
        RETURN 0;
    END IF;
    RETURN FLOOR(p_TongTien / 100000);
END FN_TinhDiemTichLuy;
