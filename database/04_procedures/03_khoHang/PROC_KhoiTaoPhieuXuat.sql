CREATE OR REPLACE PROCEDURE PROC_KHOI_TAO_PHIEUXUAT (
    p_MaPhieuTra IN VARCHAR2, 
    p_MaNCC IN VARCHAR2, 
    p_MaNV IN VARCHAR2, 
    p_LyDo IN NVARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM PHIEUXUATTRA WHERE MaPhieuTra = p_MaPhieuTra;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MAPHIEUTRA'; RETURN;
    END IF;

    INSERT INTO PHIEUXUATTRA (MaPhieuTra, MaNCC, MaNV, NgayTra, LyDo)
    VALUES (p_MaPhieuTra, p_MaNCC, p_MaNV, SYSDATE, p_LyDo);

    COMMIT;
    p_Result := 'SUCCESS';
END;