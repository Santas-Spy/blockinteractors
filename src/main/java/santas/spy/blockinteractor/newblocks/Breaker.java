package santas.spy.blockinteractor.newblocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import santas.spy.blockinteractor.BlockInteractor;
import santas.spy.blockinteractor.config.Config;

public class Breaker implements Interactor {
    Dispenser breaker;
    Config config;
    public static final ItemStack fillerItem = getFiller();

    public Breaker(Dispenser breaker)
    {
        this.breaker = breaker;
        config = Config.getConfig();
        BlockInteractor.debugMessage("Putting filler item in new breaker", 2);
        if (breaker.getInventory().isEmpty() && config.fillEmptyBreaker()) {
            breaker.getInventory().addItem(fillerItem);
        }
    }

    /**
     * Breaks the block infront of the dispenser
     * */
    @Override
    public void interact()
    {
        BlockInteractor.debugMessage("Breaker is trying to break", 2);
        Directional direction = (Directional)breaker.getBlockData();
        Location location = breaker.getLocation().add(direction.getFacing().getDirection());
        if (!location.getBlock().getType().equals(Material.AIR)) {
            BlockInteractor.debugMessage("Breaker breaking block type " + location.getBlock().getType(), 2);
            location.getBlock().breakNaturally();
        } else {
            BlockInteractor.debugMessage("Breaker tried to break block but block was AIR", 2);
        }
    }

    @Override
    public Location getLocation()
    {
        return breaker.getLocation();
    }

    @Override
    public String saveData()
    {   
        String data = "breaker,";
        data += breaker.getWorld().getName();
        data += ",";
        data += breaker.getX();
        data += ",";
        data += breaker.getY();
        data += ",";
        data += breaker.getZ();
        return data;
    }

    private static ItemStack getFiller()
    {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Breakers need one item in their inv to work");
        List<String> lore;
        if (meta.hasLore()) { 
            lore = meta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(0, "If you remove this item this breaker may stop working");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
