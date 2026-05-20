CREATE OR REPLACE PROCEDURE PROC_Them_CTDH (
    p_MaDH IN VARCHAR2, p_MaBienThe IN VARCHAR2, p_SoLuong IN NUMBER,
    p_Result OUT VARCHAR2
) AS
    v_GiaBanThucTe NUMBER;
    v_Count INT;
BEGIN
    SELECT COUNT(*) INTO v_Count 
    FROM CHITIETKHUYENMAI ctkm
    JOIN KHUYENMAI km ON ctkm.MaKM = km.MaKM
    WHERE ctkm.MaBienThe = p_MaBienThe 
      AND km.TrangThaiKM = 'Dang dien ra'
      AND SYSDATE BETWEEN km.NgayBatDau AND km.NgayKetThuc;

    IF v_Count > 0 THEN
        SELECT ctkm.GiaKhuyenMai INTO v_GiaBanThucTe
        FROM CHITIETKHUYENMAI ctkm
        JOIN KHUYENMAI km ON ctkm.MaKM = km.MaKM
        WHERE ctkm.MaBienThe = p_MaBienThe AND km.TrangThaiKM = 'Dang dien ra' AND ROWNUM = 1;
    ELSE
        SELECT GiaBan INTO v_GiaBanThucTe FROM BIENTHESANPHAM WHERE MaBienThe = p_MaBienThe;
    END IF;

    INSERT INTO CHITIETDONHANG (MaDH, MaBienThe, SoLuong, GiaBanLucMua)
    VALUES (p_MaDH, p_MaBienThe, p_SoLuong, v_GiaBanThucTe);

    COMMIT;
    p_Result := 'SUCCESS';
EXCEPTION
    WHEN OTHERS THEN
        p_Result := 'LỖI: ' || SQLERRM;
END;