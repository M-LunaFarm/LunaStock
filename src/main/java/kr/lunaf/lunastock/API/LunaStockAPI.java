package kr.lunaf.lunastock.API;

import kr.lunaf.lunastock.LunaStock;
import kr.lunaf.lunastock.classes.Stock;
import kr.lunaf.lunastock.utils.DatabaseUtils;
import kr.lunaf.lunastock.utils.StockUtils;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public class LunaStockAPI {
    private LunaStock plugin;
    private DatabaseUtils databaseUtils;

    public LunaStockAPI(LunaStock plugin, DatabaseUtils databaseUtils) {
        this.plugin = plugin;
        this.databaseUtils = databaseUtils;
    }

    public Stock getStock(String name) {
        return plugin.getStockUtils().getStock(name);
    }

    public Stock getStock(UUID uuid) {
        return plugin.getStockUtils().getStock(uuid);
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

    public List<Stock> getStockList() {
        return plugin.getStockUtils().getStocks();
    }

    public double getPlayerStockPurchasePrice(Player player, Stock stock) {
        return databaseUtils.getPlayerStockPurchasePrice(player.getUniqueId(), stock.getUuid());
    }

    public double getPlayerTotalPurchasePrice(Player player, Stock stock) {
        return databaseUtils.getPlayerTotalPurchasePrice(player.getUniqueId(), stock.getUuid());
    }

    public double calculateReturns(Player player, Stock stock) {
        int playerStockAmount = getPlayerStock(player, stock);
        double totalPurchasePrice = getPlayerTotalPurchasePrice(player, stock);
        double currentValue = stock.getCurrentValue() * playerStockAmount;
        if (totalPurchasePrice == 0) return 0;
        return ((currentValue - totalPurchasePrice) / totalPurchasePrice) * 100;
    }
}
