package kr.lunaf.lunastock;

import kr.lunaf.lunastock.API.LunaStockAPI;
import kr.lunaf.lunastock.commands.StockCommand;

import kr.lunaf.lunastock.tasks.NewsEventTask;
import kr.lunaf.lunastock.tasks.StockUpdateTask;
import kr.lunaf.lunastock.utils.DatabaseUtils;
import kr.lunaf.lunastock.utils.EconomyUtils;
import kr.lunaf.lunastock.utils.StockUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class LunaStock extends JavaPlugin {
    private StockUtils stockUtils;
    private DatabaseUtils databaseUtils;
    private EconomyUtils economyUtils;
    private LunaStockAPI LunaStockAPI;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        stockUtils = new StockUtils(this);
        databaseUtils = new DatabaseUtils(this);
        economyUtils = new EconomyUtils(this);
        LunaStockAPI = new LunaStockAPI(this, databaseUtils);

        if (!economyUtils.isEconomySetup()) {
            getLogger().info("Vault plugin not found! Disabling LunaStock plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("주식").setExecutor(new StockCommand(this));
        new StockUpdateTask(this).runTaskTimer(this, 0L, 20 * 300L);
        new NewsEventTask(this).scheduleNextEvent();
    }

    @Override
    public void onDisable() {
        databaseUtils.closeConnection();
    }

    public StockUtils getStockUtils() {
        return stockUtils;
    }

    public DatabaseUtils getDatabaseUtils() {
        return databaseUtils;
    }

    public EconomyUtils getEconomyUtils() {
        return economyUtils;
    }

    public LunaStockAPI getLunaStockAPI() {
        return LunaStockAPI;
    }
}
