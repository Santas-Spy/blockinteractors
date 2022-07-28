package santas.spy.blockinteractor.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MineableBlueprint {
    private Material type;
    private List<Fuel> fuels;
    private ItemStack result;
    private int uses;

    public MineableBlueprint(Material type, List<Fuel> fuels, ItemStack result, int uses)
    {
        this.type = type;
        this.fuels = fuels;
        this.result = result;
        this.uses = uses;
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

    public int uses()
    {
        return uses;
    }
}