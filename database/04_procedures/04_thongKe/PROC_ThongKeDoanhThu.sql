create or replace PROCEDURE PROC_ThongKeDoanhThu (
    p_TuNgay IN DATE,
    p_DenNgay IN DATE,
    p_Cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_Cursor FOR
        SELECT TRUNC(NgayXuat) AS Ngay, COUNT(MaHD) AS SoHoaDon, SUM(TongTienHD) AS DirectDoanhThu
        FROM HOADON
        WHERE TRUNC(NgayXuat) BETWEEN TRUNC(p_TuNgay) AND TRUNC(p_DenNgay)
        GROUP BY TRUNC(NgayXuat)
        ORDER BY TRUNC(NgayXuat) ASC;
END;