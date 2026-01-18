package de.jauni.axsync.listener;

import de.jauni.axsync.AxSync;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
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
                PreparedStatement fillTable = conn.prepareStatement("INSERT INTO playerdata (uuid, name, health, foodlevel, gamemode, saturation, level, progress, airlevel, inventory, enderchest) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                fillTable.setString(1, p.getUniqueId().toString());
                fillTable.setString(2, p.getName());
                fillTable.setDouble(3, p.getHealth());
                fillTable.setInt(4, p.getFoodLevel());
                fillTable.setString(5, p.getGameMode().toString());
                fillTable.setFloat(6, p.getSaturation());
                fillTable.setInt(7, p.getLevel());
                fillTable.setFloat(8, p.getExp());
                fillTable.setInt(9, p.getRemainingAir());
                fillTable.setString(10, reference.getPlayerManager().serializePlayerInventory(p.getInventory()));
                fillTable.setString(11, reference.getPlayerManager().serializeInventory(p.getEnderChest()));
                fillTable.executeUpdate();
                p.setHealth(20.0);
                p.sendMessage("Spielerdaten wurden erstellt");
            }
            else{
                double health = rs.getDouble("health");
                int foodLevel = rs.getInt("foodlevel");
                String gameMode = rs.getString("gamemode");
                float saturation = rs.getFloat("saturation");
                int level = rs.getInt("level");
                int progress = rs.getInt("progress");
                int airLevel = rs.getInt("airlevel");
                p.setHealth(health);
                p.setFoodLevel(foodLevel);
                p.setGameMode(GameMode.valueOf(gameMode));
                p.setSaturation(saturation);
                p.setLevel(level);
                p.setExp(progress);
                p.setRemainingAir(airLevel);
                reference.getPlayerManager().loadPlayerInventory(p);
                reference.getPlayerManager().loadPlayerEnderchest(p);
                p.sendMessage("Deine Gesundheit + Sättigung + Spielmodus + Erfahrung + Luftniveau wurden geladen.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
