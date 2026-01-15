package de.jauni.axsync;

import de.jauni.axsync.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxSync extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try{
            databaseManager = new DatabaseManager(this);
            if(databaseManager.initDatabaseTables() == false){
                getLogger().severe("Error creating playerData table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
