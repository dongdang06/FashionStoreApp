<p align="center">
  <a href="https://www.uit.edu.vn/" title="Trường Đại học Công nghệ Thông tin">
    <img src="https://i.imgur.com/WmMnSRt.png" alt="Trường Đại học Công nghệ Thông tin | University of Information Technology" width="60%">
  </a>
</p>

<h1 align="center"><b>IS216.Q23 - LẬP TRÌNH JAVA</b></h1>
<h3 align="center"><b>ĐỒ ÁN MÔN HỌC: HỆ THỐNG QUẢN LÝ CỬA HÀNG THỜI TRANG (FASHIONSTORE)</b></h3>

---

## BẢNG MỤC LỤC

* [Giới thiệu môn học](#giới-thiệu-môn-học)
* [Giới thiệu đồ án môn học](#giới-thiệu-đồ-án-môn-học)
* [Thành viên nhóm](#thành-viên-nhóm)
* [Cài đặt phần mềm](#cài-đặt-phần-mềm)
* [Cơ sở dữ liệu](#cơ-sở-dữ-liệu)
* [Khởi chạy dự án](#khởi-chạy-dự-án)
* [Cấu trúc thư mục](#cấu-trúc-thư-mục)
* [Công nghệ sử dụng](#công-nghệ-sử-dụng)

---

## GIỚI THIỆU MÔN HỌC

* **Tên môn học:** Lập trình Java
* **Mã môn học:** IS216.Q23
* **Năm học:** HK2 2025-2026
* **Giảng viên hướng dẫn:** ThS. Tạ Việt Phương
* **Email:** phuongtv@uit.edu.vn

---

## GIỚI THIỆU ĐỒ ÁN MÔN HỌC

* **Tên đề tài:** FashionStore - Hệ thống quản lý cửa hàng thời trang
* **Repository:** [https://github.com/dongdang06/FashionStoreApp](https://github.com/dongdang06/FashionStoreApp)

**FashionStore** là ứng dụng desktop quản lý toàn diện dành cho các cửa hàng thời trang vừa và nhỏ. Hệ thống hỗ trợ tối ưu quy trình bán hàng tại quầy (POS), kiểm soát hàng tồn kho (Nhập/Xuất kho), quản lý thông tin khách hàng thành viên, chương trình khuyến mãi, doanh thu và quản trị nhân viên. Ứng dụng được xây dựng trên nền tảng **Java Swing** hiện đại kết hợp với hệ quản trị cơ sở dữ liệu **Oracle Database** mạnh mẽ, tổ chức theo kiến trúc **3-tier MVC** (Model-View-Controller/DAO).

### Các chức năng chính:
* **Quản lý bán hàng (POS):** Tạo hóa đơn nhanh, tìm kiếm sản phẩm theo mã/tên, áp dụng mã khuyến mãi tự động, tích lũy điểm thành viên và in hóa đơn thanh toán trực quan.
* **Quản lý sản phẩm & biến thể:** Quản lý danh mục sản phẩm và các biến thể chi tiết (màu sắc, kích thước, số lượng tồn kho của từng loại).
* **Quản lý kho hàng:** 
  * *Phiếu nhập kho:* Nhập hàng từ nhà cung cấp, cập nhật giá vốn và số lượng tồn.
  * *Phiếu xuất trả:* Xuất trả hàng lỗi hoặc hết hạn/hỏng về nhà cung cấp.
* **Quản lý khuyến mãi:** Tạo và áp dụng các sự kiện giảm giá theo thời gian, chiết khấu theo hóa đơn.
* **Quản lý khách hàng:** Phân hạng khách hàng, theo dõi tích lũy điểm thưởng và lịch sử mua sắm.
* **Quản lý nhân viên & Tài khoản:** Quản lý thông tin nhân viên, cấp tài khoản và phân quyền truy cập hệ thống (Quản lý, Nhân viên bán hàng, Nhân viên kho).
* **Thống kê & Báo cáo:** Dashboard hiển thị trực quan biểu đồ doanh thu, thống kê sản phẩm bán chạy, đơn hàng thành công và báo cáo xuất nhập tồn kho.

---

## THÀNH VIÊN NHÓM

| STT | MSSV | Họ và Tên | Github | Email |
| :---: | :---: | :--- | :--- | :--- |
| 1 | 24520306 | Phạm Công Định | [Dingglebell](https://github.com/Dingglebell) | 24520306@gm.uit.edu.vn |
| 2 | 24520309 | Đặng Bá Đông | [dongdang06](https://github.com/dongdang06) | 24520309@gm.uit.edu.vn |
| 3 | 24520442 | Phạm Tuấn Hải | [haiphamt](https://github.com/haiphamt) | 24520442@gm.uit.edu.vn |
| 4 | 24520483 | Nguyễn Trọng Hiệp | [HDiup](https://github.com/HDiup) | 24520483@gm.uit.edu.vn |
| 5 | 24520677 | Nguyễn Hoàng Huy | [HuyNguyen174](https://github.com/HuyNguyen174) | 24520677@gm.uit.edu.vn |

---

## CÀI ĐẶT PHẦN MỀM

Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt đầy đủ các công cụ sau:
- [x] **Oracle Database** (Khuyến nghị bản 19c hoặc 21c Express Edition XE)
- [x] **Oracle SQL Developer** (Công cụ quản lý CSDL)
- [x] **Java Development Kit (JDK 11+)**
- [x] **Apache Netbeans IDE** hoặc **Visual Studio Code**

---

## CƠ SỞ DỮ LIỆU

Dự án sử dụng cơ sở dữ liệu **Oracle Database (Pluggable Database)**. Thực hiện các bước sau để thiết lập:

### Bước 1: Tạo Pluggable Database (PDB) & User quản trị
Khởi động **SQL Developer**, đăng nhập vào Oracle Database với tài khoản có quyền **SYSDBA** (ví dụ: `sys as sysdba`), sau đó mở một worksheet mới và thực thi các câu lệnh SQL sau:

```sql
-- Chuyển sang Container Root
ALTER SESSION SET CONTAINER = CDB$ROOT;

-- Tạo Pluggable Database FASHIONSTORE
CREATE PLUGGABLE DATABASE FASHIONSTORE
ADMIN USER fashionstore IDENTIFIED BY "123"
ROLES = (DBA)
FILE_NAME_CONVERT = ('pdbseed', 'fashionstore');

-- Mở Database FASHIONSTORE và lưu trạng thái
ALTER PLUGGABLE DATABASE FASHIONSTORE OPEN READ WRITE;
ALTER PLUGGABLE DATABASE FASHIONSTORE SAVE STATE;

-- Chuyển ngữ cảnh làm việc sang PDB vừa tạo
ALTER SESSION SET CONTAINER = FASHIONSTORE;

-- Cấp quyền không giới hạn dung lượng bộ nhớ cho user quản trị
GRANT UNLIMITED TABLESPACE TO fashionstore;
```

### Bước 2: Tạo kết nối mới trong SQL Developer
Tạo kết nối mới đến PDB `FASHIONSTORE` với các thông số cấu hình như sau:

* **Name:** `FASHIONSTORE`
* **Database Type:** `Oracle`
* **Authentication Type:** `Default`
* **Username:** `fashionstore`
* **Password:** `123` *(Chọn **Save Password**)*
* **Connection Type:** `Basic`
* **Role:** `default`
* **Hostname:** `localhost`
* **Port:** `1521`
* **Service name:** `FASHIONSTORE`

### Bước 3: Chạy script khởi tạo dữ liệu
Chọn kết nối `FASHIONSTORE` vừa tạo, mở thư mục `database/` trong mã nguồn dự án, và chạy lần lượt các file `.sql` theo đúng thứ tự thư mục từ **1 đến 7**:
1. `01_tables/` - Khởi tạo cấu trúc các bảng.
2. `02_constraints/` - Ràng buộc khóa ngoại, duy nhất.
3. `03_triggers/` - Triggers tự động hóa logic nghiệp vụ.
4. `04_procedures/` - Các thủ tục lưu trữ (Stored Procedures).
5. `05_functions/` - Các hàm tính toán (Functions).
6. `06_data/` - Nạp dữ liệu mẫu ban đầu (Data Seeding).
7. `07_sequences/` - Khởi tạo các bộ đếm tự tăng (Sequences).

---

## KHỞI CHẠY DỰ ÁN

Sử dụng một trong hai IDE sau để chạy chương trình:

### Lựa chọn 1: Sử dụng Apache Netbeans IDE
1. Mở Netbeans, chọn **File** -> **Open Project**.
2. Tìm và chọn thư mục chứa mã nguồn của chương trình (`FashionStoreApp`).
3. Ấn phím **F6** hoặc click vào nút **Run Project** (biểu tượng Play màu xanh lá) để tiến hành build Maven và chạy ứng dụng.

### Lựa chọn 2: Sử dụng Visual Studio Code (VS Code)
1. Hãy đảm bảo bạn đã cài đặt extension **Extension Pack for Java** trong VS Code.
2. Chọn **File** -> **Open Folder...** -> Chọn thư mục mã nguồn dự án `FashionStoreApp`.
3. Mở file `App.java` (nằm ở `src/main/java/com/fashionstore/App.java`).
4. Click chuột phải tại vùng soạn thảo mã nguồn file `App.java` hoặc nút chạy ở góc phải và chọn **Run Java** để khởi động chương trình.

---

## CẤU TRÚC THƯ MỤC

Dưới đây là sơ đồ cấu trúc thư mục chính của dự án:

```text
FashionStoreApp/
├── database/                   # Thư mục chứa các script thiết lập CSDL Oracle
│   ├── 01_tables/              # Định nghĩa cấu trúc bảng
│   ├── 02_constraints/         # Ràng buộc toàn vẹn dữ liệu
│   ├── 03_triggers/            # Trình kích hoạt tự động (Triggers)
│   ├── 04_procedures/          # Thủ tục lưu trữ (Stored Procedures)
│   ├── 05_functions/           # Hàm tiện ích SQL (Functions)
│   ├── 06_data/                # Dữ liệu mẫu khởi tạo (Data Seeding)
│   └── 07_sequences/           # Bộ đếm tự tăng (Sequences)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── fashionstore/
│       │           ├── App.java    # Điểm khởi chạy chính của ứng dụng
│       │           ├── model/      # Các lớp thực thể (Entities/Models)
│       │           ├── view/       # Các lớp giao diện Swing (Views)
│       │           │   ├── auth/       # Giao diện Đăng nhập / Đăng ký
│       │           │   ├── quanly/     # Giao diện dành cho vai trò Quản lý
│       │           │   ├── nvbanhang/  # Giao diện bán hàng (POS)
│       │           │   └── nvkho/      # Giao diện quản lý xuất/nhập kho
│       │           ├── controller/ # Các bộ điều khiển trung gian (Controllers)
│       │           ├── dao/        # Tầng truy xuất dữ liệu Oracle (DAOs)
│       │           └── util/       # Các lớp tiện ích (Database Connection, ...)
│       └── resources/
└── pom.xml                     # Tệp quản lý thư viện và cấu hình dự án Maven
```

---

## CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ / Thư viện sử dụng | Chi tiết |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java (JDK 11) | Ngôn ngữ phát triển cốt lõi |
| **Giao diện (GUI)** | Java Swing | Thiết kế giao diện Desktop app |
| **Giao diện Theme** | FlatLaf (Look and Feel) v3.4 | Giao diện phẳng hiện đại, hỗ trợ Dark/Light mode |
| **Lịch / DatePicker** | JCalendar v1.4 | Component chọn ngày tháng trực quan |
| **Cơ sở dữ liệu** | Oracle Database | Hệ quản trị cơ sở dữ liệu quan hệ |
| **Kết nối CSDL** | JDBC (ojdbc11 v23.3) | Thư viện kết nối Java với Oracle Database |
| **Quản lý dự án** | Maven | Quản lý vòng đời dự án và các thư viện phụ thuộc |
