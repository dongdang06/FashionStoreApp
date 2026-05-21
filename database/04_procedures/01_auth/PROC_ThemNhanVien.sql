CREATE OR REPLACE PROCEDURE PROC_Them_NhanVien (
    p_MaNV IN VARCHAR2,
    p_HoTen IN NVARCHAR2,
    p_Email IN VARCHAR2,
    p_SDT IN VARCHAR2,
    p_ChucVu IN NVARCHAR2,
    p_VaiTro IN NVARCHAR2,
    p_Result OUT VARCHAR2
) AS
    v_Count INT;
    v_MaxTK INT := 0;
    v_NextTK VARCHAR2(10);
    v_IdStr VARCHAR2(20);
BEGIN
    -- 1. Kiem tra trung ma NV
    SELECT COUNT(*) INTO v_Count FROM NHANVIEN WHERE MaNV = p_MaNV;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_MANV'; RETURN;
    END IF;

    -- 2. Kiem tra trung UserName (MaNV duoc dung lam UserName)
    SELECT COUNT(*) INTO v_Count FROM TAIKHOAN WHERE UserName = p_MaNV;
    IF v_Count > 0 THEN
        p_Result := 'ERR_DUP_USER'; RETURN;
    END IF;

    -- 3. Tu dong sinh MaTaiKhoan tiep theo (TK001, TK002, ...)
    FOR rec IN (SELECT MaTaiKhoan FROM TAIKHOAN) LOOP
        v_IdStr := rec.MaTaiKhoan;
        IF v_IdStr IS NOT NULL AND SUBSTR(v_IdStr, 1, 2) = 'TK' THEN
            BEGIN
                IF TO_NUMBER(SUBSTR(v_IdStr, 3)) > v_MaxTK THEN
                    v_MaxTK := TO_NUMBER(SUBSTR(v_IdStr, 3));
                END IF;
            EXCEPTION
                WHEN OTHERS THEN NULL; -- Bo qua gia tri khong hop le
            END;
        END IF;
    END LOOP;
    v_NextTK := 'TK' || LPAD(v_MaxTK + 1, 3, '0');

    -- 4. Insert vao bang NHANVIEN
    INSERT INTO NHANVIEN (MaNV, HoTen, Email, SDT, ChucVu, NgayVaoLam, TrangThaiLamViec)
    VALUES (p_MaNV, p_HoTen, p_Email, p_SDT, p_ChucVu, SYSDATE, 'Dang lam viec');

    -- 5. Insert vao bang TAIKHOAN voi mat khau mac dinh la '123456'
    INSERT INTO TAIKHOAN (MaTaiKhoan, MaNV, UserName, PassWord, VaiTro, NgayTao, TrangThai)
    VALUES (v_NextTK, p_MaNV, p_MaNV, '123456', p_VaiTro, SYSDATE, 'Hoat dong');

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_Result := 'ERR_SYSTEM: ' || SQLERRM;
END;
/