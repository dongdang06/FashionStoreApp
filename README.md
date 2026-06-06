<p align="center">
  <a href="https://www.uit.edu.vn/" title="Trường Đại học Công nghệ Thông tin" style="border: 5;">
    <img src="https://i.imgur.com/WmMnSRt.png" alt="Trường Đại học Công nghệ Thông tin | University of Information Technology">
  </a>
</p>

<h1 align="center"><b>Hệ thống quản lý cửa hàng thời trang</b></h1>


## GIỚI THIỆU MÔN HỌC
<a name="gioithieumonhoc"></a>
* **Tên môn học**: Lập trình Java
* **Mã môn học**: IS216.Q23
* **Năm học**: 2025-2026


## GIẢNG VIÊN HƯỚNG DẪN
<a name="giangvien"></a>
* ThS. **Tạ Việt Phương** - *phuongtv@uit.edu.vn*


## THÀNH VIÊN NHÓM
<a name="thanhvien"></a>
| STT    | MSSV          | Họ và Tên              | Github                                               | Email                   |
| ------ |:-------------:| ----------------------:|-----------------------------------------------------:|-------------------------:
| 1      | 24520306      | Phạm Công Định         |                                                      |24520306@gm.uit.edu.vn   |
| 2      | 24520309      | Đặng Bá Đông           |                                                      |24520309@gm.uit.edu.vn   |
| 3      | 24520442      | Phạm Tuấn Hải          |                                                      |24520442@gm.uit.edu.vn   |
| 4      | 24520483      | Nguyễn Trọng Hiệp      |                                                      |24520483@gm.uit.edu.vn   |
| 5      | 24520677      | Nguyễn Hoàng Huy       |                                                      |24520677@gm.uit.edu.vn   |

## SEMINAR
<a name="seminar"></a>



## ĐỒ ÁN MÔN HỌC
<a name="doan"></a>

---

## [cite_start]HƯỚNG DẪN CÀI ĐẶT CHƯƠNG TRÌNH [cite: 1]

[cite_start]Cần cài đặt trước các chương trình sau: [cite: 2]
* [cite_start]Oracle Database [cite: 3]
* [cite_start]SQL Developer [cite: 4]
* [cite_start]JDK (Java Development Kit) [cite: 5]
* [cite_start]Apache Netbeans IDE hoặc Visual Studio Code [cite: 6]

### [cite_start]Bước 1: Sử dụng SQL Developer, đăng nhập vào Oracle Database với tài khoản có quyền SYSDBA, sau đó thực hiện các lệnh sau: [cite: 7]
```sql
ALTER SESSION SET CONTAINER = CDB$ROOT; [cite_start]-- [cite: 8]
[cite_start]CREATE PLUGGABLE DATABASE FASHIONSTORE -- [cite: 9]
[cite_start]ADMIN USER fashionstore IDENTIFIED BY "123" -- [cite: 10]
[cite_start]ROLES = (DBA) -- [cite: 11]
FILE_NAME_CONVERT = ('pdbseed', 'fashionstore'); [cite_start]-- [cite: 12]
ALTER PLUGGABLE DATABASE FASHIONSTORE OPEN READ WRITE; [cite_start]-- [cite: 13]
ALTER PLUGGABLE DATABASE FASHIONSTORE SAVE STATE; [cite_start]-- [cite: 14]
ALTER SESSION SET CONTAINER = FASHIONSTORE; [cite_start]-- [cite: 15]
GRANT UNLIMITED TABLESPACE TO fashionstore; [cite_start]-- [cite: 16]
ALTER SESSION SET CONTAINER = CDB$ROOT; [cite_start]-- [cite: 17]
[cite_start]CREATE PLUGGABLE DATABASE FASHIONSTORE -- [cite: 18]
[cite_start]ADMIN USER fashionstore IDENTIFIED BY "123" -- [cite: 19]
[cite_start]ROLES = (DBA) -- [cite: 20]
FILE_NAME_CONVERT = ('pdbseed', 'fashionstore'); [cite_start]-- [cite: 21]
ALTER PLUGGABLE DATABASE FASHIONSTORE OPEN READ WRITE; [cite_start]-- [cite: 22]
ALTER PLUGGABLE DATABASE FASHIONSTORE SAVE STATE; [cite_start]-- [cite: 23]
ALTER SESSION SET CONTAINER = FASHIONSTORE; [cite_start]-- [cite: 24]
GRANT UNLIMITED TABLESPACE TO fashionstore; [cite_start]-- [cite: 25]
