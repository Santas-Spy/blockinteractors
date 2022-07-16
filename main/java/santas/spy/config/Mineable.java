package santas.spy.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Mineable {
    private Material type;
    private List<Fuel> fuels;
    private ItemStack result;

    public Mineable(Material type, List<Fuel> fuels, ItemStack result)
    {
        this.type = type;
        this.fuels = fuels;
        this.result = result;
    }

    public Material type()
    {
        return type;
    }

    public List<Fuel> fuels()
    {
        return fuels;
    }

    public ItemStack result()
    {
        return result;
    }
}
