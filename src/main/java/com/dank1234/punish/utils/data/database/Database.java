package com.dank1234.punish.utils.data.database;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/";
    private final String USER = "dan";
    private final String PASSWORD = "admin";
    private final String SCHEMA = "punish_lite";
    private final String TABLE = "punishments";

    public Database() {
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            createSchema();
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSchema() throws SQLException {
        String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS " + SCHEMA;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createSchemaSQL);
        }
    }
    private void createTables() throws SQLException {
        String useSchemaSQL = "USE " + SCHEMA;
        String createUsersTableSQL = """
            CREATE TABLE IF NOT EXISTS `punish_lite`.`users` (
                `uuid`     VARCHAR(36)  NOT NULL,
                `username` VARCHAR(45)  NOT NULL,
                PRIMARY KEY (`uuid`)
            );
        """;
        String createPunishmentsTableSQL = """
            CREATE TABLE IF NOT EXISTS `punish_lite`.`punishments` (
                banId          VARCHAR(11)  NOT NULL,
                type           VARCHAR(255) NOT NULL DEFAULT 'BAN',
                player         VARCHAR(36)  NOT NULL,
                punisher       VARCHAR(36)  NOT NULL,
                reason         TEXT         NOT NULL DEFAULT 'No Reason Provided!',
                length         BIGINT       NOT NULL DEFAULT -1,
                startTime      BIGINT       NOT NULL DEFAULT 0,
                endTime        BIGINT       NOT NULL DEFAULT 9223372036854775807,
                silent         TINYINT(1)   NOT NULL DEFAULT 1,
                active         TINYINT(1)   NOT NULL DEFAULT 1,
                PRIMARY KEY (banId),
                UNIQUE INDEX banId_UNIQUE (banId ASC) VISIBLE,
                INDEX player_idx (player ASC) VISIBLE,
                INDEX punisher_idx (punisher ASC) VISIBLE,
                CONSTRAINT player FOREIGN KEY (player)
                  REFERENCES `punish_lite`.`users` (uuid)
                  ON DELETE NO ACTION
                  ON UPDATE NO ACTION,
                CONSTRAINT punisher FOREIGN KEY (punisher)
                  REFERENCES `punish_lite`.`users` (uuid)
                  ON DELETE NO ACTION
                  ON UPDATE NO ACTION
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(useSchemaSQL);
            stmt.executeUpdate(createUsersTableSQL);
            stmt.executeUpdate(createPunishmentsTableSQL);
        }
    }

    public void insertPunishment(Punishment punishment) {
        String insertSQL = "INSERT INTO " + TABLE + " (banId, type, player, punisher, reason, length, startTime, endTime, silent, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, punishment.banId());
            pstmt.setString(2, punishment.type().name());
            pstmt.setString(3, punishment.player().toString());
            pstmt.setString(4, punishment.punisher().toString());
            pstmt.setString(5, punishment.reason());
            pstmt.setLong(6, punishment.length());
            pstmt.setLong(7, punishment.startTime());
            pstmt.setLong(8, punishment.endTime());
            pstmt.setBoolean(9, punishment.silent());
            pstmt.setBoolean(10, punishment.active());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Punishment> getPunishments(UUID player) {
        List<Punishment> punishments = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE + " WHERE player = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, player.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Punishment punishment = new Punishment(
                        rs.getString("banId"),
                        Type.valueOf(rs.getString("type")),
                        UUID.fromString(rs.getString("player")),
                        UUID.fromString(rs.getString("punisher")),
                        rs.getString("reason"),
                        rs.getLong("length"),
                        rs.getLong("endTime"),
                        rs.getBoolean("silent"),
                        rs.getBoolean("active")
                );
                punishments.add(punishment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }
    public List<UUID> getPunishedPlayers(Type type) {
        List<UUID> players = new ArrayList<>();
        String query = "SELECT DISTINCT player FROM " + TABLE + " WHERE type = ? AND active = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, type.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("player")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
    public Punishment getPunishment(String banId) {
        String query = "SELECT * FROM " + TABLE + " WHERE banId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, banId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Punishment(
                        rs.getString("banId"),
                        Type.valueOf(rs.getString("type")),
                        UUID.fromString(rs.getString("player")),
                        UUID.fromString(rs.getString("punisher")),
                        rs.getString("reason"),
                        rs.getLong("length"),
                        rs.getLong("endTime"),
                        rs.getBoolean("silent"),
                        rs.getBoolean("active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Punishment getLatestPunishment(Type type, UUID player) {
        String query = "SELECT * FROM " + TABLE + " WHERE player = ? AND type = ? AND active = TRUE ORDER BY startTime DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, player.toString());
            pstmt.setString(2, type.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Punishment(
                        rs.getString("banId"),
                        Type.valueOf(rs.getString("type")),
                        UUID.fromString(rs.getString("player")),
                        UUID.fromString(rs.getString("punisher")),
                        rs.getString("reason"),
                        rs.getLong("length"),
                        rs.getLong("endTime"),
                        rs.getBoolean("silent"),
                        rs.getBoolean("active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void updatePunishment(String banId, boolean active) {
        String updateSQL = "UPDATE " + TABLE + " SET active = ? WHERE banId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setBoolean(1, active);
            pstmt.setString(2, banId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void prunePunishments(UUID player, long timeframeMillis) {
        String deleteSQL = "DELETE FROM " + TABLE + " WHERE player = ? AND startTime >= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, player.toString());
            pstmt.setLong(2, System.currentTimeMillis() - timeframeMillis);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isPlayerPunished(Type type, UUID player) {
        List<Punishment> punishments = getPunishments(player);
        long currentTime = System.currentTimeMillis();
        for (Punishment punishment : punishments) {
            if (!punishment.active()) {
                return false;
            }
            if (punishment.type() == type && (punishment.length() == -1L || (punishment.endTime() > currentTime))) {
                return true;
            }
        }
        return false;
    }

    public void insertUser(Player player) {
        String insertSQL = "INSERT INTO users (uuid, username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, player.getUniqueId().toString());
            pstmt.setString(2, player.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean userExists(UUID uuid) {
        String query = "SELECT COUNT(*) FROM users WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getNameByUUID(UUID uuid) {
        String query = "SELECT username FROM users WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public UUID getUUIDByName(String playerName) {
        String query = "SELECT uuid FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}