CREATE OR REPLACE PROCEDURE PROC_DangNhap (
    p_UserName IN VARCHAR2,
    p_PassWord IN VARCHAR2,
    p_Cursor OUT SYS_REFCURSOR,
    p_Status OUT VARCHAR2
) AS
    v_DbPass VARCHAR2(255);
    v_TrangThai VARCHAR2(30);
BEGIN
    -- Tìm tài khoản theo UserName
    SELECT PassWord, TrangThai INTO v_DbPass, v_TrangThai
    FROM TAIKHOAN
    WHERE UserName = p_UserName;

    IF v_TrangThai = 'Bi khoa' THEN
        p_Status := 'BLOCKED';
        -- Vẫn trả về thông tin tài khoản để hiển thị thông báo phù hợp
        OPEN p_Cursor FOR
            SELECT MaTaiKhoan, MaNV, UserName, PassWord, NgayTao, TrangThai, VaiTro
            FROM TAIKHOAN WHERE UserName = p_UserName;
    ELSIF v_DbPass <> p_PassWord THEN
        p_Status := 'WRONG_PASS';
        OPEN p_Cursor FOR
            SELECT NULL AS MaTaiKhoan, NULL AS MaNV, NULL AS UserName,
                   NULL AS PassWord, NULL AS NgayTao, NULL AS TrangThai, NULL AS VaiTro
            FROM DUAL WHERE 1 = 0;
    ELSE
        p_Status := 'SUCCESS';
        OPEN p_Cursor FOR
            SELECT MaTaiKhoan, MaNV, UserName, PassWord, NgayTao, TrangThai, VaiTro
            FROM TAIKHOAN WHERE UserName = p_UserName;
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_Status := 'NOT_FOUND';
        OPEN p_Cursor FOR
            SELECT NULL AS MaTaiKhoan, NULL AS MaNV, NULL AS UserName,
                   NULL AS PassWord, NULL AS NgayTao, NULL AS TrangThai, NULL AS VaiTro
            FROM DUAL WHERE 1 = 0;
END;
/