package kr.lunaf.lunastock.events;

import kr.lunaf.lunastock.classes.Stock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBuyStockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Stock stock;
    private int amount;
    private double cost;

    public PlayerBuyStockEvent(Player player, Stock stock, int amount, double cost) {
        this.player = player;
        this.stock = stock;
        this.amount = amount;
        this.cost = cost;
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

    public double getCost() {
        return cost;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
