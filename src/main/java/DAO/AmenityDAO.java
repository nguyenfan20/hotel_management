package DAO;

import DTO.AmenityDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AmenityDAO {

    private static final String SELECT_ALL = "SELECT * FROM Amenity WHERE is_hide = 0 ORDER BY amenity_id";
    private static final String SELECT_BY_ID = "SELECT * FROM Amenity WHERE amenity_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Amenity (name, charge_type, price, description) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Amenity SET name = ?, charge_type = ?, price = ?, description = ? WHERE amenity_id = ?";
    private static final String DELETE_SQL = "UPDATE Amenity SET is_hide = 1 WHERE amenity_id = ?";
    private static final String SEARCH_BY_NAME = "SELECT * FROM Amenity WHERE name LIKE ? AND is_hide = 0 ORDER BY amenity_id";
    private static final String FILTER_BY_CHARGE_TYPE = "SELECT * FROM Amenity WHERE charge_type = ? AND is_hide = 0 ORDER BY amenity_id";

    public List<AmenityDTO> getAllAmenities() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public AmenityDTO getAmenityById(int amenityId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, amenityId);
    }

    public boolean addAmenity(AmenityDTO amenity) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                amenity.getName(),
                amenity.getChargeType(),
                amenity.getPrice(),
                amenity.getDescription()
        );
    }

    public boolean updateAmenity(AmenityDTO amenity) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                amenity.getName(),
                amenity.getChargeType(),
                amenity.getPrice(),
                amenity.getDescription(),
                amenity.getAmenityId()
        );
    }

    public boolean deleteAmenity(int amenityId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, amenityId);
    }

    public List<AmenityDTO> searchAmenitiesByName(String keyword) {
        return DatabaseConnection.executeQueryList(SEARCH_BY_NAME, this::mapToDTO, "%" + keyword + "%");
    }

    public List<AmenityDTO> filterAmenitiesByChargeType(String chargeType) {
        return DatabaseConnection.executeQueryList(FILTER_BY_CHARGE_TYPE, this::mapToDTO, chargeType);
    }

    private AmenityDTO mapToDTO(ResultSet rs) throws SQLException {
        return new AmenityDTO(
                rs.getInt("amenity_id"),
                rs.getString("name"),
                rs.getString("charge_type"),
                rs.getBigDecimal("price"),
                rs.getString("description")
        );
    }
}