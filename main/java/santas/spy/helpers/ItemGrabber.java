package santas.spy.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import santas.spy.BlockInteractor;
import santas.spy.config.Config;

public class ItemGrabber 
{
    public ItemStack grab(String type)
    {
        ItemStack item = null;
        String[] data = type.split(":");
        BlockInteractor.debugMessage("grabbing type " + type, 2);
        BlockInteractor.debugMessage("data[0] = " + data[0], 2);
        if (data[0].equals("CUSTOM")) {
            if (BlockInteractor.CRAFTING == null) {
                Bukkit.getLogger().warning("Using custom item " + data[1] + " but SantasCrafting is not installed");
                Config.getConfig().addError("Using custom item " + data[1] + " but SantasCrafting is not installed", type);
            } else {
                if (ItemValidater.isNameOfCustomItem(data[1])) {
                    item = ItemValidater.giveCustomItem(data[1]);
                }
            }
        } else {
            item = new ItemStack(Material.getMaterial(data[0].toUpperCase()));
        }
        return item;
    }
}
