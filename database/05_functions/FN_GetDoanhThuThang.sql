-- Function tính doanh thu theo tháng với khoảng lệch tháng (p_MonthOffset)
-- p_MonthOffset = 0: Tháng này
-- p_MonthOffset = -1: Tháng trước
CREATE OR REPLACE FUNCTION FN_GetDoanhThuThang(p_MonthOffset IN NUMBER)
RETURN NUMBER IS
    v_DoanhThu NUMBER := 0;
BEGIN
    SELECT COALESCE(SUM(TongTienHD), 0) INTO v_DoanhThu
    FROM HOADON
    WHERE TRUNC(NgayXuat, 'MM') = TRUNC(ADD_MONTHS(SYSDATE, p_MonthOffset), 'MM');
    
    RETURN v_DoanhThu;
END FN_GetDoanhThuThang;
/
