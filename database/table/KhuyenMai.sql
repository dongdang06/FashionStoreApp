CREATE TABLE KHUYENMAI (
    MaKM VARCHAR2(20) PRIMARY KEY, 
    TenKM NVARCHAR2(200) NOT NULL, 
    NgayBatDau DATE,
    NgayKetThuc DATE,
    MucGiamToiDa NUMBER(15,0),
    TrangThaiKM VARCHAR2(20) CHECK (TrangThaiKM IN ('Dang dien ra', 'Ket thuc')) 
);