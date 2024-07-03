package kr.lunaf.lunastock.utils;

import kr.lunaf.lunastock.LunaStock;
import kr.lunaf.lunastock.classes.Stock;
import kr.lunaf.lunastock.events.StockPriceChangeEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.*;

public class StockUtils {
    private LunaStock plugin;
    private Map<UUID, Stock> stocks;
    private DatabaseUtils databaseUtils;

    public StockUtils(LunaStock plugin) {
        this.plugin = plugin;
        this.stocks = new HashMap<>();
        this.databaseUtils = plugin.getDatabaseUtils();
        loadStocks();
    }

    public void loadStocks() {
        FileConfiguration config = plugin.getConfig();
        for (String key : config.getConfigurationSection("stocks").getKeys(false)) {
            String name = key;
            UUID uuid = config.contains("stocks." + key + ".uuid")
                    ? UUID.fromString(config.getString("stocks." + key + ".uuid"))
                    : UUID.randomUUID();
            if (!config.contains("stocks." + key + ".uuid")) {
                config.set("stocks." + key + ".uuid", uuid.toString());
            }
            int firstAmount = config.getInt("stocks." + key + ".first_amount");
            int maxAmount = config.getInt("stocks." + key + ".max_amount");
            double currentValue = config.getDouble("stocks." + key + ".current_value");

            List<String> goodNews = config.getStringList("stocks." + key + ".good_news");
            List<String> badNews = config.getStringList("stocks." + key + ".bad_news");

            stocks.put(uuid, new Stock(uuid, name, firstAmount, maxAmount, currentValue, goodNews, badNews));
        }
        plugin.saveConfig();
    }

    public Stock getStock(String name) {
        for (Stock stock : stocks.values()) {
            if (stock.getName().equalsIgnoreCase(name)) {
                return stock;
            }
        }
        return null;
    }

    public Stock getStock(UUID uuid) {
        return stocks.get(uuid);
    }

    public Integer getPlayerStock(Player player, Stock stock) {
        return databaseUtils.getPlayerStock(player.getUniqueId(), stock.getUuid());
    }

    public List<Stock> getPlayerStockList(Player player) {
        return databaseUtils.getPlayerStockList(player.getUniqueId());
    }

    public List<String> getStockHistory(Stock stock, Integer limit) {
        return databaseUtils.getRecentStockChanges(stock.getUuid(), limit);
    }

    public List<String> getStockPlayerHistory(Player player, Stock stock, Integer limit) {
        return databaseUtils.getPlayerStockHistory(player.getUniqueId(), stock.getUuid(), limit);
    }

    public List<Stock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public void updateStockValue(Stock stock, double change) {
        double oldValue = stock.getCurrentValue();
        double newValue = stock.getCurrentValue() * (1 + change);
        newValue = Math.max(newValue, 0.01);
        stock.setCurrentValue(newValue);
        stock.setLastChange(change);
        saveStock(stock);

        databaseUtils.addStockChange(stock.getUuid(), change);

        plugin.getServer().getPluginManager().callEvent(new StockPriceChangeEvent(stock, oldValue, newValue));
    }

    public void saveStock(Stock stock) {
        FileConfiguration config = plugin.getConfig();
        config.set("stocks." + stock.getName() + ".current_value", stock.getCurrentValue());
        plugin.saveConfig();
    }
}