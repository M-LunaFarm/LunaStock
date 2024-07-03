package kr.lunaf.lunastock.classes;

import java.util.List;
import java.util.UUID;

public class Stock {
    private UUID uuid;
    private String name;
    private int firstAmount;
    private int maxAmount;
    private double currentValue;
    private List<String> goodNews;
    private List<String> badNews;
    private double lastChange;

    public Stock(UUID uuid, String name, int firstAmount, int maxAmount, double currentValue, List<String> goodNews, List<String> badNews) {
        this.uuid = uuid;
        this.name = name;
        this.firstAmount = firstAmount;
        this.maxAmount = maxAmount;
        this.currentValue = currentValue;
        this.goodNews = goodNews;
        this.badNews = badNews;
        this.lastChange = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getFirstAmount() {
        return firstAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public List<String> getGoodNews() {
        return goodNews;
    }

    public List<String> getBadNews() {
        return badNews;
    }

    public double getLastChange() {
        return lastChange;
    }

    public void setLastChange(double lastChange) {
        this.lastChange = lastChange;
    }
}
