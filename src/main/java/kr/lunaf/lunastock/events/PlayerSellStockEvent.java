package kr.lunaf.lunastock.events;

import kr.lunaf.lunastock.classes.Stock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSellStockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Stock stock;
    private int amount;
    private double revenue;

    public PlayerSellStockEvent(Player player, Stock stock, int amount, double revenue) {
        this.player = player;
        this.stock = stock;
        this.amount = amount;
        this.revenue = revenue;
    }

    public Player getPlayer() {
        return player;
    }

    public Stock getStock() {
        return stock;
    }

    public int getAmount() {
        return amount;
    }

    public double getRevenue() {
        return revenue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
