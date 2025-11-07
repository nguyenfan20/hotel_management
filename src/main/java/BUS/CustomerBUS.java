package BUS;

import java.util.ArrayList;
import java.util.List;

import DAO.CustomerDAO;
import DTO.CustomerDTO;
import util.DatabaseConnection;

public class CustomerBUS {
    private CustomerDAO customerDAO;

    public CustomerBUS() {
        this.customerDAO = new CustomerDAO();
    }

    // Lấy tất cả khách hàng
    public List<CustomerDTO> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    // Lấy khách hàng theo ID
    public CustomerDTO getCustomerById(int customerId) {
        if (customerId <= 0) {
            System.err.println("ID khách hàng không hợp lệ");
            return null;
        }
        return customerDAO.getCustomerById(customerId);
    }

    // Thêm khách hàng mới
    public boolean addCustomer(CustomerDTO customer) {
        if (!validateCustomer(customer, true)) {
            return false;
        }

        // Kiểm tra trùng CMND/CCCD
        if (isIdCardExists(customer.getId_card(), 0)) {
            System.err.println("Số CMND/CCCD đã tồn tại!");
            return false;
        }

        // Kiểm tra trùng email (nếu có)
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (isEmailExists(customer.getEmail(), 0)) {
                System.err.println("Email đã được sử dụng!");
                return false;
            }
        }

        // Kiểm tra trùng số điện thoại
        if (customer.getPhone() != null && isPhoneExists(customer.getPhone(), 0)) {
            System.err.println("Số điện thoại đã được sử dụng!");
            return false;
        }

        return customerDAO.addCustomer(customer);
    }

    // Cập nhật khách hàng
    public boolean updateCustomer(CustomerDTO customer) {
        if (customer.getCustomer_id() <= 0) {
            System.err.println("ID khách hàng không hợp lệ khi cập nhật");
            return false;
        }

        if (!validateCustomer(customer, false)) {
            return false;
        }

        // Kiểm tra trùng CMND (trừ chính nó)
        if (isIdCardExists(customer.getId_card(), customer.getCustomer_id())) {
            System.err.println("Số CMND/CCCD đã tồn tại!");
            return false;
        }

        // Kiểm tra trùng email
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (isEmailExists(customer.getEmail(), customer.getCustomer_id())) {
                System.err.println("Email đã được sử dụng bởi khách hàng khác!");
                return false;
            }
        }

        // Kiểm tra trùng phone
        if (customer.getPhone() != null && isPhoneExists(customer.getPhone(), customer.getCustomer_id())) {
            System.err.println("Số điện thoại đã được sử dụng bởi khách hàng khác!");
            return false;
        }

        return customerDAO.updateCustomer(customer);
    }

    // Xóa khách hàng
    public boolean deleteCustomer(int customerId) {
        if (customerId <= 0) {
            System.err.println("ID khách hàng không hợp lệ khi xóa");
            return false;
        }

        // Kiểm tra ràng buộc: nếu khách hàng có booking → không cho xóa
        if (hasBookings(customerId)) {
            System.err.println("Không thể xóa khách hàng vì đã có lịch đặt phòng!");
            return false;
        }

        return customerDAO.deleteCustomer(customerId);
    }

    // Tìm kiếm khách hàng
    public List<CustomerDTO> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.searchCustomers(keyword.trim());
    }
    // === LẤY DANH SÁCH QUỐC TỊCH DUY NHẤT ===
public List<String> getAllNationalities() {
    List<String> nationalities = new ArrayList<>();
    String sql = "SELECT DISTINCT nationality FROM Customer WHERE is_hide = 0 AND nationality IS NOT NULL ORDER BY nationality";

    try (var conn = DatabaseConnection.getConnection();
         var stmt = conn.createStatement();
         var rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            String nat = rs.getString("nationality").trim();
            if (!nat.isEmpty()) {
                nationalities.add(nat);
            }
        }
    } catch (Exception e) {
        System.err.println("Lỗi lấy danh sách quốc tịch: " + e.getMessage());
    }

    return nationalities;
}

    // Lọc theo quốc tịch
    public List<CustomerDTO> filterByNationality(String nationality) {
        if (nationality == null || nationality.trim().isEmpty() || nationality.equals("Tất cả")) {
            return getAllCustomers();
        }
        return customerDAO.filterByNationality(nationality);
    }

    // === Kiểm tra trùng lặp ===

    public boolean isIdCardExists(String idCard, int excludeId) {
        if (idCard == null || idCard.trim().isEmpty()) return false;
        List<CustomerDTO> all = getAllCustomers();
        for (CustomerDTO c : all) {
            if (c.getId_card() != null && c.getId_card().equals(idCard) && c.getCustomer_id() != excludeId) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailExists(String email, int excludeId) {
        if (email == null || email.trim().isEmpty()) return false;
        List<CustomerDTO> all = getAllCustomers();
        for (CustomerDTO c : all) {
            if (c.getEmail() != null && c.getEmail().equalsIgnoreCase(email) && c.getCustomer_id() != excludeId) {
                return true;
            }
        }
        return false;
    }

    public boolean isPhoneExists(String phone, int excludeId) {
        if (phone == null) return false;
        List<CustomerDTO> all = getAllCustomers();
        for (CustomerDTO c : all) {
            if (c.getPhone().equals(phone) && c.getCustomer_id() != excludeId) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra khách hàng có booking chưa
    private boolean hasBookings(int customerId) {
        String sql = "SELECT COUNT(*) FROM Booking WHERE customer_id = ?";
        try (var conn = util.DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra booking: " + e.getMessage());
        }
        return false;
    }

    public CustomerDTO getCustomerByPhone(String phone) {
        if (phone == null) {
            System.err.println("Số điện thoại không hợp lệ");
            return null;
        }
        List<CustomerDTO> all = getAllCustomers();
        for (CustomerDTO c : all) {
            if (c.getPhone().equals(phone)) {
                return c;
            }
        }
        return null;
    }

    // === Validate dữ liệu đầu vào ===

    private boolean validateCustomer(CustomerDTO customer, boolean isAdd) {
        if (customer == null) {
            System.err.println("Dữ liệu khách hàng không được null");
            return false;
        }

        if (customer.getFull_name() == null || customer.getFull_name().trim().isEmpty()) {
            System.err.println("Họ tên không được để trống");
            return false;
        }

        if (customer.getPhone() == null || String.valueOf(customer.getPhone()).length() < 10) {
            System.err.println("Số điện thoại phải có ít nhất 10 chữ số");
            return false;
        }

        if (customer.getId_card() == null || customer.getId_card().trim().isEmpty()) {
            System.err.println("CMND/CCCD không được để trống");
            return false;
        }

        if (customer.getId_card().length() < 9 || customer.getId_card().length() > 12) {
            System.err.println("CMND/CCCD phải từ 9-12 ký tự");
            return false;
        }

        if (customer.getNationality() == null || customer.getNationality().trim().isEmpty()) {
            System.err.println("Quốc tịch không được để trống");
            return false;
        }

        if (customer.getDob() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.YEAR, -150);
            if (customer.getDob().before(cal.getTime())) {
                System.err.println("Ngày sinh không hợp lệ (quá xa)");
                return false;
            }
            cal.setTime(customer.getDob());
            cal.add(java.util.Calendar.YEAR, -16);
            if (cal.getTime().after(new java.util.Date())) {
                System.err.println("Khách hàng phải trên 16 tuổi");
                return false;
            }
        }

        if (customer.getGender() == null || !customer.getGender().matches("Male|Female|Other")) {
            System.err.println("Giới tính phải là Male, Female hoặc Other");
            return false;
        }

        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (!customer.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                System.err.println("Email không đúng định dạng");
                return false;
            }
        }

        return true;
    }
}