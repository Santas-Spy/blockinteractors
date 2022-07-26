package santas.spy.blockinteractor.newblocks;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface Interactor {
    public abstract void interact(ItemStack item);
    public abstract Location getLocation();
    public abstract String saveData();
}
