package DAO;

import DTO.AmenityDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAO {

    // Lấy tất cả tiện nghi
    public List<AmenityDTO> getAllAmenities() {
        List<AmenityDTO> amenities = new ArrayList<>();
        String sql = "SELECT * FROM Amenity ORDER BY amenity_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AmenityDTO amenity = new AmenityDTO(
                        rs.getInt("amenity_id"),
                        rs.getString("name"),
                        rs.getString("charge_type"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                );
                amenities.add(amenity);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tiện nghi: " + e.getMessage());
            e.printStackTrace();
        }

        return amenities;
    }

    // Lấy tiện nghi theo ID
    public AmenityDTO getAmenityById(int amenityId) {
        String sql = "SELECT * FROM Amenity WHERE amenity_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amenityId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new AmenityDTO(
                        rs.getInt("amenity_id"),
                        rs.getString("name"),
                        rs.getString("charge_type"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tiện nghi: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm tiện nghi mới
    public boolean addAmenity(AmenityDTO amenity) {
        String sql = "INSERT INTO Amenity (name, charge_type, price, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, amenity.getName());
            pstmt.setString(2, amenity.getChargeType());
            pstmt.setBigDecimal(3, amenity.getPrice());
            pstmt.setString(4, amenity.getDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm tiện nghi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật tiện nghi
    public boolean updateAmenity(AmenityDTO amenity) {
        String sql = "UPDATE Amenity SET name = ?, charge_type = ?, price = ?, description = ? WHERE amenity_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, amenity.getName());
            pstmt.setString(2, amenity.getChargeType());
            pstmt.setBigDecimal(3, amenity.getPrice());
            pstmt.setString(4, amenity.getDescription());
            pstmt.setInt(5, amenity.getAmenityId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tiện nghi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa tiện nghi
    public boolean deleteAmenity(int amenityId) {
        String sql = "DELETE FROM Amenity WHERE amenity_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amenityId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tiện nghi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm tiện nghi theo tên
    public List<AmenityDTO> searchAmenitiesByName(String keyword) {
        List<AmenityDTO> amenities = new ArrayList<>();
        String sql = "SELECT * FROM Amenity WHERE name LIKE ? ORDER BY amenity_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AmenityDTO amenity = new AmenityDTO(
                        rs.getInt("amenity_id"),
                        rs.getString("name"),
                        rs.getString("charge_type"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                );
                amenities.add(amenity);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm tiện nghi: " + e.getMessage());
            e.printStackTrace();
        }

        return amenities;
    }

    // Lọc tiện nghi theo loại phí
    public List<AmenityDTO> filterAmenitiesByChargeType(String chargeType) {
        List<AmenityDTO> amenities = new ArrayList<>();
        String sql = "SELECT * FROM Amenity WHERE charge_type = ? ORDER BY amenity_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chargeType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AmenityDTO amenity = new AmenityDTO(
                        rs.getInt("amenity_id"),
                        rs.getString("name"),
                        rs.getString("charge_type"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                );
                amenities.add(amenity);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc tiện nghi: " + e.getMessage());
            e.printStackTrace();
        }

        return amenities;
    }
}
