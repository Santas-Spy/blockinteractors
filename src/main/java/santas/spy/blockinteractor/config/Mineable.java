package santas.spy.blockinteractor.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Mineable {
    private Material type;
    private List<Fuel> fuels;
    private ItemStack result;
    private int usesRemaining;

    public Mineable(Material type, List<Fuel> fuels, ItemStack result, int usesRemaining)
    {
        this.type = type;
        this.fuels = fuels;
        this.result = result;
        this.usesRemaining = usesRemaining;
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

    public boolean use()
    {
        boolean done = false;
        if (usesRemaining >= 0) {
            if (usesRemaining == 0) {
                done = true;
            } else {
                usesRemaining--;
            }
        }

        return done;
    }
}
