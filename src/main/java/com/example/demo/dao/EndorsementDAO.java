package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Endorsement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Endorsement entity
 */
public class EndorsementDAO {
    private final DatabaseManager dbManager;

    public EndorsementDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Endorsement endorsement) {
        String sql = "INSERT INTO Endorsement (productID, endorserName, endorsementType, startDate, endDate, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, endorsement.getProductID());
            stmt.setString(2, endorsement.getEndorserName());
            stmt.setString(3, endorsement.getEndorsementType());

            if (endorsement.getStartDate() != null) {
                stmt.setDate(4, Date.valueOf(endorsement.getStartDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            if (endorsement.getEndDate() != null) {
                stmt.setDate(5, Date.valueOf(endorsement.getEndDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, endorsement.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        endorsement.setEndorsementID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Endorsement> getByProduct(int productID) {
        List<Endorsement> endorsements = new ArrayList<>();
        String sql = "SELECT * FROM Endorsement WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                endorsements.add(extractEndorsementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return endorsements;
    }

    private Endorsement extractEndorsementFromResultSet(ResultSet rs) throws SQLException {
        Endorsement endorsement = new Endorsement();
        endorsement.setEndorsementID(rs.getInt("endorsementID"));
        endorsement.setProductID(rs.getInt("productID"));
        endorsement.setEndorserName(rs.getString("endorserName"));
        endorsement.setEndorsementType(rs.getString("endorsementType"));

        Date startDate = rs.getDate("startDate");
        if (startDate != null) {
            endorsement.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("endDate");
        if (endDate != null) {
            endorsement.setEndDate(endDate.toLocalDate());
        }

        endorsement.setDescription(rs.getString("description"));

        return endorsement;
    }

    public List<Endorsement> getAll() {
        List<Endorsement> endorsements = new ArrayList<>();
        String sql = "SELECT * FROM Endorsement ORDER BY startDate DESC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                endorsements.add(extractEndorsementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return endorsements;
    }
}
