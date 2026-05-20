CREATE OR REPLACE PROCEDURE PROC_KhoiTaoPhieuNhap (
    p_MaPN IN VARCHAR2, p_MaNCC IN VARCHAR2, p_MaNV IN VARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM PHIEUNHAP WHERE MaPN = p_MaPN;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MAPN'; RETURN;
    END IF;

    INSERT INTO PHIEUNHAP (MaPN, NgayNhap, TongGiaTri, MaNCC, MaNV)
    VALUES (p_MaPN, SYSDATE, 0, p_MaNCC, p_MaNV);

    COMMIT;
    p_Result := 'SUCCESS';
END;