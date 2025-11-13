# Hotel Management (Java Swing)

Quản lý khách sạn – Ứng dụng desktop viết bằng **Java Swing** (thiết kế form với NetBeans), đóng gói bằng **Maven**. Mục tiêu: hỗ trợ quy trình nghiệp vụ khách sạn như đặt phòng, nhận/trả phòng, quản lý khách/nhân viên, hoá đơn – báo cáo.

> Giấy phép: **MIT**. Ngôn ngữ: **Java**. Cấu trúc dự án chuẩn Maven với `src/main` và `pom.xml`. Repo có sử dụng thư viện **NetBeans AbsoluteLayout** cho UI.  
## ✨ Tính năng chính
> Ghi chú: Bật/tắt các checkbox phù hợp với tình trạng dự án hiện tại của bạn.

- [X] **Quản lý phòng:** tạo loại phòng, giá, tình trạng; tìm kiếm & lọc.
- [X] **Đặt phòng (Booking):** đặt theo ngày, cọc/giữ phòng, xử lý trùng lịch.
- [X] **Nhận/Trả phòng:** check-in, check-out, tính tiền theo ngày/giờ/phụ phí.
- [X] **Quản lý khách hàng:** thông tin, lịch sử lưu trú, hoá đơn liên quan.
- [X] **Quản lý nhân viên & phân quyền:** vai trò, đăng nhập, nhật ký hoạt động.
- [X] **Hoá đơn & thanh toán:** tạo hoá đơn, giảm giá, thuế, in/xuất PDF.
- [X] **Báo cáo – thống kê:** doanh thu theo ngày/tháng/quý, công suất phòng, biểu đồ.
- [ ] **Cấu hình hệ thống:** đơn vị tiền tệ, thuế suất, chính sách phụ thu, sao lưu dữ liệu.

## 🧱 Kiến trúc & công nghệ

- **Ngôn ngữ:** Java (ứng dụng desktop Swing).  
- **UI:** NetBeans GUI Builder (AbsoluteLayout).  
- **Build:** Maven (`pom.xml`).  
- **CSDL:** SQL Server
- **Mô hình lớp gợi ý:** `DTO` (data transfer) / `DAO` (truy cập DB) / `BUS` (xử lý nghiệp vụ) / `GUI` (giao diện) / `utils`.

## 📁 Cấu trúc thư mục (tham khảo)

```
hotel_management/
├─ pom.xml
├─ README.md
├─ LICENSE
├─ .gitignore
├─ lib/
│  └─ .../AbsoluteLayout/SNAPSHOT/          # Thư viện layout của NetBeans (UI)
├─ src/
│  └─ main/
│     ├─ java/
│     │  ├─ GUI/                            # Form Swing (JFrame/JPanel)
│     │  ├─ DAO/                            # Data Access Objects
│     │  ├─ DTO/                            # Data Transfer Objects
│     │  ├─ BUS/                            # Nghiệp vụ (validate/tính giá/...)
│     │  └─ utils/                          # Helper: Date, Money, PDF, ...
│     └─ resources/
│        ├─ application.properties          # Cấu hình DB, i18n, ...
│        └─ images/                         # Logo
│        └─ icon/                           # Icon
└─ target/
   └─ ...                                   # Output Maven build (JAR)
```

## 🚀 Chạy thử nhanh

### Yêu cầu môi trường
- **Java JDK** 17+ (khuyến nghị)  
- **Maven** 3.8+  

### 1) Clone & build
```bash
git clone https://github.com/nguyenfan20/hotel_management.git
cd hotel_management
mvn clean package
```

### 2) Chạy ứng dụng
```bash
java -jar target/hotel_management-1.0-SNAPSHOT.jar
```

### 3) Cấu hình CSDL
**SQL Server**
```properties
db.server.name=localhost
db.server.port=1433
db.database.name=hotel_management
....

## 🧪 Dữ liệu mẫu & tài khoản demo
- Tài khoản: `admin / admin123` _(bổ sung nếu có)_
- Dữ liệu mẫu: `resources/sample/` _(bổ sung nếu có)_

## 🖼️ Ảnh chụp màn hình (Screenshots)

| Dashboard | Quản lý phòng | Đặt phòng |
|---|---|---|
| ![dashboard](resources/images/screenshots/dashboard.png) | ![rooms](resources/images/screenshots/rooms.png) | ![booking](resources/images/screenshots/booking.png) |

## ⚙️ Cấu hình Maven/JDK (tham khảo)

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
    <source>17</source>
    <target>17</target>
  </configuration>
</plugin>
```

## 🧩 Các module/chức năng đề xuất

- **BUS:** quy tắc tính tiền, giảm giá, validate đặt phòng.
- **DAO:** CRUD cho Room, Booking, Customer, Invoice, Employee...
- **DTO:** ánh xạ 1-1 với bảng DB.
- **GUI:** tách JPanel theo màn hình.
- **utils:** MoneyUtils, DateTimeUtils, PdfUtils, ChartUtils.

## 📊 Báo cáo & biểu đồ gợi ý
- **Doanh thu** theo ngày/tháng/quý (Pie/Bar/Line).  
- **Công suất phòng** theo loại phòng/ca/suốt tuần.  
- **Top dịch vụ/phụ thu** theo doanh thu.

## 🗺️ Roadmap
- [X] Chuẩn hoá cấu trúc package
- [X] Thêm file cấu hình `application.properties`
- [X] Hoàn thiện đăng nhập + phân quyền
- [X] Thêm báo cáo doanh thu
- [X] Test unit (JUnit)
- [ ] Đóng gói phát hành (Release)

## 🤝 Đóng góp
1. Fork dự án & tạo nhánh: `feature/my-feature`  
2. Commit theo chuẩn: `feat: ...`, `fix: ...`  
3. Tạo Pull Request mô tả rõ thay đổi.

## 📄 Giấy phép
Phát hành theo giấy phép **MIT** – xem file `LICENSE`.

---

### Ghi công
- Nhóm phát triển: 
   + Phan Tài Nguyên
   + Trương Tấn Đạt
   + Đạo Hoàng Đăng
   + Dương Vũ Nghĩa
   + Nguyễn Ngọc Tài
- Công cụ: NetBeans GUI Builder (AbsoluteLayout), Maven.
