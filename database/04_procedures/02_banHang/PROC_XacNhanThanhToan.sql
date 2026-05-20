CREATE OR REPLACE PROCEDURE PROC_XacNhanThanhToan (
    p_MaHD IN VARCHAR2, p_MaDH IN VARCHAR2, p_DiemSuDung IN NUMBER,
    p_PhuongThucTT IN VARCHAR2, p_GhiChu IN NVARCHAR2, p_MaNV IN VARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_TongTienDH NUMBER;
    v_DiemNhanDuoc NUMBER;
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM HOADON WHERE MaHD = p_MaHD;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MAHD'; RETURN;
    END IF;

    SELECT TongTienDH INTO v_TongTienDH FROM DONHANG WHERE MaDH = p_MaDH;

    v_DiemNhanDuoc := TRUNC(v_TongTienDH / 100000);

    UPDATE DONHANG 
    SET DiemSuDung = p_DiemSuDung, DiemNhanDuoc = v_DiemNhanDuoc 
    WHERE MaDH = p_MaDH;

    INSERT INTO HOADON (MaHD, MaDH, NgayXuat, TongTienHD, PhuongThucTT, GhiChu, MaNV)
    VALUES (p_MaHD, p_MaDH, SYSDATE, 0, p_PhuongThucTT, p_GhiChu, p_MaNV);

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_Result := 'ERR_SYSTEM: ' || SQLERRM;
END;