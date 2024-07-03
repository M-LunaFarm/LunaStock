package kr.lunaf.lunastock.utils;

import kr.lunaf.lunastock.LunaStock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseUtils {
    private Connection connection;

    public DatabaseUtils(LunaStock plugin) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/stocks.db");
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createPlayerStocksTable = "CREATE TABLE IF NOT EXISTS player_stocks (" +
                "player_uuid TEXT NOT NULL, " +
                "stock_uuid TEXT NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "PRIMARY KEY(player_uuid, stock_uuid))";

        String createStockChangesTable = "CREATE TABLE IF NOT EXISTS stock_changes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "stock_uuid TEXT NOT NULL, " +
                "change REAL NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerStocksTable);
            stmt.execute(createStockChangesTable);
        }
    }

    public void addPlayerStock(UUID playerUUID, UUID stockUUID, int amount) {
        String insertSQL = "INSERT INTO player_stocks(player_uuid, stock_uuid, amount) VALUES(?,?,?) " +
                "ON CONFLICT(player_uuid, stock_uuid) DO UPDATE SET amount = amount + ?";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, stockUUID.toString());
            pstmt.setInt(3, amount);
            pstmt.setInt(4, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerStock(UUID playerUUID, UUID stockUUID, int amount) {
        String updateSQL = "UPDATE player_stocks SET amount = amount - ? WHERE player_uuid = ? AND stock_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, playerUUID.toString());
            pstmt.setString(3, stockUUID.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerStock(UUID playerUUID, UUID stockUUID) {
        String querySQL = "SELECT amount FROM player_stocks WHERE player_uuid = ? AND stock_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, stockUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalStock(UUID stockUUID) {
        String querySQL = "SELECT SUM(amount) AS total FROM player_stocks WHERE stock_uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, stockUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addStockChange(UUID stockUUID, double change) {
        String insertSQL = "INSERT INTO stock_changes(stock_uuid, change) VALUES(?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, stockUUID.toString());
            pstmt.setDouble(2, change);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 최근 50개의 기록만 유지
        String deleteOldRecordsSQL = "DELETE FROM stock_changes WHERE id NOT IN (SELECT id FROM stock_changes WHERE stock_uuid = ? ORDER BY timestamp DESC LIMIT 50)";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteOldRecordsSQL)) {
            pstmt.setString(1, stockUUID.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getRecentStockChanges(UUID stockUUID, int limit) {
        List<String> changes = new ArrayList<>();
        String querySQL = "SELECT change, timestamp FROM stock_changes WHERE stock_uuid = ? ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, stockUUID.toString());
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                changes.add(rs.getString("timestamp") + ": " + rs.getDouble("change"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changes;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
