/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DTO.ServiceDTO;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    private ServiceDTO mapRow(ResultSet rs) throws SQLException {
        return new ServiceDTO(
                rs.getInt("service_id"),
                rs.getString("name"),
                rs.getString("unit"),
                rs.getDouble("unit_price"),
                rs.getString("charge_type"),
                rs.getBoolean("is_active")
        );
    }

    public List<ServiceDTO> getAll() {
        String sql = "SELECT service_id, name, unit, unit_price, charge_type, is_active FROM Service ORDER BY service_id";
        List<ServiceDTO> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ServiceDTO getById(int id) {
        String sql = "SELECT service_id, name, unit, unit_price, charge_type, is_active FROM Service WHERE service_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<ServiceDTO> searchByName(String keyword) {
        String sql = "SELECT service_id, name, unit, unit_price, charge_type, is_active " +
                     "FROM Service WHERE name LIKE ? ORDER BY name";
        List<ServiceDTO> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int insert(ServiceDTO s) {
        String sql = "INSERT INTO Service (name, unit, unit_price, charge_type, is_active) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getUnit());
            ps.setDouble(3, s.getUnitPrice());
            ps.setString(4, s.getChargeType());
            ps.setBoolean(5, s.isActive());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public boolean update(ServiceDTO s) {
        String sql = "UPDATE Service SET name=?, unit=?, unit_price=?, charge_type=?, is_active=? WHERE service_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getUnit());
            ps.setDouble(3, s.getUnitPrice());
            ps.setString(4, s.getChargeType());
            ps.setBoolean(5, s.isActive());
            ps.setInt(6, s.getServiceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Service WHERE service_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean setActive(int id, boolean active) {
        String sql = "UPDATE Service SET is_active=? WHERE service_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}

