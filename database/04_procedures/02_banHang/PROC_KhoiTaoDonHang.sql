CREATE OR REPLACE PROCEDURE PROC_KhoiTaoDonHang (
    p_MaDH IN VARCHAR2, p_MaKH IN VARCHAR2, p_MaNV IN VARCHAR2, p_MaKM IN VARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM DONHANG WHERE MaDH = p_MaDH;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MADH'; RETURN;
    END IF;

    INSERT INTO DONHANG (MaDH, NgayMua, TongTienDH, MaKH, MaKM, DiemSuDung, DiemNhanDuoc, MaNV)
    VALUES (p_MaDH, SYSDATE, 0, p_MaKH, p_MaKM, 0, 0, p_MaNV);
    
    COMMIT;
    p_Result := 'SUCCESS';
END;