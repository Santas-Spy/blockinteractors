package santas.spy.blockinteractor.config;

import org.bukkit.inventory.ItemStack;

public class Fuel {
    private ItemStack fuel;
    private int timer;
    private String name;

    public Fuel(ItemStack fuel, int timer, String name) {
        this.fuel = fuel;
        this.timer = timer;
        this.name = name;
    }

    public ItemStack fuel()
    {
        return fuel;
    }

    public int timer()
    {
        return timer;
    }

    public String name()
    {
        return name;
    }
}
