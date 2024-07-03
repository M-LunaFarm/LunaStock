package kr.lunaf.lunastock.tasks;

import kr.lunaf.lunastock.LunaStock;
import kr.lunaf.lunastock.classes.Stock;
import kr.lunaf.lunastock.utils.DatabaseUtils;
import kr.lunaf.lunastock.utils.StockUtils;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StockUpdateTask extends BukkitRunnable {
    private LunaStock plugin;
    private Random random;

    public StockUpdateTask(LunaStock plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @Override
    public void run() {
        StockUtils stockUtils = plugin.getStockUtils();
        DatabaseUtils databaseUtils = plugin.getDatabaseUtils();

        for (Stock stock : stockUtils.getStocks()) {
            int totalPlayerStock = databaseUtils.getTotalStock(stock.getUuid());
            double averageStock = stock.getFirstAmount() / stockUtils.getStocks().size();
            double stockRatio = (double) totalPlayerStock / averageStock;

            double baseChange = (random.nextDouble() * 0.04) - 0.02;
            double adjustedChange = baseChange * (stockRatio > 1 ? -stockRatio : stockRatio);
            adjustedChange = Math.max(Math.min(adjustedChange, 0.02), -0.02);

            stockUtils.updateStockValue(stock, adjustedChange);
        }
    }
}
