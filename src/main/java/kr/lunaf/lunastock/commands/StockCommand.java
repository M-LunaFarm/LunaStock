package kr.lunaf.lunastock.commands;


import kr.lunaf.lunastock.LunaStock;

import kr.lunaf.lunastock.classes.Stock;

import kr.lunaf.lunastock.utils.DatabaseUtils;
import kr.lunaf.lunastock.utils.EconomyUtils;
import kr.lunaf.lunastock.utils.StockUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class StockCommand implements CommandExecutor {
    private LunaStock plugin;
    private StockUtils stockUtils;
    private DatabaseUtils databaseUtils;
    private EconomyUtils economyUtils;

    public StockCommand(LunaStock plugin) {
        this.plugin = plugin;
        this.stockUtils = plugin.getStockUtils();
        this.databaseUtils = plugin.getDatabaseUtils();
        this.economyUtils = plugin.getEconomyUtils();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /주식 <buy|sell|list|info|history> <stock_name> [amount]");
            return true;
        }

        String action = args[0];

        switch (action) {
            case "buy":
                if (args.length < 3) {
                    player.sendMessage("Usage: /주식 buy <stock_name> <amount>");
                    return true;
                }
                buyStock(player, args[1], Integer.parseInt(args[2]));
                break;
            case "sell":
                if (args.length < 3) {
                    player.sendMessage("Usage: /주식 sell <stock_name> <amount>");
                    return true;
                }
                sellStock(player, args[1], Integer.parseInt(args[2]));
                break;
            case "list":
                listStocks(player);
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage("Usage: /주식 info <stock_name>");
                    return true;
                }
                stockInfo(player, args[1]);
                break;
            case "history":
                if (args.length < 2) {
                    player.sendMessage("Usage: /주식 history <stock_name>");
                    return true;
                }
                stockHistory(player, args[1]);
                break;
            default:
                player.sendMessage("Usage: /주식 <buy|sell|list|info|history> <stock_name> [amount]");
        }

        return true;
    }

    private void buyStock(Player player, String stockName, int amount) {
        Stock stock = stockUtils.getStockByName(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        double cost = stock.getCurrentValue() * amount;
        if (economyUtils.withdraw(player, cost)) {
            databaseUtils.addPlayerStock(player.getUniqueId(), stock.getUuid(), amount);
            player.sendMessage(stockName + " 주식을 " + amount + "주 구매하였습니다.");
        } else {
            player.sendMessage("돈이 부족합니다.");
        }
    }

    private void sellStock(Player player, String stockName, int amount) {
        Stock stock = stockUtils.getStockByName(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        int playerStockAmount = databaseUtils.getPlayerStock(player.getUniqueId(), stock.getUuid());
        if (playerStockAmount < amount) {
            player.sendMessage("판매할 주식이 부족합니다.");
            return;
        }

        double earnings = stock.getCurrentValue() * amount;
        economyUtils.deposit(player, earnings);
        databaseUtils.removePlayerStock(player.getUniqueId(), stock.getUuid(), amount);
        player.sendMessage(stockName + " 주식을 " + amount + "주 판매하였습니다.");
    }

    private void listStocks(Player player) {
        for (Stock stock : stockUtils.getStocks()) {
            player.sendMessage(stock.getName() + ": " + stock.getCurrentValue() + "원 (최근 변동: " + stock.getLastChange() + ")");
        }
    }

    private void stockInfo(Player player, String stockName) {
        Stock stock = stockUtils.getStockByName(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        player.sendMessage(stock.getName() + ":");
        player.sendMessage(" - 최초 수량: " + stock.getFirstAmount());
        player.sendMessage(" - 최대 수량: " + stock.getMaxAmount());
        player.sendMessage(" - 현재 가치: " + stock.getCurrentValue() + "원");
        player.sendMessage(" - 최근 변동: " + stock.getLastChange());
    }

    private void stockHistory(Player player, String stockName) {
        Stock stock = stockUtils.getStockByName(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        List<String> changes = databaseUtils.getRecentStockChanges(stock.getUuid(), 5);
        player.sendMessage(stock.getName() + " 최근 변동 내역:");
        for (String change : changes) {
            player.sendMessage(change);
        }
    }
}
