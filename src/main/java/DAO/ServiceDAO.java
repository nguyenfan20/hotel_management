package DAO;

import DTO.ServiceDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class ServiceDAO {

    private static final String SELECT_ALL = "SELECT service_id, name, unit, unit_price, charge_type, is_active FROM Service ORDER BY service_id";
    private static final String SELECT_BY_ID = "SELECT service_id, name, unit, unit_price, charge_type, is_active FROM Service WHERE service_id=?";
    private static final String SEARCH_NAME = "SELECT service_id, name, unit, unit_price, charge_type, is_active FROM Service WHERE name LIKE ? ORDER BY name";
    private static final String INSERT_SQL = "INSERT INTO Service (name, unit, unit_price, charge_type, is_active) VALUES (?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE Service SET name=?, unit=?, unit_price=?, charge_type=?, is_active=? WHERE service_id=?";
    private static final String DELETE_SQL = "DELETE FROM Service WHERE service_id=?";
    private static final String SET_ACTIVE = "UPDATE Service SET is_active=? WHERE service_id=?";

    public List<ServiceDTO> getAll() { return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO); }
    public ServiceDTO getById(int id) { return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, id); }
    public List<ServiceDTO> searchByName(String kw) { return DatabaseConnection.executeQueryList(SEARCH_NAME, this::mapToDTO, "%"+kw.trim()+"%"); }

    public int insert(ServiceDTO s) {
        return DatabaseConnection.executeInsert(INSERT_SQL, s.getName(), s.getUnit(), s.getUnitPrice(), s.getChargeType(), s.isActive());
    }

    public boolean update(ServiceDTO s) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL, s.getName(), s.getUnit(), s.getUnitPrice(), s.getChargeType(), s.isActive(), s.getServiceId());
    }

    public boolean delete(int id) { return DatabaseConnection.executeUpdate(DELETE_SQL, id); }
    public boolean setActive(int id, boolean active) { return DatabaseConnection.executeUpdate(SET_ACTIVE, active, id); }

    private ServiceDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new ServiceDTO(
                rs.getInt("service_id"),
                rs.getString("name"),
                rs.getString("unit"),
                rs.getDouble("unit_price"),
                rs.getString("charge_type"),
                rs.getBoolean("is_active")
        );
    }
}