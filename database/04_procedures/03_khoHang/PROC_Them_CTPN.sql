CREATE OR REPLACE PROCEDURE PROC_Them_CTPN (
    p_MaPN IN VARCHAR2, p_MaBienThe IN VARCHAR2, p_SoLuongNhap IN NUMBER, p_GiaNhap IN NUMBER,
    p_Result OUT VARCHAR2
) AS
BEGIN
    INSERT INTO CHITIETPHIEUNHAP (MaPN, MaBienThe, SoLuongNhap, GiaNhap)
    VALUES (p_MaPN, p_MaBienThe, p_SoLuongNhap, p_GiaNhap);

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        p_Result := 'LỖI: ' || SQLERRM;
END;