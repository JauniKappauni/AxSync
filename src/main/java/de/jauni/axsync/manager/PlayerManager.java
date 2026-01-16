package de.jauni.axsync.manager;

import de.jauni.axsync.AxSync;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerManager {
    AxSync reference;
    public PlayerManager(AxSync reference){
        this.reference = reference;
    }

    public void setPlayerHealth(Player p){
        try(Connection conn = reference.getDatabaseManager().getConnection()){
            PreparedStatement ps = conn.prepareStatement("UPDATE playerdata SET health = ? WHERE uuid = ?");
            ps.setDouble(1, p.getHealth());
            ps.setString(2, p.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
