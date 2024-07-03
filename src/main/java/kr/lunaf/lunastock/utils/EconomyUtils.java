package kr.lunaf.lunastock.utils;

import kr.lunaf.lunastock.LunaStock;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyUtils {
    private Economy economy;
    private boolean economySetup;

    public EconomyUtils(LunaStock plugin) {
        economySetup = setupEconomy();
        if (!economySetup) {
            plugin.getLogger().info("Vault plugin not found! Disabling StockMarket plugin.");
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean isEconomySetup() {
        return economySetup;
    }

    public boolean withdraw(Player player, double amount) {
        if (economy.getBalance(player) >= amount) {
            economy.withdrawPlayer(player, amount);
            return true;
        }
        return false;
    }

    public void deposit(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }
}
