CREATE OR REPLACE PROCEDURE PROC_DangNhap (
    p_UserName IN VARCHAR2,
    p_PassWord IN VARCHAR2,
    p_Status OUT VARCHAR2,
    p_ChucVu OUT NVARCHAR2
) AS
    v_DbPass VARCHAR2(255);
    v_TrangThai VARCHAR2(30);
    v_MaNV VARCHAR2(20);
BEGIN
    SELECT PassWord, TrangThai, MaNV INTO v_DbPass, v_TrangThai, v_MaNV
    FROM TAIKHOAN 
    WHERE UserName = p_UserName;

    IF v_TrangThai = 'Bi khoa' THEN
        p_Status := 'BLOCKED';
        p_ChucVu := NULL;
    ELSIF v_DbPass <> p_PassWord THEN
        p_Status := 'WRONG_PASS';
        p_ChucVu := NULL;
    ELSE
        p_Status := 'SUCCESS';
        SELECT ChucVu INTO p_ChucVu FROM NHANVIEN WHERE MaNV = v_MaNV;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_Status := 'NOT_FOUND';
        p_ChucVu := NULL;
END;