package santas.spy.blockinteractor.newblocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

import santas.spy.blockinteractor.BlockInteractor;

public class Placer implements Interactor {

    private Dispenser placer;

    public Placer(Dispenser placer) {
        this.placer = placer;
    }

    @Override
    public void interact(ItemStack dispensedItem)
    {
        Directional direction = (Directional)placer.getBlockData();
        Location location = placer.getLocation().add(direction.getFacing().getDirection());
        if (location.getBlock().getType().equals(Material.AIR)) {
            Material type = toPlace(dispensedItem);
            if (type != null) {
                location.getBlock().setType(type);
                BlockInteractor.debugMessage("Placing " + type, 2);
            } else {
                BlockInteractor.debugMessage("Valid item could not be found inside the placer", 2);
            }
        }
    }

    private Material toPlace(ItemStack dispensedItem)
    {
        boolean found = false;
        int i = 0;
        ItemStack[] inv = ((Dispenser)placer.getBlock().getState()).getInventory().getContents();
        Material type = null;

        while (!found && i < inv.length) {
            if (inv[i] != null && inv[i].getType().isBlock()) {
                found = true;
                type = inv[i].getType();
                BlockInteractor.debugMessage(i + ": " + type + " FOUND", 2);
                inv[i].setAmount(inv[i].getAmount() -1);
            } else {
                if (inv[i] == null) {
                    BlockInteractor.debugMessage(i + ": null", 2);
                } else {
                    BlockInteractor.debugMessage(i + ": " + inv[i].getType(), 2);
                }
                i++;
            }
        }

        if (!found) {
            if (dispensedItem.getType().isBlock()) {
                type = dispensedItem.getType();
                removeItem(dispensedItem);
                BlockInteractor.debugMessage("Dispensed Item was a valid item", 2);
            }
        }

        return type;
    }

    @Override
    public Location getLocation()
    {
        return placer.getLocation();
    }

    @Override
    public String saveData()
    {
        String data = "placer,";
        data += placer.getWorld().getName();
        data += ",";
        data += placer.getX();
        data += ",";
        data += placer.getY();
        data += ",";
        data += placer.getZ();
        return data;
    }

    private void removeItem(ItemStack toRemove)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BlockInteractor.PLUGIN, new Runnable() {
            @Override
            public void run() {
                ((Dispenser)placer.getBlock().getState()).getInventory().removeItem(toRemove);
            }
        }, 1);
    }
}
