package kr.lunaf.lunastock.events;

import kr.lunaf.lunastock.classes.Stock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StockPriceChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Stock stock;
    private double oldValue;
    private double newValue;

    public StockPriceChangeEvent(Stock stock, double oldValue, double newValue) {
        this.stock = stock;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Stock getStock() {
        return stock;
    }

    public double getOldValue() {
        return oldValue;
    }

    public double getNewValue() {
        return newValue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
