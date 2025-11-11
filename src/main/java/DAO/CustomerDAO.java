package DAO;

import DTO.CustomerDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDAO {

    private static final String SELECT_ALL = "SELECT * FROM Customer WHERE is_hide=0 ORDER BY customer_id";
    private static final String SELECT_BY_ID = "SELECT * FROM Customer WHERE customer_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Customer (full_name, phone, email, id_card, address, nationality, dob, gender, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Customer SET full_name = ?, phone = ?, email = ?, id_card = ?, address = ?, nationality = ?, dob = ?, gender = ?, note = ? WHERE customer_id = ?";
    private static final String DELETE_SQL = "UPDATE Customer SET is_hide = 1 WHERE customer_id = ?";
    private static final String SEARCH_SQL = "SELECT * FROM Customer WHERE is_hide = 0 AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR id_card LIKE ?) ORDER BY full_name";
    private static final String FILTER_BY_NATIONALITY = "SELECT * FROM Customer WHERE is_hide = 0 AND nationality = ? ORDER BY full_name";

    public List<CustomerDTO> getAllCustomers() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public CustomerDTO getCustomerById(int customerId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, customerId);
    }

    public boolean addCustomer(CustomerDTO customer) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                customer.getFull_name(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getId_card(),
                customer.getAddress(),
                customer.getNationality(),
                customer.getDob() != null ? new Date(customer.getDob().getTime()) : null,
                customer.getGender(),
                customer.getNote()
        );
    }

    public boolean updateCustomer(CustomerDTO customer) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                customer.getFull_name(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getId_card(),
                customer.getAddress(), // address
                customer.getNationality(),
                customer.getDob() != null ? new Date(customer.getDob().getTime()) : null,
                customer.getGender(),
                customer.getNote(),
                customer.getCustomer_id()
        );
    }

    public boolean deleteCustomer(int customerId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, customerId);
    }

    public List<CustomerDTO> searchCustomers(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern, pattern, pattern);
    }

    public List<CustomerDTO> filterByNationality(String nationality) {
        return DatabaseConnection.executeQueryList(FILTER_BY_NATIONALITY, this::mapToDTO, nationality);
    }

    private CustomerDTO mapToDTO(ResultSet rs) throws SQLException {
        CustomerDTO customer = new CustomerDTO();
        customer.setCustomer_id(rs.getInt("customer_id"));
        customer.setFull_name(rs.getString("full_name"));
        String phoneStr = rs.getString("phone");
        customer.setPhone(phoneStr != null && !phoneStr.isEmpty() ? phoneStr : null);
        customer.setEmail(rs.getString("email"));
        customer.setId_card(rs.getString("id_card"));
        customer.setAddress(rs.getString("address"));
        customer.setNationality(rs.getString("nationality"));
        Date dob = rs.getDate("dob");
        customer.setDob(dob != null ? new java.util.Date(dob.getTime()) : null);
        customer.setGender(rs.getString("gender"));
        customer.setNote(rs.getString("note"));
        return customer;
    }
}