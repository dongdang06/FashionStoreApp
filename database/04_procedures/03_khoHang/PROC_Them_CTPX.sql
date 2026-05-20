CREATE OR REPLACE PROCEDURE PROC_THEM_CT_PHIEUXUAT (
    p_MaPhieuTra IN VARCHAR2, 
    p_MaBienThe IN VARCHAR2, 
    p_SoLuong IN NUMBER,
    p_Result OUT VARCHAR2
) AS
    v_TonKho NUMBER;
BEGIN
    SELECT SoLuongTon INTO v_TonKho FROM BIENTHESANPHAM WHERE MaBienThe = p_MaBienThe;
    
    IF v_TonKho < p_SoLuong THEN
        p_Result := 'ERR_NOT_ENOUGH_STOCK'; RETURN;
    END IF;

    INSERT INTO CHITIETPHIEUXUATTRA (MaPhieuTra, MaBienThe, SoLuong)
    VALUES (p_MaPhieuTra, p_MaBienThe, p_SoLuong);

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        p_Result := 'LỖI: ' || SQLERRM;
END;