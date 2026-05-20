DECLARE
    PROCEDURE recreate_sequence(
        p_sequence_name IN VARCHAR2,
        p_table_name    IN VARCHAR2,
        p_column_name   IN VARCHAR2,
        p_prefix        IN VARCHAR2
    ) IS
        v_start_with NUMBER;
        v_sql        VARCHAR2(4000);
    BEGIN
        v_sql := 'SELECT NVL(MAX(TO_NUMBER(SUBSTR(' || p_column_name || ', ' ||
                 (LENGTH(p_prefix) + 1) || '))), 0) + 1 FROM ' || p_table_name ||
                 ' WHERE REGEXP_LIKE(' || p_column_name || ', ''^' || p_prefix || '[0-9]+$'')';

        EXECUTE IMMEDIATE v_sql INTO v_start_with;

        BEGIN
            EXECUTE IMMEDIATE 'DROP SEQUENCE ' || p_sequence_name;
        EXCEPTION
            WHEN OTHERS THEN
                IF SQLCODE != -2289 THEN
                    RAISE;
                END IF;
        END;

        EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || p_sequence_name ||
                          ' START WITH ' || v_start_with ||
                          ' INCREMENT BY 1 NOCACHE NOCYCLE';
    END;
BEGIN
    recreate_sequence('SEQ_NHANVIEN', 'NHANVIEN', 'MaNV', 'NV');
    recreate_sequence('SEQ_NHACUNGCAP', 'NHACUNGCAP', 'MaNCC', 'NCC');
    recreate_sequence('SEQ_SANPHAM', 'SANPHAM', 'MaSP', 'SP');
    recreate_sequence('SEQ_DANHMUC', 'DANHMUC', 'MaDM', 'DM');
    recreate_sequence('SEQ_KHUYENMAI', 'KHUYENMAI', 'MaKM', 'KM');
    recreate_sequence('SEQ_BIENTHESANPHAM', 'BIENTHESANPHAM', 'MaBienThe', 'BT');
    recreate_sequence('SEQ_HOADON', 'HOADON', 'MaHD', 'HD');
    recreate_sequence('SEQ_DONHANG', 'DONHANG', 'MaDH', 'DH');
    recreate_sequence('SEQ_PHIEUNHAP', 'PHIEUNHAP', 'MaPN', 'PN');
    recreate_sequence('SEQ_PHIEUXUATTRA', 'PHIEUXUATTRA', 'MaPhieuTra', 'PT');
    recreate_sequence('SEQ_KHACHHANG', 'KHACHHANG', 'MaKH', 'KH');
END;
/
