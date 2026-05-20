CREATE OR REPLACE PROCEDURE PROC_Them_NhanVien (
    p_MaNV IN VARCHAR2, p_HoTen IN NVARCHAR2, p_SDT IN VARCHAR2, p_ChucVu IN NVARCHAR2,
    p_MaTK IN VARCHAR2, p_UserName IN VARCHAR2, p_PassWord IN VARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count FROM NHANVIEN WHERE MaNV = p_MaNV;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MANV'; RETURN;
    END IF;

    SELECT COUNT(*) INTO v_Count FROM TAIKHOAN WHERE MaTaiKhoan = p_MaTK OR UserName = p_UserName;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_USER'; RETURN;
    END IF;

    INSERT INTO NHANVIEN (MaNV, HoTen, SDT, ChucVu, TrangThaiLamViec)
    VALUES (p_MaNV, p_HoTen, p_SDT, p_ChucVu, 'Dang lam viec');

    INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, NgayTao, TrangThai)
    VALUES (p_MaTK, p_MaNV, p_UserName, p_PassWord, SYSDATE, 'Hoat dong');

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_Result := 'ERR_SYSTEM: ' || SQLERRM;
END;