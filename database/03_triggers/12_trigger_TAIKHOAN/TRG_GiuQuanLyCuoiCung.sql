CREATE OR REPLACE TRIGGER TRG_GiuQuanLyCuoiCung
AFTER UPDATE OF VaiTro, TrangThai OR DELETE ON TAIKHOAN
DECLARE
    v_SoQuanLyHoatDong NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_SoQuanLyHoatDong
    FROM TAIKHOAN
    WHERE VaiTro = 'Quan ly'
      AND TrangThai = 'Hoat dong';

    IF v_SoQuanLyHoatDong = 0 THEN
        RAISE_APPLICATION_ERROR(
            -20021,
            'Loi: He thong phai co it nhat mot tai khoan Quan ly dang hoat dong!'
        );
    END IF;
END;