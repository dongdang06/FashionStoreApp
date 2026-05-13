CREATE TABLE NHANVIEN (
    MaNV VARCHAR2(10) PRIMARY KEY, -- Khóa chính [cite: 6]
    HoTen NVARCHAR2(100) NOT NULL, -- Not Null [cite: 6]
    Email VARCHAR2(100) UNIQUE, -- Unique [cite: 6]
    SDT VARCHAR2(15) NOT NULL UNIQUE, -- Not Null, Unique [cite: 6]
    TrangThaiLamViec NVARCHAR2(20) CHECK (TrangThaiLamViec IN ('Dang lam viec', 'Da nghi viec')) -- Miền giá trị [cite: 6]
);