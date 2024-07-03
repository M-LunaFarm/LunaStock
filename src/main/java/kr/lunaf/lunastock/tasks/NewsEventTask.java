package kr.lunaf.lunastock.tasks;

import kr.lunaf.lunastock.LunaStock;
import kr.lunaf.lunastock.classes.Stock;
import kr.lunaf.lunastock.utils.StockUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.Random;

public class NewsEventTask extends BukkitRunnable {
    private LunaStock plugin;
    private Random random;

    public NewsEventTask(LunaStock plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @Override
    public void run() {
        StockUtils stockUtils = plugin.getStockUtils();
        List<Stock> stocks = stockUtils.getStocks();

        for (Stock stock : stocks) {
            List<String> goodNews = stock.getGoodNews();
            List<String> badNews = stock.getBadNews();

            boolean isGoodNews = random.nextBoolean();
            String news = isGoodNews ? getRandomElement(goodNews) : getRandomElement(badNews);
            double impact = isGoodNews ? 0.01 : -0.01;

            if (news != null) {
                Bukkit.broadcastMessage("뉴스 속보: " + news + " [" + stock.getName() + "]");
                stockUtils.updateStockValue(stock, impact);
            }
        }
        scheduleNextEvent();
    }

    private String getRandomElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    public void scheduleNextEvent() {
        int delay = (15 * 60 + random.nextInt(5 * 60)) * 20;
        this.runTaskLater(plugin, delay);
    }
}
