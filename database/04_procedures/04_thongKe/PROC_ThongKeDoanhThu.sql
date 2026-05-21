CREATE OR REPLACE PROCEDURE PROC_ThongKeDoanhThu (
    p_TuNgay IN DATE,
    p_DenNgay IN DATE,
    p_LoaiThongKe IN VARCHAR2, -- 'Theo ngày' hoac 'Theo sản phẩm'
    p_Cursor OUT SYS_REFCURSOR
) AS
BEGIN
    IF p_LoaiThongKe = 'Theo sản phẩm' THEN
        OPEN p_Cursor FOR
            SELECT
                sp.TenSP AS TieuChi,
                SUM(ctdh.SoLuong) AS SoLuong,
                SUM(ctdh.SoLuong * ctdh.GiaBanLucMua) AS TongDoanhThu,
                SUM(ctdh.SoLuong * NVL(ctpn.GiaNhap, ctdh.GiaBanLucMua * 0.6)) AS TongChiPhi
            FROM HOADON hd
            JOIN CHITIETDONHANG ctdh ON hd.MaDH = ctdh.MaDH
            JOIN BIENTHESANPHAM bt ON ctdh.MaBienThe = bt.MaBienThe
            JOIN SANPHAM sp ON bt.MaSP = sp.MaSP
            LEFT JOIN (
                SELECT MaBienThe, AVG(GiaNhap) AS GiaNhap
                FROM CHITIETPHIEUNHAP
                GROUP BY MaBienThe
            ) ctpn ON ctdh.MaBienThe = ctpn.MaBienThe
            WHERE hd.NgayXuat >= p_TuNgay AND hd.NgayXuat <= p_DenNgay
            GROUP BY sp.TenSP
            ORDER BY TongDoanhThu DESC;
    ELSE
        -- Mac dinh: Theo ngay
        OPEN p_Cursor FOR
            SELECT
                TO_CHAR(hd.NgayXuat, 'dd/MM/yyyy') AS TieuChi,
                COUNT(hd.MaHD) AS SoLuong,
                SUM(hd.TongTienHD) AS TongDoanhThu,
                SUM(cost.TotalCost) AS TongChiPhi
            FROM HOADON hd
            JOIN (
                SELECT ctdh.MaDH, SUM(ctdh.SoLuong * NVL(ctpn.GiaNhap, ctdh.GiaBanLucMua * 0.6)) AS TotalCost
                FROM CHITIETDONHANG ctdh
                LEFT JOIN (
                    SELECT MaBienThe, AVG(GiaNhap) AS GiaNhap
                    FROM CHITIETPHIEUNHAP
                    GROUP BY MaBienThe
                ) ctpn ON ctdh.MaBienThe = ctpn.MaBienThe
                GROUP BY ctdh.MaDH
            ) cost ON hd.MaDH = cost.MaDH
            WHERE hd.NgayXuat >= p_TuNgay AND hd.NgayXuat <= p_DenNgay
            GROUP BY TO_CHAR(hd.NgayXuat, 'dd/MM/yyyy'), TRUNC(hd.NgayXuat)
            ORDER BY TRUNC(hd.NgayXuat);
    END IF;
END;
/