package de.jauni.axsync.listener;

import de.jauni.axsync.AxSync;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    AxSync reference;

    public PlayerJoinListener(AxSync reference) {
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player p = event.getPlayer();
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM playerdata WHERE uuid = ?");
            check.setString(1, p.getUniqueId().toString());
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                PreparedStatement fillTable = conn.prepareStatement("INSERT INTO playerdata (uuid, name, health) VALUES (?, ?, ?)");
                fillTable.setString(1, p.getUniqueId().toString());
                fillTable.setString(2, p.getName());
                fillTable.setDouble(3, p.getHealth());
                fillTable.executeUpdate();
                p.setHealth(20.0);
                p.sendMessage("Keine Daten gefunden");
            }
            double health = rs.getDouble("health");
            p.setHealth(health);
            p.sendMessage("Deine Gesundheit wurde auf" + " " + health + " " + "gesetzt.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
