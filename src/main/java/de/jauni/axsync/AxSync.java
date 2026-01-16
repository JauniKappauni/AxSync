package de.jauni.axsync;

import de.jauni.axsync.listener.PlayerJoinListener;
import de.jauni.axsync.listener.PlayerQuitListener;
import de.jauni.axsync.manager.DatabaseManager;
import de.jauni.axsync.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxSync extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    PlayerManager playerManager;
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            playerManager = new PlayerManager(this);
            if(databaseManager.initDatabaseTables() == false){
                getLogger().severe("Error creating playerdata table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
