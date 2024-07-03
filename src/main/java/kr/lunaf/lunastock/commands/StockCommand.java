package kr.lunaf.lunastock.commands;

import kr.lunaf.lunastock.API.LunaStockAPI;
import kr.lunaf.lunastock.LunaStock;
import kr.lunaf.lunastock.classes.Stock;
import kr.lunaf.lunastock.events.PlayerBuyStockEvent;
import kr.lunaf.lunastock.events.PlayerSellStockEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StockCommand implements CommandExecutor {
    private LunaStock plugin;
    private LunaStockAPI lunaStockAPI;

    public StockCommand(LunaStock plugin) {
        this.plugin = plugin;
        this.lunaStockAPI = plugin.getLunaStockAPI();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /주식 <buy|sell|list|info|history|returns> <stock_name> [amount]");
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
            case "returns":
                if (args.length < 2) {
                    player.sendMessage("Usage: /주식 returns <stock_name>");
                    return true;
                }
                stockReturns(player, args[1]);
                break;
            default:
                player.sendMessage("Usage: /주식 <buy|sell|list|info|history|returns> <stock_name> [amount]");
        }

        return true;
    }

    private void buyStock(Player player, String stockName, int amount) {
        Stock stock = lunaStockAPI.getStock(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        double cost = stock.getCurrentValue() * amount;
        if (plugin.getEconomyUtils().withdraw(player, cost)) {
            plugin.getDatabaseUtils().addPlayerStock(player.getUniqueId(), stock.getUuid(), amount, stock.getCurrentValue());
            player.sendMessage(stockName + " 주식을 " + amount + "주 구매하였습니다.");
            plugin.getServer().getPluginManager().callEvent(new PlayerBuyStockEvent(player, stock, amount, cost));
        } else {
            player.sendMessage("돈이 부족합니다.");
        }
    }

    private void sellStock(Player player, String stockName, int amount) {
        Stock stock = lunaStockAPI.getStock(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        int playerStockAmount = plugin.getDatabaseUtils().getPlayerStock(player.getUniqueId(), stock.getUuid());
        if (playerStockAmount < amount) {
            player.sendMessage("판매할 주식이 부족합니다.");
            return;
        }

        double earnings = stock.getCurrentValue() * amount;
        plugin.getEconomyUtils().deposit(player, earnings);
        plugin.getDatabaseUtils().removePlayerStock(player.getUniqueId(), stock.getUuid(), amount);
        player.sendMessage(stockName + " 주식을 " + amount + "주 판매하였습니다.");
        plugin.getServer().getPluginManager().callEvent(new PlayerSellStockEvent(player, stock, amount, earnings));
    }

    private void listStocks(Player player) {
        for (Stock stock : lunaStockAPI.getStockList()) {
            player.sendMessage(stock.getName() + ": " + stock.getCurrentValue() + "원 (최근 변동: " + stock.getLastChange() + ")");
        }
    }

    private void stockInfo(Player player, String stockName) {
        Stock stock = lunaStockAPI.getStock(stockName);
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
        Stock stock = lunaStockAPI.getStock(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        List<String> changes = lunaStockAPI.getStockHistory(stock, 5);
        player.sendMessage(stock.getName() + " 최근 변동 내역:");
        for (String change : changes) {
            player.sendMessage(change);
        }
    }

    private void stockReturns(Player player, String stockName) {
        Stock stock = lunaStockAPI.getStock(stockName);
        if (stock == null) {
            player.sendMessage("존재하지 않는 주식입니다.");
            return;
        }

        double returns = lunaStockAPI.calculateReturns(player, stock);
        player.sendMessage(stock.getName() + " 주식 수익률: " + returns + "%");
    }
}
